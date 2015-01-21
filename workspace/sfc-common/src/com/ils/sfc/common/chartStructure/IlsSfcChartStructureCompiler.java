package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** This class takes IA SFC Charts and creates objects to represent its structure in a form 
 *  that is convenient for us.
 *  
 *  Note that since we load project resources, changes that have not been committed to the project will
 *  not be seen.
 *  
 */
public class IlsSfcChartStructureCompiler {
	// constants:
	public static final String FACTORY_ID_PROPERTY = "factory-id";
	public static final String CHART_PATH_PROPERTY = "chart-path";
	public static final String NAME_PROPERTY = "name";
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	public static final String ENCLOSING_FACTORY_ID = EnclosingStepProperties.FACTORY_ID;
	static final UUID ROOT_FOLDER_ID = ChartUIModel.ROOT_FOLDER;	

	private static LoggerEx logger = LogUtil.getLogger(IlsSfcChartStructureCompiler.class.getName());
	private final Project globalProject;
	private final StepRegistry stepRegistry;
	private List<String> messages = new ArrayList<String>();  // messages about compilation errors or warnings
	
	// intermediate structures:
	private List<UIModelInfo> uiModelInfos;
	private Map<UUID, FolderInfo> folderInfosById;
	private Map<UUID, IlsSfcStepStructure> stepsById;  // all steps for all charts indexed by UUID

	// The ultimate product of our compilation:
	private Map<String, IlsSfcChartStructure> chartsByName;

	// A helper class that holds some info about the resource hierarchy context of a chart model
	private static class UIModelInfo {
		ChartUIModel uiModel;
		String name;
		UUID parentUuid;
		String path;
		public UIModelInfo(ChartUIModel uiModel, String name, UUID parentUuid) {
			super();
			this.uiModel = uiModel;
			this.name = name;
			this.parentUuid = parentUuid;
		}				
	}
	
	private static class FolderInfo {
		UUID id;
		String name;
		UUID parentId;
		public FolderInfo(UUID id, String name, UUID parentId) {
			super();
			this.id = id;
			this.name = name;
			this.parentId = parentId;
		}		
	}

	public IlsSfcChartStructureCompiler(Project globalProject, StepRegistry stepRegistry) {
		this.globalProject = globalProject;
		this.stepRegistry = stepRegistry;
	}
				
	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public IlsSfcChartStructureMgr compile() {
		if(!loadModels()) {
			logger.error("Could not compile SFC chart models");
			return null;  // if we can't load the resources we can't do much...
		}
		createChartPathNames();
		compileCharts();  // do the IA chart compilation
		linkParents();
		return new IlsSfcChartStructureMgr(chartsByName);
	}
	
	// create the full path names for the charts by traversing the folder hierarchy
	private boolean createChartPathNames() {
		for(UIModelInfo uiModelInfo: uiModelInfos) {			
			if(!uiModelInfo.parentUuid.equals(ROOT_FOLDER_ID)) {
				StringBuilder buf = new StringBuilder();
				if(getParentPath(uiModelInfo.parentUuid, buf)) {
					buf.append("/");
					buf.append(uiModelInfo.name);
					uiModelInfo.path = buf.toString();
				}
				else {
					logger.warn("couldn't get hierarchy for " + uiModelInfo.name);
				}
			}
			else {
				uiModelInfo.path = uiModelInfo.name;
			}
		}
		return true;
	}

	/** trace up the folder hierarchy to build path names. */
	private boolean getParentPath(UUID id, StringBuilder buf) {
		if(!folderInfosById.containsKey(id)) {
			messages.add("Failed to find parent folder for id " + id);
			return false;
		}
		FolderInfo folderInfo = folderInfosById.get(id);
		if(!folderInfo.parentId.equals(ROOT_FOLDER_ID)) {
			getParentPath(folderInfo.parentId, buf);
			buf.append("/");
		}
		buf.append(folderInfo.name);
		return true;
	}

	/** Deserialize all SFC charts from the global project. */
	private boolean loadModels() {
		uiModelInfos = new ArrayList<UIModelInfo>();
		folderInfosById = new HashMap<UUID, FolderInfo>();
		List<ProjectResource> resources = globalProject.getResources();		
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				try {
					byte[] chartResourceData = res.getData();					
					//IlsSfcCommonUtils.printResource(data);					
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
					ChartUIModel uiModel = ChartUIModel.fromXML(xmlInput, stepRegistry );
					uiModelInfos.add(new UIModelInfo(uiModel, res.getName(), res.getParentUuid()));
				}
				catch(Exception e) {
					messages.add("Compilation failed--a chart resource could not be deserialized");
					logger.error("Error deserializing chart", e);
					return false;
				}
			}
			else if( res.getResourceType().equals(FOLDER_RESOURCE_TYPE)) {
				folderInfosById.put(res.getDataAsUUID(), new FolderInfo(res.getDataAsUUID(), res.getName(), res.getParentUuid()));
			}
		}
		return true;
	}

	public List<String> getMessages() {
		return messages;
	}
	
	public IlsSfcStepStructure getStep(UUID id) {
		return stepsById.get(id);
	}
	
	/** Do the IA chart compilation and create corresponding chart/step elements in our structure. */
	private boolean compileCharts() {
		chartsByName = new HashMap<String, IlsSfcChartStructure>();
		for(UIModelInfo uiModelInfo: uiModelInfos) {
			stepsById = new HashMap<UUID, IlsSfcStepStructure>();
			ChartCompiler chartCompiler = new ChartCompiler(uiModelInfo.uiModel, stepRegistry);
			ChartCompilationResults compiledChart = chartCompiler.compile();
			if(!compiledChart.isSuccessful()) {
				messages.add("Chart " + uiModelInfo.path + " did not compile.");
			}
			
			IlsSfcChartStructure newChart = new IlsSfcChartStructure(uiModelInfo.path);
			chartsByName.put(uiModelInfo.path, newChart);
			Set<UUID> seen = new HashSet<UUID>();
			for(ElementDefinition def: compiledChart.getRootDefinitions()) {
				createSteps(newChart, def, null, seen);
			}
		}
		stepsById = null; 
		return true;
	}
	
	/** Recurse through the "next elements" relation to create step structure. */
	private void createSteps(IlsSfcChartStructure chart, ElementDefinition elDef, IlsSfcStepStructure previousStep, Set<UUID> seen) {
		if(seen.contains(elDef.getElementId())) {
			return; // avoid infinite loop
		}
		else {
			seen.add(elDef.getElementId());
		}
		if(elDef.getElementType() == ElementDefinition.ElementType.Step) {
			StepDefinition stepDef = (StepDefinition)elDef;
			IlsSfcStepStructure thisStep = getOrCreateStep(chart, stepDef, previousStep);
			// by only setting "previousStep" if the object we are looking at is a step,
			// we can skip over intervening non-step objects like transitions...
			previousStep = thisStep;
		}
		for(ElementDefinition nextDef: elDef.getNextElements()) {
			createSteps(chart, nextDef, previousStep, seen);
		}
	}
	
	/** Return the step structure for the given id, creating if it doesn't exist. */
	private IlsSfcStepStructure getOrCreateStep(IlsSfcChartStructure chart, StepDefinition stepDef, IlsSfcStepStructure previousStep) {
		UUID stepId = stepDef.getElementId();
		if(!stepsById.containsKey(stepId)) {
			String stepName = (String)IlsSfcCommonUtils.getStepPropertyValue(stepDef.getProperties(), NAME_PROPERTY);
			boolean isEnclosingStep = stepDef.getFactoryId().equals(ENCLOSING_FACTORY_ID);
			IlsSfcStepStructure newStep = new IlsSfcStepStructure(chart, stepId.toString(), stepDef.getFactoryId(), 
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
	
	/** Create the relationships for chart inclusion. */
	private void linkParents() {
		for(IlsSfcChartStructure chart: chartsByName.values()) {
			for(IlsSfcStepStructure step: chart.getSteps()) {
				if(step.isEnclosingStep()) {
					String enclosedChartName = step.getEnclosedChartName();
					IlsSfcChartStructure enclosedChart = chartsByName.get(enclosedChartName);
					if(enclosedChart != null) {
						step.setEnclosedChart(enclosedChart);
						enclosedChart.addParent(chart, step);
					}
					else {
						messages.add("enclosed chart " + step.getEnclosedChartName() + " not found for step " + step.getName() + " in chart " + chart.getName() );
					}
				}
			}					
		}
	}
}
