package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XmlParseException;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.ParallelDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.definitions.TransitionDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults.CompilationError;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** This class takes IA SFC Charts and creates objects to represent its structure in a form 
 *  that is convenient for us.
 *  
 *  Note that since we load project resources, changes that have not been committed to the project will
 *  not be seen.
 *  
 */
public class ChartStructureCompiler {
	private static String TAG = "ChartStructureCompiler";
	private final LoggerEx log;
	// constants:
	public static final String NAME_PROPERTY = "name";
	public static final String CHART_RESOURCE_TYPE="charts";
	public static final String CHART_MODULE = "com.inductiveautomation.sfc";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	public static final String ENCLOSING_FACTORY_ID = EnclosingStepProperties.FACTORY_ID;
	//static final UUID ROOT_FOLDER_ID = ChartUIModel.ROOT_FOLDER;	travis.sareault 20211114 - source field is removed, and this seems to be unused.
	private final Project project;
	private final StepRegistry stepRegistry;
	
	// intermediate structures:
	private final Map<Long,ChartModelInfo> modelInfoByResourceId;
	private final Map<String,ChartModelInfo> modelInfoByChartPath; // Reverse directory
	private final Map<String,StepStructure> stepsById;
	private final Map<String,StepStructure> stepsByKey;            // Reverse lookup by chartId,stepname

	public ChartStructureCompiler(Project proj, StepRegistry stepRegistry) {
		this.project = proj;
		this.stepRegistry = stepRegistry;
		this.modelInfoByResourceId = new HashMap<>();
		this.modelInfoByChartPath = new HashMap<>();
		this.stepsById = new HashMap<>();
		this.stepsByKey = new HashMap<>();
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}


	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public void compile() {
		stepsById.clear();
		stepsByKey.clear();
		modelInfoByResourceId.clear();
		modelInfoByChartPath.clear();
		
		log.infof("*** In compile ***");
		if(loadModels()) {
			log.infof("compiling charts...");
			compileCharts();  // do the IA chart compilation
		}
		else {
			log.error("Errors compiling SFC chart models");
			//return ;  // if we can't load the resources we can't do much...
		}
		
		linkParents();
	}
	
	public ChartModelInfo  getChartInformation(long resourceId) { return modelInfoByResourceId.get(resourceId); }
	public StepStructure   getStepInformation(String stepId) { return stepsById.get(stepId); }
	public StepStructure   getStepInformation(String chartPath,String stepName) { 
		String key = makeKey(chartPath,stepName);
		return stepsByKey.get(key); 
	}
	
	public Collection<StepStructure> getSteps() {
		return stepsById.values();
	}
		
	// ===================================== Private Helper Methods =========================
	// Deserialize all SFC charts from the global project.
	private boolean loadModels() {
		List<ProjectResource> resources = project.getResources();
		boolean success = true;
		for(ProjectResource res:resources) {
			if( res.getResourceType().getModuleId().equals(CHART_MODULE) &&
				res.getResourceType().getTypeId().equals(CHART_RESOURCE_TYPE)) {
				String path = res.getFolderPath();
				try {
					
					byte[] chartResourceData = res.getData();
					
					if(chartResourceData == null) {
						log.warnf("loadModels: Chart %s has no byte data.", path);
						return false;
					}
									
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
					ChartUIModel uiModel = ChartUIModel.fromXml(xmlInput, stepRegistry );
					ChartModelInfo info = new ChartModelInfo(uiModel,res,path);
					modelInfoByResourceId.put((long)res.getResourceId().hashCode(),info);
					modelInfoByChartPath.put(path,info);
					log.debugf("loadModels: found resource %s (%d)",path,res.getResourceId());
				}
				catch(IOException ioe) {
					log.errorf("loadModels: Exception reading %s:%d (%s)",path,res.getResourceId(),ioe.getLocalizedMessage());
					success = false;
				}
				catch(XmlParseException xpe) {
					log.errorf("loadModels: Error deserializing %s:%d (%s)",path,res.getResourceId(),xpe.getLocalizedMessage());
					success = false;
				}
			}
		}
		return success;
	}

	// Do the IA chart compilation and create corresponding chart/step elements in our structure.
	private boolean compileCharts() {
		for(ChartModelInfo modelInfo: modelInfoByResourceId.values() ) {
			try {
				ChartCompiler chartCompiler = new ChartCompiler(modelInfo.uiModel, stepRegistry);
				ChartCompilationResults ccr = chartCompiler.compile();
				if(ccr.isSuccessful()) {
					modelInfo.chartDefinition = ccr.getChartDefinition();
					ChartStructure newChart = new ChartStructure(modelInfo.resource.getResourceName(),modelInfo.resource.getResourceId(),modelInfo.resource.getFolderPath());
					modelInfo.chartStructure = newChart;
					Set<UUID> seen = new HashSet<UUID>();
					StepStructure.parallelCount = 0;
					StepStructure.transitionCount = 0;
					for(ElementDefinition def: ccr.getRootDefinitions()) {
						createSteps(newChart, null, def, seen);
					}
				}
				else {
					for(CompilationError ce:ccr.getErrors()) {
						log.warnf("%s.compileCharts: Chart %s has compilation error (%s)",TAG,modelInfo.chartPath,ce.getMessage());
					}				
				}
			}
			catch(Throwable t) {
				log.errorf("%s.compileCharts: Unexpected exception compiling chart %s",TAG,modelInfo.chartPath);				
			}
		} 
		return true;
	}
	
	// Recurse through the "next elements" relation to create step structure. 
	private void createSteps(ChartStructure chart, StepStructure previousStep, ElementDefinition elDef, Set<UUID> seen) {
		if(seen.contains(elDef.getElementId())) {
			return; // avoid infinite loop
		}
		else {
			seen.add(elDef.getElementId());
		}
		ElementDefinition.ElementType elementType = elDef.getElementType();
		if( elementType == ElementDefinition.ElementType.Step ) {
			StepDefinition stepDef = (StepDefinition)elDef;
			StepStructure thisStep = getOrCreateStep(chart,previousStep, stepDef);
			previousStep = thisStep;
		}
		else if( elementType == ElementDefinition.ElementType.Transition) {
			TransitionDefinition transDef = (TransitionDefinition)elDef;
			StepStructure thisStep = getOrCreateStep(chart,previousStep,transDef);
			previousStep = thisStep;
		}
		else if( elementType == ElementDefinition.ElementType.Parallel) {
			ParallelDefinition pDef = (ParallelDefinition)elDef;
			StepStructure thisStep = getOrCreateStep(chart,previousStep,pDef,seen);
			previousStep = thisStep;
		}
		for(ElementDefinition nextDef: elDef.getNextElements()) {
			createSteps(chart, previousStep, nextDef, seen);
		}
	}


	// Return the step structure for the given id, creating if it doesn't exist. 
	private StepStructure getOrCreateStep(ChartStructure chart, StepStructure previousStep, StepDefinition stepDef ) {
		String stepId = stepDef.getElementId().toString();
		if(!stepsById.containsKey(stepId)) {
			StepStructure newStep = new StepStructure(chart, previousStep, stepDef); 
			chart.addStep(newStep);
			stepsById.put(stepId, newStep);
			String key = makeKey(chart.getPath(),newStep.getName());
			stepsByKey.put(key, newStep);
		}
		return stepsById.get(stepId);			
	}
	
	// Return the step structure for the given id, creating if it doesn't exist. 
	private StepStructure getOrCreateStep(ChartStructure chart, StepStructure previousStep, ParallelDefinition pDef,Set<UUID> seen) {
		String stepId = pDef.getElementId().toString();
		if(!stepsById.containsKey(stepId)) {
			StepStructure newStep = new StepStructure(chart, previousStep, pDef); 
			chart.addStep(newStep);
			stepsById.put(stepId, newStep);
			String key = makeKey(chart.getPath(),newStep.getName());
			stepsByKey.put(key, newStep);
			previousStep = newStep;
			for(ElementDefinition nextDef: pDef.getNextElements()) {
				createSteps(chart, previousStep, nextDef, seen);
			}
		}
		return stepsById.get(stepId);			
	}
	
	// Return the step structure for the given id, creating if it doesn't exist. 
	private StepStructure getOrCreateStep(ChartStructure chart, StepStructure previousStep, TransitionDefinition transDef) {
		String stepId = transDef.getElementId().toString();
		if(!stepsById.containsKey(stepId)) {
			StepStructure newStep = new StepStructure(chart, previousStep, transDef); 
			chart.addStep(newStep);
			stepsById.put(stepId, newStep);
			String key = makeKey(chart.getPath(),newStep.getName());
			stepsByKey.put(key, newStep);
		}
		return stepsById.get(stepId);			
	}

	//Create the relationships for chart inclusion.
	private void linkParents() {
		log.tracef("%s.linkParents: .....",TAG);

		for(ChartModelInfo modelInfo: modelInfoByResourceId.values() ) {
			ChartStructure chartStruct = modelInfo.chartStructure;  // Null if compile failed
			if( chartStruct!=null ) {
				for(StepStructure step: chartStruct.getSteps()) {
					if(step.isEnclosure()) {
						String chartPath = step.getEnclosedChartName();
						ChartModelInfo chartInfo = modelInfoByChartPath.get(chartPath);
						if(chartInfo != null) {
							ChartStructure enclosedChart = chartInfo.chartStructure;
							if(enclosedChart != null) {
								step.setEnclosedChart(enclosedChart);
								enclosedChart.addParent(step);
								if( log.isInfoEnabled()) {
									ChartModelInfo stepInfo = modelInfoByResourceId.get(enclosedChart.getResourceId());
									log.tracef("%s.linkParents: %s is a parent of %s",TAG,modelInfo.chartPath,stepInfo.chartPath);
								}
							}
							else {
								log.tracef("%s.linkParents: Enclosed chart %s not found for step %s in chart %s",
										TAG,step.getEnclosedChartName(),step.getName(),modelInfo.chartStructure.getName() );
							}
						}
						else {
							log.errorf("No info found for chart path %s", chartPath);
						}
					}
				}	
			}
							
		}
	}
	
	/**
	 * The is the key to lookup a step structure
	 * @param chartId
	 * @param stepName
	 * @return a map key
	 */
	private String makeKey(String chartPath,String stepName) {
		StringBuilder sb = new StringBuilder();
		sb.append(chartPath);
		sb.append(":");
		sb.append(stepName);
		return sb.toString();
	}
	
	public List<String> getMatchingCharts(String regex) {
		List<String> matchingCharts = new ArrayList<String>();
		List<ProjectResource> resources = project.getResources();
		for(ProjectResource res:resources) {
			if(res.getResourceType().getModuleId().equals(CHART_MODULE) &&
					res.getResourceType().getTypeId().equals(CHART_RESOURCE_TYPE)) {
				String path = res.getFolderPath();
				if(path.matches(regex)) {
					matchingCharts.add(path);
				}
			}
		}
		return matchingCharts;
	}

}
