/**
 * Copyright 2015. ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway.monitor;
/** 
 * This class holds the most recent state of a chart and its steps.
 * The intent of the class is to provide a well-known location to
 * query last-known chart state.
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.SfcGatewayHook;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.api.elements.GatewayStepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

public class IlsStepNameMapper {
	private final static String TAG = "IlsStepNameMapper";
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	private final GatewayContext context;
	private final SfcGatewayHook hook;
	private final static LoggerEx log = LogUtil.getLogger(IlsStepNameMapper.class.getPackage().getName());
	private final Map<String,FolderHolder> folderHierarchy;
	private final Map<String, String> stepIdByName;
	private final Map<String, Long> resourceIdByStep;
	
	/**
	 * On construction, create a map of Step names by UUID.
	 * @param context
	 */
	public IlsStepNameMapper(GatewayContext ctx,SfcGatewayHook iaSfcHook) {
		stepIdByName = new HashMap<>();
		folderHierarchy = new HashMap<>();
		resourceIdByStep = new HashMap<>();
		configureRootNode();    // Adds root to the folder hierarchy
		this.context = ctx;
		this.hook = iaSfcHook;
		
	}
	
	// Scan project resources for charts
	public void initialize() {
		List<ProjectResource> resources = context.getProjectManager().getGlobalProject(ApplicationScope.GATEWAY).getResources();
		GatewayStepRegistry registry = hook.getStepRegistry();
		// First figure out the folder hierarchy
		for(ProjectResource res:resources) {
			if( res==null) continue;
			if( res.getResourceType().equals(FOLDER_RESOURCE_TYPE)) {
				UUID self = res.getDataAsUUID();
				UUID parent = res.getParentUuid();
				if( self==null) continue;
				FolderHolder holder = new FolderHolder(res.getDataAsUUID(),res.getParentUuid(),res.getName());
				folderHierarchy.put(self.toString(),holder);
				log.tracef("%s.initialize: folder resource %s (%s) (%s, parent %s)", TAG,res.getName(),res.getResourceType(),
						self.toString(),(parent==null?"NO PARENT":parent.toString()));
				resolvePath(holder);  // High likelihood of success if we're traversing down the tree
			}
		}
		// Complete the folder analysis
		resolveFolderHierarchy();
		
		// Loop over the chart resources -- we're really after the steps.
		// We can now assign full paths to the chart.
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if(ccr.isSuccessful()) {
						ChartDefinition definition = ccr.getChartDefinition();
						FolderHolder fh = folderHierarchy.get(res.getParentUuid().toString());
						if( fh!=null ) {
							String path = fh.getPath()+"/"+res.getName();
							if( path.startsWith("/")) path = path.substring(1);
							// Use the chart-specific logger to announce its creation
							LoggerEx logger = LogUtil.getLogger(path);
							logger.debugf("%s.initilize: Created chart resource and logger  %s",TAG,path);
							mapElement(path,res.getResourceId(),definition.getBeginElement());
						}
						else {
							// We expect each chart to have a parent folder. 
							log.warnf("%s.initialize: Could not find parent for chart %s", TAG,res.getName());
							mapElement(res.getName(),res.getResourceId(),definition.getBeginElement());
						}
					}
					else {
						log.warnf("%s.initialize: Chart %s has compilation errors", TAG,res.getName());
					}
				}
				catch(IOException ioe ) {
					log.warnf("%s.initialize: IO Exception for %s (%s)", TAG,res.getName(),ioe.getLocalizedMessage());
				}
				catch(NumberFormatException nfe ) {
					log.warnf("%s.initialize: Chart instantiation error for %s (%s)", TAG,res.getName(),nfe.getLocalizedMessage());
				}
				catch(XMLParseException xpe ) {
					log.warnf("%s.initialize: Parse Exception for %s (%s)", TAG,res.getName(),xpe.getLocalizedMessage());
				}
				catch(Exception ex) {
					log.warn(TAG+".initialize: Unhandled exception for "+ res.getName(),ex);
				}
			}	
		}
		
	}
	// Configure a root node. All folders should have a parent
	private void configureRootNode() {
		FolderHolder root = new FolderHolder(ChartUIModel.ROOT_FOLDER,null,"");
		root.setPath("");
		folderHierarchy.put(ChartUIModel.ROOT_FOLDER.toString(), root);
	}
	// Add the current element to the map, then recursively its children.
	private void mapElement(String chartName,long resId,ElementDefinition element) {
		if( element instanceof StepDefinition ) {
			StepDefinition stepDef = (StepDefinition)element;
			String stepId = stepDef.getElementId().toString();
			if( stepIdByName.get(stepId)!=null ) return; // We've looped
			String name = stepDef.getProperties().get(ChartStepProperties.Name);
			stepIdByName.put(makeKey(chartName,name), stepId);
			resourceIdByStep.put(stepId, new Long(resId));
			log.debugf("%s.mapElement: %s:%s is %s",TAG,chartName,name,stepId);

			for( ElementDefinition def:stepDef.getNextElements() ) {
				mapElement(chartName,resId,def);
			}
		}
	}
	// Resolve folder hierarchy
	// Creates complete folder paths for each folder.
	// We expect the parents to be resolved before their children.
	private void resolveFolderHierarchy() {
		log.debugf("%s.resolveFolderHierarchy ...", TAG);
		int MAX_DEPTH = 100;
		int depth = 0;
		boolean success = false;
		while( !success && depth<MAX_DEPTH ) {
			success = true;
			for(FolderHolder holder:folderHierarchy.values()) {
				if( holder.getPath()==null) {
					if(!resolvePath(holder)) success = false;       // Didn't resolve
				}
			}
			depth++;
		}
		if(!success) log.warnf("%s.resolveFolderHierarchy. Failed to find paths for all folders", TAG);
	}
		
	private boolean resolvePath(FolderHolder holder) {
		boolean success = false;
		UUID parent = holder.getParent();
		if( parent!=null ) {
			FolderHolder parentHolder = folderHierarchy.get(parent.toString());
			if( parentHolder!=null ) {
				String path = parentHolder.getPath();
				if( path!=null) {
					if( path.length()==0) path = holder.getName();
					else path = String.format("%s/%s", path,holder.getName());
					holder.setPath(path);
					success = true;
				}
			}
			else {
				// We expect all to be resolved immediately, but are not assured of this.
				log.infof("%s.resolvePath. Unresolved parent %s for folder %s", TAG,holder.getParent().toString(),holder.getId().toString());
			}
		}
		else {
			// We expect all to be resolved immediately, but are not assured of this.
			log.infof("%s.resolvePath. No parent for folder %s", TAG,holder.getName());
		}
		return success;
	}
	
	public String idForName(String chartName,String name) { return stepIdByName.get(makeKey(chartName,name)); }
	/**
	 * @param stepId
	 * @return the resourceId associated with the parent chart of the specified step.
	 *         If the lookup fails, return -1.
	 */
	public long resourceIdForStep(String stepId) { 
		Long id = resourceIdByStep.get(stepId);
		if( id!=null) return id.longValue();
		else return -1L; 
	}
	
	private String makeKey(String chartName,String stepName) {
		StringBuilder sb = new StringBuilder();
		sb.append(chartName);
		sb.append(":");
		sb.append(stepName);
		return sb.toString();
	}
	/**
	 * Helper class for use in trying to resolve folder paths
	 */
	private class FolderHolder {
		private final UUID id;
		private final UUID parentId;
		private final String name;
		private String path = null;   // As long as this is null, we're not resolved
		public FolderHolder(UUID uuid,UUID parentUUId,String folderName) {
			this.id = uuid;
			this.parentId = parentUUId;
			this.name = folderName;
		}
		public UUID getId() { return id; }
		public String getName() { return name;}
		public String getPath() { return path; }
		public UUID getParent() { return parentId; }
		public void setPath(String p) { this.path = p; }
	}
}
