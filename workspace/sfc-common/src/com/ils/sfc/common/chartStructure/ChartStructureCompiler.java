package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XMLParseException;
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
	public static final String FACTORY_ID_PROPERTY = "factory-id";
	public static final String CHART_PATH_PROPERTY = "chart-path";
	public static final String NAME_PROPERTY = "name";
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	public static final String ENCLOSING_FACTORY_ID = EnclosingStepProperties.FACTORY_ID;
	static final UUID ROOT_FOLDER_ID = ChartUIModel.ROOT_FOLDER;	
	private final DesignerContext context;
	private final Project globalProject;
	private final StepRegistry stepRegistry;
	
	// intermediate structures:
	private final Map<Long,ChartModelInfo> modelInfoByResourceId;
	private final Map<String,ChartModelInfo> modelInfoByChartPath;     // Reverse directory
	private final Map<String,StepStructure> stepsById;

	public ChartStructureCompiler(DesignerContext ctx, StepRegistry stepRegistry) {
		this.context = ctx;
		this.globalProject = context.getGlobalProject().getProject();
		this.stepRegistry = stepRegistry;
		this.modelInfoByResourceId = new HashMap<>();
		this.modelInfoByChartPath = new HashMap<>();
		this.stepsById = new HashMap<>();
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}


	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public void compile() {
		if(loadModels()) {
			compileCharts();  // do the IA chart compilation
		}
		else {
			log.error("Could not compile SFC chart models");
			return ;  // if we can't load the resources we can't do much...
		}
		
		linkParents();
	}
	
	public ChartModelInfo  getChartInformation(long resourceId) { return modelInfoByResourceId.get(new Long(resourceId)); }
	public StepStructure   getStepInformation(String stepId) { return stepsById.get(stepId); }
	
	public Collection<StepStructure> getSteps() {
		return stepsById.values();
	}
	
	// ===================================== Private Helper Methods =========================
	// Deserialize all SFC charts from the global project.
	private boolean loadModels() {
		List<ProjectResource> resources = globalProject.getResources();		
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				try {
					byte[] chartResourceData = res.getData();					
					//IlsSfcCommonUtils.printResource(data);					
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
					ChartUIModel uiModel = ChartUIModel.fromXML(xmlInput, stepRegistry );
					String path = context.getGlobalProject().getProject().getFolderPath(res.getResourceId());
					ChartModelInfo info = new ChartModelInfo(uiModel,res,path);
					modelInfoByResourceId.put(new Long(res.getResourceId()),info);
					modelInfoByChartPath.put(path,info);
					log.infof("%s.loadModels: found resource %s (%d)",TAG,path,res.getResourceId());
				}
				catch(IOException ioe) {
					log.errorf("%s.loadModels: IO exception deserializing chart (%s)",TAG,ioe.getLocalizedMessage());
					return false;
				}
				catch(XMLParseException xpe) {
					log.errorf("%s.loadModels: XML error deserializing chart (%s)",TAG,xpe.getLocalizedMessage());
					return false;
				}
			}
		}
		return true;
	}

	// Do the IA chart compilation and create corresponding chart/step elements in our structure.
	private boolean compileCharts() {
		for(ChartModelInfo modelInfo: modelInfoByResourceId.values() ) {
			ChartCompiler chartCompiler = new ChartCompiler(modelInfo.uiModel, stepRegistry);
			ChartCompilationResults ccr = chartCompiler.compile();
			if(ccr.isSuccessful() ) {
				modelInfo.chartDefinition = ccr.getChartDefinition();
				ChartStructure newChart = new ChartStructure(modelInfo.resource.getName(),modelInfo.resource.getResourceId());
				modelInfo.chartStructure = newChart;
				Set<UUID> seen = new HashSet<UUID>();
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
			if(newStep.isEnclosure()) {
				String enclosedChartName =  (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(), CHART_PATH_PROPERTY);
				newStep.setEnclosedChartName(enclosedChartName);
			}
			stepsById.put(stepId, newStep);
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
		}
		return stepsById.get(stepId);			
	}

	//Create the relationships for chart inclusion.
	private void linkParents() {
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
								enclosedChart.addParent(modelInfo.chartStructure, step);
							}
							else {
								log.infof("%s.linkParents: Enclosed chart %s not found for step %s in chart %s",
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

	// Holder for parent structures so that we walk the tree
	public static class Parent {
		ChartStructure chart;
		StepStructure step;
		
		public Parent(ChartStructure chart, StepStructure step) {
			this.chart = chart;
			this.step = step;
		}
	}
}
