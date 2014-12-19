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
	private int ROOT_ROW = 0;           // Number of the root row
	
	// Table column names
	private static final String CXNS    = "Cxns";          // Incoming connection count
	private static final String ENCLOSURES = "Enclosures"; // Refs to this as an enclosure
	public static final String KEY      = "Key";
	public static final String NAME     = "Name";
	public static final String PARENT   = "Parent";
	private static final String PATH    = "Path";       // Chart identifier
	public static final String RESOURCE = "Resource"; // ResourceId

	private final Map<Integer,List<Integer>> childrenLookup; // Find node children by parent row
	private final Map<String,Integer>  rowLookup;            // Find node row by path
	private final Map<String,FolderHolder> folderHierarchy;
	private final List<EnclosingStep> enclosingSteps;
	
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final Table nodes;
	private final Table edges;
	
	public ChartTreeDataModel(DesignerContext ctx) {
		context = ctx;
		childrenLookup = new HashMap<>();
		folderHierarchy = new HashMap<>();
		rowLookup = new HashMap<>();
		nodes = new Table();
		nodes.addColumn(CXNS, int.class);        // Count of linked connections 
		nodes.addColumn(ENCLOSURES, int.class);  // Number of times this node has been used as an enclosure
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
		
		enclosingSteps = new ArrayList<>();
		initialize();
	}

	/**
	 * Initialize the UI
	 */
	private void initialize() {
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResources();
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		// Initialize the folder hierarchy
		UUID root = ChartUIModel.ROOT_FOLDER;
		log.tracef("%s.initialize: ROOT_FOLDER = %s",TAG,root.toString());
		configureRootNode();
		FolderHolder rootHolder = new FolderHolder(root,null,"");
		rootHolder.setPath("");
		folderHierarchy.put(root.toString(), rootHolder);
		
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				log.tracef("%s.initialize: found chart %s, parent = %s", TAG,res.getName(),res.getParentUuid().toString());

				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if(ccr.isSuccessful()) {
						ChartDefinition definition = ccr.getChartDefinition();
						int row = addNodeTableRow(res.getName(),res.getResourceId());
						nodes.setString(row, PARENT, res.getParentUuid().toString());
						// Check steps in node for being enclosing steps
						handleEnclosingSteps(row,null,definition.getBeginElement().getNextElements());
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
				folderHierarchy.put(self.toString(),holder);
				log.tracef("%s.initialize: folder resource %s (%s) (%s, parent %s)", TAG,res.getName(),res.getResourceType(),
						self.toString(),res.getParentUuid().toString());
				resolvePath(holder);  // High likelihood of success if we're traversing down the tree
			}
		}
		// Make table legal if it was empty
		if(nodes.getRowCount()==0)  {
			configureWhenEmpty();
		}
		else {
			// Resolve any folder paths
			resolveFolderHierarchy();  // For folders compute complete parentage
			resolveFolderPaths();      // Set the folder path for each node
			linkEnclosingNodes();      // Replicate enclosing node descendants
			resolveRootConnections();  // Link nodes with no parent to the root node
			rowLookup.clear();   // Free memory
			folderHierarchy.clear();
		}
		log.tracef("%s.initialize ...COMPLETE", TAG);
	}
	
	/**
	 * @return a tree constructed out of the nodes and edges.
	 */
	public Tree getTree() {
		Tree tree = new Tree(nodes,edges,KEY,Graph.DEFAULT_SOURCE_KEY,Graph.DEFAULT_TARGET_KEY);
		return tree;
	}
	

	
	// Create a connection between nodes. Update the connection count
	// so that later on we can determine which nodes connect to the root.
	// @return the row corresponding to the newly created connection.
	private int addEdgeTableRow(int sourceRow,int destinationRow) {
		// Update connection count for the destination
		int count = nodes.getInt(destinationRow,CXNS);
		nodes.setInt(destinationRow, CXNS, count+1);
		int row = edges.getRowCount();
		log.infof("%s.addEdgeTableRow: %d -> %d", TAG,sourceRow,destinationRow);
		edges.addRow();
		edges.setInt(row,Graph.DEFAULT_SOURCE_KEY,sourceRow);
		edges.setInt(row,Graph.DEFAULT_TARGET_KEY,destinationRow);
		return row;
	}
	// We've found a chart resource. Add it to the table
    // @param resourceId if negative, then this row corresponds to
	// @return the row corresponding to the newly discovered chart.
	private int addNodeTableRow(String name,long resourceId) {
		int row = nodes.getRowCount();
		log.infof("%s.addNodeTableRow: %d = %s", TAG,row,name);
		nodes.addRow();
		nodes.setInt(row,CXNS,0); 
		nodes.setInt(row,ENCLOSURES,0);  
		nodes.setString(row,NAME,name);
		nodes.setInt(row,KEY,row);
		nodes.setLong(row,RESOURCE,resourceId);
		return row;
	}
	// Configure a root node. Connect all nodes without other
	// parents to this.
	private void configureRootNode() {
		ROOT_ROW = nodes.getRowCount();
		nodes.addRow();
		log.infof("%s.configureRootNode. root", TAG);
		nodes.setString(ROOT_ROW,NAME,"root");
		nodes.setInt(ROOT_ROW,CXNS,0);
		nodes.setInt(ROOT_ROW,KEY,ROOT_ROW);
		nodes.setString(ROOT_ROW,PATH,"");
		nodes.setLong(ROOT_ROW,RESOURCE,-1);
	}
	// Configure the nodes table to display something reasonable
	// if it is otherwise empty.
	private void configureWhenEmpty() {
		int row = 0;
		nodes.addRow();
		log.warnf("%s.configureWhenEmpty. No charts", TAG);
		nodes.setString(row,NAME,"No charts");
		nodes.setInt(row,CXNS,0);
		nodes.setInt(row,KEY,0);
		nodes.setString(row,PATH,"/");
		nodes.setLong(row,RESOURCE,-1);
	}
	
	// Check and see if the referenced element is an enclosing step
	// @param parentRow the row in the nodes table corresponding to the enclosing block
	private void handleEnclosingSteps(int parentRow,String stepName,List<ElementDefinition> steps) {
		for( ElementDefinition step:steps) {
			if( step instanceof StepDefinition ) {
				StepDefinition stepDef = (StepDefinition)step;
				if( stepDef.getFactoryId().equals(EnclosingStepProperties.FACTORY_ID)) {
					String name = stepDef.getProperties().get(EnclosingStepProperties.Name);
					String path = stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH);
					log.infof("%s.handleEnclosingStep: enclosing step %d.%s = %s", TAG,parentRow,name,path);
					// Create the step-that-is-an-enclosure node
					// Create the base instance.
					EnclosingStep es = new EnclosingStep(parentRow,name,path);
					enclosingSteps.add(es);
				}
			}
			handleEnclosingSteps(parentRow,stepName,step.getNextElements());
		}
	}

	// Loop through the enclosing steps, creating new linkages.
	// The method called populates the entire tree under the
	// enclosure. Each enclosing step is expanded separately
	private void linkEnclosingNodes() {
		for( EnclosingStep step:enclosingSteps) {
			populateEnclosureReference(step.getParentRow(),step);	
		}
	}
	// If this is other than the first time through, copy the
	// existing node hierarchy as a completely new linkage.
	// Stop should there be a circular reference.
	//@param parentRow is the actual parent. This may or may not be the same node as 
	//       was found originally when the step was created.
	private void populateEnclosureReference(int parentRow,EnclosingStep step) {
		Integer refrow = rowLookup.get(step.getReferencePath());
		if( refrow!=null ) {
			String newName = String.format("%s:%s",step.getStepName(),step.getReferenceName());
			int count = nodes.getInt(refrow, ENCLOSURES);
			nodes.setInt(refrow,ENCLOSURES,count+1);
			log.infof("%s.populateEnclosureReference: enclosure %d.%s->%d, count=%d", TAG,parentRow,newName,refrow,count);
			// If this is other than the first reference to the
			// enclosed node, then make a copy.
			if( count>0) {
				// For an enclosing step, copy the hierarchy of its
				// descendant children. Stop should there be a circular reference.
				refrow = addNodeTableRow( newName,nodes.getInt(refrow, RESOURCE));
				addEdgeTableRow(parentRow,refrow);
			}
			else {
				// Use the combined name from the step and referenced node
				// and apply it to the node referenced by the enclosure
				// create a link to the parent node.
				nodes.setString(refrow,NAME,newName);
				addEdgeTableRow(parentRow,refrow);
			}
		}
		else {
			log.warnf("%s.populateEnclosureReference. Unable to find node %s referenced by enclosure %d:%s:%s", TAG,parentRow,step.getReferencePath(),step.getStepName());
		}
		// Search the enclosing steps for enclosures under this parent
		for( EnclosingStep es:enclosingSteps) {
			if( es.getParentRow()==parentRow)	 {
				populateEnclosureReference(refrow,es);
			}
		}
	}

	
	// Resolve folder hierarchy
	// Creates complete folder paths for each folder.
	// We expect the parents to be resolved before their children.
	private void resolveFolderHierarchy() {
		log.infof("%s.resolveFolderHierarchy ...", TAG);
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
			log.tracef("%s.resolvePath. Unresolved parent %s for folder %s", TAG,holder.getParent().toString(),holder.getId().toString());
		}
		return success;
	}
	
	// Loop through all of the nodes-that-are-charts and
	// set the path.
	private void resolveFolderPaths() {
		log.infof("%s.resolveFolderPaths ...", TAG);
		int maxRow = nodes.getRowCount();
		int row = 0;
		while(row<maxRow) {
			long resourceId = nodes.getLong(row, RESOURCE);
			if( resourceId>=0) {
				String parent = nodes.getString(row, PARENT);
				if( parent !=null ) {
					FolderHolder fh = folderHierarchy.get(parent);
					String path = fh.getPath();   // Parent path
					if( path!=null) {
						String name = nodes.getString(row, NAME);
						if( path.length()==0) path = name;
						else path = String.format("%s/%s",path,name);
						nodes.setString(row, PATH, path);
						log.infof("%s.resolveFolderPaths ... %d is %s", TAG,row,path);
						rowLookup.put(path,new Integer(row));   // So we can find this for links 
					}
					else {
						log.warnf("%s.resolveFolderPaths. No path for resource %d (parent=%s)", TAG,resourceId,parent);
					}
				}
				else {
					log.warnf("%s.resolveFolderPaths. Unknown parent for resource %d", TAG,resourceId);
				}
			}
			row++;
		}
	}

	// Loop through all of the nodes. If they haven't been connected to by anything,
	// then they should be connected to the root node.
	private void resolveRootConnections() {
		log.infof("%s.resolveRootConnections ...", TAG);
		int maxRow = nodes.getRowCount();
		int row = 0;
		while(row<maxRow) {
			int connections = nodes.getInt(row,CXNS);
			if( connections==0 && row!=ROOT_ROW) {
				addEdgeTableRow(ROOT_ROW,row);
			}
			row++;
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
		public UUID getId() { return id; }
		public String getName() { return name;}
		public String getPath() { return path; }
		public UUID getParent() { return parentId; }
		public void setPath(String p) { this.path = p; }
	}
	/**
	 * Information about an enclosing step so that we can retrieve and
	 * create/replicate all enclosing links.
	 */
	private class EnclosingStep {
		private final String referenceName;
		private final String stepName;
		private final String path;
		private final int parentRow;
		
		public EnclosingStep(int pRow,String sName,String referencePath ) {
			this.parentRow = pRow;
			this.referenceName = getLastPathElement(referencePath);
			this.stepName   = sName;
			this.path = referencePath;
		}
		public String getReferenceName() { return referenceName; }
		public String getStepName() { return stepName; }     // May be null
		public String getReferencePath() { return path; }
		public int getParentRow() { return parentRow; }
		private String getLastPathElement(String path) {
			String result = path;
			int index = path.lastIndexOf("/");
			if( index>=0 && index<path.length() ) {
				result = path.substring(index+1);
			}
			return result;
		}
	}
}
