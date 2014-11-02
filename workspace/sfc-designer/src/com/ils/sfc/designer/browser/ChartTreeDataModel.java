/**
 * Copyright 2014. ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tree;

import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** 
 * This class holds SfcChart objects in a Tree structure. The tree is derived from
 * serialized """ resources. We create the tree on startup and from then on rely on
 * project resource updates.
 */
public class ChartTreeDataModel {
	private static final String TAG = "ChartTreeDataModel";
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	// Table column names
	private static final String PATH    = "Path";       // Chart identifier
	public static final String KEY      = "Key";
	public static final String NAME     = "Name";
	public static final String PARENT   = "Parent";
	public static final String RESOURCE = "Resource"; // ResourceId
	private final Map<String,Integer> idMap;                 // index given UUID string
	private final List<LinkHolder> links;    // link elements
	private final Map<UUID,FolderHolder> folderHierarchy;
	
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final Table nodes;
	private final Table edges;
	private int nodeCount = 0;
	
	public ChartTreeDataModel(DesignerContext ctx) {
		context = ctx;
		idMap = new HashMap<>();
		links = new ArrayList<>();
		folderHierarchy = new HashMap<>();
		nodes = new Table();
		nodes.addColumn(NAME, String.class);
		nodes.addColumn(KEY, int.class);            // Table row - key
		nodes.addColumn(PATH, String.class);
		nodes.addColumn(PARENT, String.class);
		nodes.addColumn(RESOURCE, long.class);
		
		edges = new Table();
		// The keys match the node key in the node table
		// The node direction is from parent to child.
		edges.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
		edges.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
		
		initialize();
	}

	/**
	 * Initialize the UI
	 */
	private void initialize() {
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResources();
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		// Initialize the folder hierachy
		UUID root = ChartUIModel.ROOT_FOLDER;
		FolderHolder rootHolder = new FolderHolder(root,null,"");
		rootHolder.setPath("");
		folderHierarchy.put(root, rootHolder);
		
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				log.infof("%s.initialize: found chart %s, parent = %s", TAG,res.getName(),res.getParentUuid().toString());

				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if(ccr.isSuccessful()) {
						ChartDefinition definition = ccr.getChartDefinition();
						int row = addTableRow(res.getName(),res.getResourceId());
						checkForEnclosingStep(row,definition.getBeginElement());
					}
					else {
						log.warnf("%s.initialize: Chart %s has compilation errors", TAG,res.getName());
					}
				}
				catch(IOException ioe ) {
					log.warnf("%s.initialize: IO Exception for %s (%s)", TAG,res.getName(),ioe.getLocalizedMessage());
				}
				catch(XMLParseException xpe ) {
					log.warnf("%s.initialize: Parse Exception for %s (%s)", TAG,res.getName(),xpe.getLocalizedMessage());
				}
			}
			// Handle the folder hierarchy
			else if( res.getResourceType().equals(FOLDER_RESOURCE_TYPE)) {
				UUID self = res.getDataAsUUID();
				FolderHolder holder = new FolderHolder(res.getDataAsUUID(),res.getParentUuid(),res.getName());
				folderHierarchy.put(self,holder);
				log.infof("%s.initialize: folder resource %s (%s) (%s, parent %s)", TAG,res.getName(),res.getResourceType(),
						self.toString(),res.getParentUuid().toString());
				resolvePath(holder);  // High likelihood of success if we're traversing down the tree
			}
		}
		// Make table legal if it was empty
		if(nodes.getRowCount()==0)  {
			configureNodesAsEmpty();
		}
		else {
			// Resolve any folder paths
			//configureRootNode();
			resolveFolderHierarchy();
			//resolveFolderPaths();
			//connectLinkElements();
			//resolveRootConnections();
		}
	}
	
	/**
	 * @return a tree constructed out of the nodes and edges.
	 */
	public Tree getTree() {
		Tree tree = new Tree(nodes,edges,KEY,Graph.DEFAULT_SOURCE_KEY,Graph.DEFAULT_TARGET_KEY);
		return tree;
	}
	
	// Check and see if the referenced element is an enclosing step
	// @param parentRow the row in the nodes table corresponding to the enclosing block
	private void checkForEnclosingStep(int parentRow,ElementDefinition step) {
		StepDefinition stepDef = (StepDefinition)step;
		if( stepDef.getFactoryId().equals(EnclosingStepProperties.FACTORY_ID)) {
			log.infof("%s.checkForEnclosingStep: enclosing step %s = %s", TAG,
					stepDef.getProperties().get(EnclosingStepProperties.Name),
					stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH));
		}
				
		// Check descendents
		List<ElementDefinition> nextSteps = step.getNextElements();
		for( ElementDefinition ce:nextSteps) {
			checkForEnclosingStep(parentRow,ce);
		}
	}
	
	// We've found a chart resource. Add it to the table
    // @param resourceId if negative, then this row corresponds to
	// @return the row corresponding to the newly discovered chart.
	private int addTableRow(String name,long resourceId) {
		int row = nodes.getRowCount();
		log.infof("%s.addTableRow: %d = %s", TAG,row,name);
		nodes.addRow();
		nodes.setString(row,NAME,name);
		nodes.setInt(row,KEY,row);
		//nodes.setString(row,ID,"Not-a-uuid");
		nodes.setLong(row,RESOURCE,resourceId);
		return row;
	}
	// Configure the nodes table to display something reasonable
	// if it is otherwise empty.
	private void configureNodesAsEmpty() {
		int row = 0;
		nodes.addRow();
		log.warnf("%s.configureNodesAsEmpty. %s", TAG,Graph.DEFAULT_NODE_KEY);
		nodes.setString(row,NAME,"No charts");
		nodes.setInt(row,KEY,0);
		nodes.setString(row,PATH,"/");
		nodes.setLong(row,RESOURCE,-1);
	}
	
	// Resolve folder hierarchy
	private void resolveFolderHierarchy() {
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
		FolderHolder parentHolder = folderHierarchy.get(parent);
		if( parentHolder!=null ) {
			String path = parentHolder.getPath();
			if( path!=null) {
				if( path.length()==0) path = holder.getName();
				else path = String.format("%s/%s", path,holder.getName());
				holder.setPath(path);
				success = true;
			}
		}
		return success;
	}
	
	/**
	 * Class to hold UI element link information.
	 */
	private class LinkHolder {
		public LinkHolder(String parentId,ChartUIElement element) {
			
		}
		
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
		public String getName() { return name;}
		public String getPath() { return path; }
		public UUID getParent() { return parentId; }
		public void setPath(String p) { this.path = p; }
	}
	
}
