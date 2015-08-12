package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
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
					createSteps(newChart, def, null, seen);
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
	private void createSteps(ChartStructure chart, ElementDefinition elDef, StepStructure previousStep, Set<UUID> seen) {
		if(seen.contains(elDef.getElementId())) {
			return; // avoid infinite loop
		}
		else {
			seen.add(elDef.getElementId());
		}
		if(elDef.getElementType() == ElementDefinition.ElementType.Step) {
			StepDefinition stepDef = (StepDefinition)elDef;
			StepStructure thisStep = getOrCreateStep(chart, stepDef, previousStep);
			// by only setting "previousStep" if the object we are looking at is a step,
			// we can skip over intervening non-step objects like transitions...
			previousStep = thisStep;
		}
		for(ElementDefinition nextDef: elDef.getNextElements()) {
			createSteps(chart, nextDef, previousStep, seen);
		}
	}

	// Return the step structure for the given id, creating if it doesn't exist. 
	private StepStructure getOrCreateStep(ChartStructure chart, StepDefinition stepDef, StepStructure previousStep) {
		String stepId = stepDef.getElementId().toString();
		if(!stepsById.containsKey(stepId)) {
			String stepName = (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(), NAME_PROPERTY);
			boolean isEnclosingStep = stepDef.getFactoryId().equals(ENCLOSING_FACTORY_ID);
			StepStructure newStep = new StepStructure(chart, stepId.toString(), stepDef.getFactoryId(), 
					stepName, previousStep, isEnclosingStep);
			chart.addStep(newStep);
			if(newStep.isEnclosingStep()) {
				String enclosedChartName =  (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(), CHART_PATH_PROPERTY);
				newStep.setEnclosedChartName(enclosedChartName);
			}
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
					if(step.isEnclosingStep()) {
						String path = step.getEnclosedChartName();
						ChartStructure enclosedChart = modelInfoByChartPath.get(path).chartStructure;
						if(enclosedChart != null) {
							step.setEnclosedChart(enclosedChart);
							enclosedChart.addParent(modelInfo.chartStructure, step);
						}
						else {
							log.infof("%s.linkParents: Enclosed chart %s not found for step %s in chart %s",
									TAG,step.getEnclosedChartName(),step.getName(),modelInfo.chartStructure.getName() );
						}
					}
				}	
			}
							
		}
	}

	// ================================ Nested Classes ===============================
	// A helper class that holds useful information about a chart. Has "package" access.
	static class ChartModelInfo {
		final ChartUIModel uiModel;
		final ProjectResource resource;
		final String chartPath;
		ChartDefinition chartDefinition;
		ChartStructure chartStructure   = null;
		
		public ChartModelInfo(ChartUIModel uiModel, ProjectResource resource,String path) {
			super();
			this.uiModel = uiModel;
			this.resource = resource;
			this.chartPath = path;
			this.chartDefinition = null;
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
	
	/** 
	 * A class to hold the relationship structure of a single SFC chart in a way that is 
	 *  convenient for our purposes. 
	 */
	public static class ChartStructure {

		// Enclosing Steps in another chart that contain this chart:
		private List<Parent> parents = new ArrayList<Parent>();
		private final List<StepStructure> steps; 	// the steps in this chart
		private final String name;                  // the name (path) of this chart
		private final long resourceId;
		
		public ChartStructure(String name,long resid) {
			this.name = name;
			this.resourceId = resid;
			this.steps= new ArrayList<StepStructure>();
		}
		
		
		public void addParent(ChartStructure chart, StepStructure step) {
			parents.add(new Parent(chart,step));
		}  
		
		
		public String getName() {return this.name;}
		public long getResourceId() { return this.resourceId; }

		//Get all chart steps that enclose this chart.
		public List<Parent> getParents() {return parents;}
		public List<StepStructure> getSteps() {return steps;}
		
		/** 
		 * Find a step with the given factory id, recursing down to enclosed charts if any.
		 * @return null if none found.
		 */ 
		public StepStructure findStepWithFactoryId(String factoryId) {
			for(StepStructure step: getSteps()) {
				if(step.getFactoryId().equals(factoryId)) {
					return step;
				}
			}
			return null;
		}
	
		/** Find a step with the given factory id, recursing down to enclosed charts if any.
		 * @return null if none found.
		 */
		public StepStructure findStepWithFactoryIdInSubtree(String factoryId) {
			StepStructure result = null;
			if((result = findStepWithFactoryId(factoryId)) != null ) {
				return result;
			}
			// Didn't find it in this chart; look in subcharts
			for(StepStructure step: getSteps()) {
				if(step.getEnclosedChart() != null) {
					if((result = step.getEnclosedChart().findStepWithFactoryIdInSubtree(factoryId)) != null) {
						return result;
					}
				}
			}
			return null;
		}

		/** Find a step with the given id, recursing down to enclosed charts if any. 
		 * @return null if none found.
		 */ 
		public StepStructure findStepWithId(String id) {
			for(StepStructure step: getSteps()) {
				if(step.getId().equals(id)) {
					return step;
				}
			}
			return null;
		}
		
		/** Find a step with the given factory id, recursing down to enclosed charts if any.
		* @return null if none found.
		*/  
		public StepStructure findStepWithIdInSubtree(String id) {
			StepStructure result = null;
			if((result = findStepWithId(id)) != null ) {
				return result;
			}
			// Didn't find it in this chart; look in subcharts
			for(StepStructure step: getSteps()) {
				if(step.getEnclosedChart() != null) {
					if((result = step.getEnclosedChart().findStepWithIdInSubtree(id)) != null) {
						return result;
					}
				}
			}
			return null;
		}

		public void addStep(StepStructure step) {
			steps.add(step);
		}
	}

	
	/** 
	 * A class to hold an SFC Step's relationships in a way that is handy for us. 
	 * Package access for ChartStructureManager.
	 */
	 static class StepStructure {
		private final String id;        // required, globally unique UUID
		final String name;              // required; unique within chart
		private final String factoryId; // required
		private final ChartStructure chart;   // the chart that contains this step; required
		private final StepStructure previous; // preceding step, if any; nullable
		private String enclosedChartName;     // the full path name of the enclosed chart; may be null or bogus
		private ChartStructure enclosedChart; // null unless enclosedChartName refers to a valid chart
		private boolean isEnclosingStep;
		
		public StepStructure(ChartStructure chart, String id, String factoryId, String name, 
							 StepStructure previous, boolean isEnclosingStep) {
			this.chart = chart;
			this.id = id;
			this.factoryId = factoryId;
			this.name = name;
			this.previous = previous;
			this.isEnclosingStep = isEnclosingStep;
		}
		
		public ChartStructure getChart() {return chart;}
		public String getFactoryId() {return factoryId;}
		public String getId() {return id;}
		public String getName() {return name;}
		public StepStructure getPrevious() {return previous;}
		public ChartStructure getEnclosedChart() {return enclosedChart;}
		public String getEnclosedChartName() {return enclosedChartName;}

		public boolean isEnclosingStep() {return isEnclosingStep;}
		
		public void setEnclosedChart(ChartStructure enclosedChart) {this.enclosedChart = enclosedChart;}
		public void setEnclosedChartName(String enclosedChartName) {this.enclosedChartName = enclosedChartName;}
		public void setEnclosingStep(boolean isEnclosingStep) {this.isEnclosingStep = isEnclosingStep;}

		
		/** return the step that encloses this one, else null. If more than one step encloses this one, 
		 *  one is arbitrarily chosen.
		 */
		public StepStructure getParent() {
			if(chart.getParents().size() > 0) {
				return chart.getParents().get(0).step;
			}
			else {
				return null;
			}
		}
		
		/** 
		 * Find an enclosing parent (or self) with the given suffix. 
		 * @return null if none found. 
		 */
		public StepStructure findParentWithNameEnding(String ending) {
			if(name.endsWith(ending)) {
				return this;
			}
			else {
				StepStructure result = null;
				for(Parent parent: chart.getParents()) {
					if((result = parent.step.findParentWithNameEnding(ending)) != null) {
						return result;
					}
				}
			}
			return null;
		}
	}
}
