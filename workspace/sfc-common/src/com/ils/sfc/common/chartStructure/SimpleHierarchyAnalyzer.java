package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XmlParseException;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

import system.ils.sfc.common.Constants;

/** A utility to analyze chart inclusion hierarchy. This is intended to be as robust as
 *  possible in the face of chart errors, so it avoids using IA chart compilation and 
 *  instead depends on design-time information.
 */
public class SimpleHierarchyAnalyzer {
	private static final String CLSS = "SimpleHierarchyAnalyzer";
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private Project globalProject;
	private StepRegistry stepRegistry;
	private Map<String, List<EnclosureInfo>> parentsByChildChartPath = new HashMap<String, List<EnclosureInfo>>();
	private Map<String,ChartInfo> chartsByPath = new HashMap<String,ChartInfo>();

	public static final Property<String> stepNameProperty = EnclosingStepProperties.Name;
	public static final Property<String> factoryIdProperty = EnclosingStepProperties.FactoryId;
	public static final Property<String> chartPathProperty = EnclosingStepProperties.CHART_PATH;
	public static final Property<ChartUIModel> parallelChildrenProperty = new BasicProperty<ChartUIModel>("parallel-children", ChartUIModel.class);
	public static final Property<JSONObject> associatedDataProperty = new BasicProperty<JSONObject>("associated-data", JSONObject.class);

	public static class ChartInfo {
		public ChartUIModel model;
		public String path;
		public long resourceId;
		
		public ChartInfo(ChartUIModel model, String path, long resourceId) {
			super();
			this.model = model;
			this.path = path;
			this.resourceId = resourceId;
		}
	}
	
	/** Info about enclosing steps in other charts that enclose a given chart. */
	public static class EnclosureInfo {
		public String chartPath;
		public String parentChartPath;
		public String parentStepName;
		public String parentStepFactoryId;
		public ChartUIElement parentElement;
		public String messageQueue;
		
		public EnclosureInfo(String childChartPath, String parentChartPath, 
			String parentStepName, String parentStepFactoryId, String messageQueue,  ChartUIElement parentElement) {
			this.chartPath = childChartPath;
			this.parentChartPath = parentChartPath;
			this.parentStepName = parentStepName;
			this.parentStepFactoryId = parentStepFactoryId;
			this.messageQueue = messageQueue;
			this.parentElement = parentElement;
		}
	}
	
	public SimpleHierarchyAnalyzer(Project globalProject, StepRegistry stepRegistry) {
		this.globalProject = globalProject;
		this.stepRegistry = stepRegistry;
	}
	
	/** Create the hierarchy info for all SFCs. */
	public void analyze() {
		List<ProjectResource> resources = globalProject.getResources();
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(ChartStructureCompiler.CHART_RESOURCE_TYPE)) {
				String chartPath = globalProject.getFolderPath(res.getResourceId());
				try {
					byte[] chartResourceData = res.getData();					
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
					ChartUIModel uiModel = ChartUIModel.fromXml(xmlInput, stepRegistry );					
					chartsByPath.put(chartPath, new ChartInfo(uiModel, chartPath, res.getResourceId()));
					analyzeModel(uiModel, chartPath);					
				}
				catch(Exception e) {
					log.errorf("IO exception deserializing chart " + chartPath + ": " + e.getMessage());
				}
			}
		}
	}

	private void analyzeModel(ChartUIModel uiModel, String parentChartPath) throws XmlParseException {
		for(ChartUIElement element: uiModel.getChartElements()) {
			if(element.getProperties().contains(chartPathProperty)) {
				String parentStepPath = element.get(stepNameProperty);
				String parentStepFactoryId = element.get(factoryIdProperty);
				String chartPath = element.get(chartPathProperty);
				String messageQueue = element.get(IlsProperty.MESSAGE_QUEUE);
				List<EnclosureInfo> parentEnclosures = parentsByChildChartPath.get(chartPath);
				if(parentEnclosures == null) {
					parentEnclosures = new ArrayList<EnclosureInfo>();
					parentsByChildChartPath.put(chartPath, parentEnclosures);
				}
				parentEnclosures.add(new EnclosureInfo(chartPath, 
					parentChartPath, parentStepPath, parentStepFactoryId, messageQueue, element));
			}
			else if(element.getProperties().contains(parallelChildrenProperty)) {
				ChartUIModel parallelChildrenModel = element.get(parallelChildrenProperty);
				analyzeModel(parallelChildrenModel, parentChartPath);
			}
		}
	}
	public Long getChartResourceForPath(String path) {
		Long result = null;
		ChartInfo info = chartsByPath.get(path);
		if( info!=null ) {
			result = new Long(info.resourceId);
		}
		else {
			log.warnf("%s.getChartResourceForPath: WARNING: Path %s not found while searching for chart",CLSS,path);
		}
		return result;
	}
	/** 
	 * Get info for all steps that enclose this chart and are at a given level in the hierarchy.
	 * Returns an empty list if none found. The list should not contain duplicates. This method is called recursively.
	 * @param enclosures passed between levels in the recursion. Initially empty or null.
	 */
	public List<String> getParentPathsForEnclosingScope(String chartPath,String scope) {
		List<String> parentPaths = new ArrayList<>();
		List<EnclosureInfo> parents =  parentsByChildChartPath.get(chartPath);
		if( parents!=null ) {
			if( scope.equalsIgnoreCase(Constants.SUPERIOR) ) {
				parents = getParentEnclosures(chartPath);
				for( EnclosureInfo info: parents) {
					parentPaths.add(info.parentChartPath);
				}
			}
			else {
				// For each chart with an enclosing step that encloses this, trace its lineage.
				for( EnclosureInfo info: parents) {
					recursivelyAddToList(info.parentChartPath,info.parentElement,scope,parentPaths);
				}
				
			}	
		}
		return parentPaths;
	}
	/** 
	 * Check the current path to see if it satisfies the requirements. If not check the parent(s).
	 * @param chartPath the chart to be tested.
	 * @param element the enclosing element 
	 * @param scope the desired scope
	 * @param ancestorPaths the current list of matches 
	 */
	public void recursivelyAddToList(String chartPath,ChartUIElement element,String scope,List<String> ancestorPaths) {
		if(chartPath!=null && !chartPath.isEmpty() ) {
			// Does the current path satisfy the criterion
			String stepFactoryId = element.get(factoryIdProperty);
			if( (scope.equalsIgnoreCase(Constants.GLOBAL) && stepFactoryId.equalsIgnoreCase(ProcedureStepProperties.FACTORY_ID))   ||
				(scope.equalsIgnoreCase(Constants.OPERATION) && stepFactoryId.equalsIgnoreCase(OperationStepProperties.FACTORY_ID))||
				(scope.equalsIgnoreCase(Constants.PHASE) && stepFactoryId.equalsIgnoreCase(PhaseStepProperties.FACTORY_ID))           ) {
				
				if( !ancestorPaths.contains(chartPath) ) ancestorPaths.add(chartPath);
			}
			else {
				// Continue the search
				List<EnclosureInfo> parents =  parentsByChildChartPath.get(chartPath);
				if( parents!=null && !parents.isEmpty() ) {
					for( EnclosureInfo info: parents) {
						recursivelyAddToList(info.parentChartPath,info.parentElement,scope,ancestorPaths);
					}
				}
				else {
					log.warnf("%s.recursivelyAddToList: WARNING: Path %s not found while searching for ancestor",CLSS,chartPath);
					return;
				}
			}
		}
	}
	
	/** Get info for all steps that enclose this chart. Returns null if none found. If more
	 *  than one step directly encloses the given chart, info for all of those will be 
	 *  returned. This does NOT recurse up the hierarchy. */
	public List<EnclosureInfo> getParentEnclosures(String chartPath) {
		return parentsByChildChartPath.get(chartPath);
	}
	
	/** Get the enclosure hierarchy for a child chart. If the "strict" flag is set, if
	 *  there are multiple parents at any level, an exception will be thrown. If not set,
	 *  one parent will be chosen arbitrarily. The enclosures are returned in
	 *  bottom-up order, i.e. the immediate parent is first in the list. If there are 
	 	no enclosures an empty list will be returned.
	 */
	public List<EnclosureInfo> getEnclosureHierarchyBottomUp(String childChartPath,
		boolean strict) {
		List<EnclosureInfo> enclosures = new ArrayList<EnclosureInfo>();
		List<EnclosureInfo> parentEnclosures = null;
		while((parentEnclosures = parentsByChildChartPath.get(childChartPath)) != null) {
			if(parentEnclosures.size() == 1 || !strict) {
				EnclosureInfo enclosure = parentEnclosures.get(0);
				enclosures.add(enclosure);
				childChartPath = enclosure.parentChartPath;
			}
			else {
				throw new IllegalArgumentException("more than one parent for " + childChartPath);
			}
		}
		return enclosures;
	}
	
	/** Get the ChartUIElement for the given recipe data scope, or null if not found. */
	public ChartUIElement getElementForScope(String scope, String childChartPath, ChartUIElement referencingElement) {
		if(scope==null || scope.equals(Constants.LOCAL)) return referencingElement;
		if(childChartPath==null)                         return referencingElement;
		List<EnclosureInfo> parents = getEnclosureHierarchyBottomUp(childChartPath, true);
		if(parents.size() == 0) return null;
		if(scope.equals(Constants.SUPERIOR)) {
			return parents.get(0).parentElement;
		}
		else {
			for(EnclosureInfo parent: parents) {
				if(scope.equals(Constants.PHASE) && 
				    parent.parentStepFactoryId.equals(PhaseStepProperties.FACTORY_ID)) {
					return parent.parentElement;
				}
				else if(scope.equals(Constants.OPERATION) && 
					parent.parentStepFactoryId.equals(OperationStepProperties.FACTORY_ID)) {
					return parent.parentElement;
				}
				else if(scope.equals(Constants.GLOBAL) && 
					  parent.parentStepFactoryId.equals(ProcedureStepProperties.FACTORY_ID)) {
					return parent.parentElement;
				}
			}
		}
		return null;
	}
	
	/** Get the message queue, as inferred from property settings in enclosing Foundation steps.
	 *  Returns null if none found.
	 */
	public String getMessageQueue(String childChartPath) {
		String queue = null;
		List<EnclosureInfo> hierarchy = getEnclosureHierarchyBottomUp(childChartPath, false);
		for(EnclosureInfo info: hierarchy) {
			if(info.messageQueue != null && info.messageQueue.length() > 0) {
				queue = info.messageQueue;
				break;
			}
		}
		return queue;
	}

	public Map<String, ChartInfo> getChartsByPath() {
		return chartsByPath;
	}

}
