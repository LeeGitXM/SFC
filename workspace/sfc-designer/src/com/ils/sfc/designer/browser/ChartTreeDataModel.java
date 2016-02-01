/**
 * Copyright 2014-2015. ILS Automation. All rights reserved.
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

import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.project.Project;
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
import com.inductiveautomation.sfc.definitions.ParallelDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** 
 * This class holds SfcChart objects in a Tree structure. The tree is derived from
 * serialized """ resources. We create the tree on startup and from then on rely on
 * project resource updates.
 * 
 * NOTE: This class has its own logger. Turn on degugging and you can see the complete
 *       analysis of the chart resources.
 */
public class ChartTreeDataModel {
	private static final String TAG = "ChartTreeDataModel";
	public static int ROOT_ROW = 0;                // Number of the root row
	private final boolean DEBUG_STRUCTURE = false; // Development convenience
	private final Project project;
	private final Map<Long,ChartDefinition> definitions;   // Chart definition by resourceId
	private final Map<Integer,Integer> lineage;            // Find parent row given child row
	private final Map<String,Integer>  rowLookup;          // Find node row by path
	private final Map<Integer,List<String>> stepMap;       // For checking step for parent
	private final List<EnclosingStep> enclosingSteps;
	
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName()+"."+TAG);
	private final Table nodes;
	private final Table edges;
	
	public ChartTreeDataModel(DesignerContext ctx) {
		this.context = ctx;
		this.project = context.getGlobalProject().getProject();
		lineage = new HashMap<>();
		definitions = new HashMap<>();
		rowLookup = new HashMap<>();
		stepMap = new HashMap<>();
		nodes = new Table();
		nodes.addColumn(BrowserConstants.CXNS, int.class);        // Count of linked connections 
		nodes.addColumn(BrowserConstants.STATUS, int.class);      // Health of this node
		nodes.addColumn(BrowserConstants.NAME, String.class);
		nodes.addColumn(BrowserConstants.KEY, int.class);         // Table row - key
		nodes.addColumn(BrowserConstants.PATH, String.class);
		nodes.addColumn(BrowserConstants.RESOURCE, long.class);
		
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
		List<ProjectResource> resources = project.getResources();  // Global project resources
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		// Initialize the folder hierarchy
		UUID root = ChartUIModel.ROOT_FOLDER;
		if( log.isTraceEnabled() || DEBUG_STRUCTURE ) log.infof("%s.initialize: ROOT_FOLDER = %s",TAG,root.toString());
		configureRootNode();
		lineage.clear();
		rowLookup.clear();
		
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(BrowserConstants.CHART_RESOURCE_TYPE)) {
				if( log.isTraceEnabled() || DEBUG_STRUCTURE ) log.infof("%s.initialize: chart %s, parent = %s", TAG,res.getName(),res.getParentUuid().toString());

				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if(ccr.isSuccessful()) {
						ChartDefinition definition = ccr.getChartDefinition();
						definitions.put(new Long(res.getResourceId()), definition);
						int row = addNodeTableRow(res.getName(),res.getResourceId());
						lineage.put(new Integer(row), new Integer(ROOT_ROW));
						// Check steps in chart for being enclosures
						analyzeChartSteps(row,definition,res.getName());
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
			// Ignore the folders. We use the resource Id to get paths.
			else if( res.getResourceType().equals(BrowserConstants.FOLDER_RESOURCE_TYPE)) {
				; //
			}
		}
		// Make table legal if it was empty
		if(nodes.getRowCount()==0)  {
			configureWhenEmpty();
		}
		else {
			resolvePaths();            // Use the resourceIds to look up the paths
			linkEnclosingNodes();      // Replicate enclosing node descendants
			resolveRootConnections();  // Link nodes with no parent to the root node
		}
		log.tracef("%s.initialize ...COMPLETE", TAG);
	}
	
	/**
	 * @return a map of chart definitions indexed by resourceId.
	 */
	public Map<Long,ChartDefinition>  getDefinitions() { return definitions; }
	/**
	 * @return the list of chart connections.
	 */
	public Table getEdges() { return this.edges; }
	/**
	 * @return a list of enclosing steps
	 */
	public List<EnclosingStep> getEnclosingSteps() { return this.enclosingSteps; }
	/**
	 * @return a map of parent given child. These are indices into the nodes
	 *         structure.
	 */
	public Map<Integer,Integer>  getLineage() { return lineage; }
	/**
	 * @return the list of chart nodes.
	 */
	public Table getNodes() { return this.nodes; }
	/**
	 * @return a map of node rows indexed by path,
	 */
	public Map<String,Integer>  getRowLookup() { return rowLookup; }

	/**
	 * @return a tree constructed out of the nodes and edges.
	 */
	public Tree getTree() {
		Tree tree = new Tree(nodes,edges,BrowserConstants.KEY,Graph.DEFAULT_SOURCE_KEY,Graph.DEFAULT_TARGET_KEY);
		return tree;
	}

	
	// Create a connection between nodes. Update the connection count
	// so that later on we can determine which nodes connect to the root.
	// @return the row corresponding to the newly created connection.
	private int addEdgeTableRow(int sourceRow,int destinationRow) {
		// Update connection count for the destination
		int count = nodes.getInt(destinationRow,BrowserConstants.CXNS);
		nodes.setInt(destinationRow, BrowserConstants.CXNS, count+1);
		int row = edges.getRowCount();
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof("%s.addEdgeTableRow: %d -> %d", TAG,sourceRow,destinationRow);
		edges.addRow();
		edges.setInt(row,Graph.DEFAULT_SOURCE_KEY,sourceRow);
		edges.setInt(row,Graph.DEFAULT_TARGET_KEY,destinationRow);
		return row;
	}
	
	// We've found a chart resource. Add it to the table
    // @param resourceId if negative, then this row corresponds to an enclosing step
	// @return the row corresponding to the newly discovered chart.
	private int addNodeTableRow(String name,long resourceId) {
		int row = nodes.getRowCount();
		log.tracef("%s.addNodeTableRow: %d = %s (%d)", TAG,row,name,resourceId);
		nodes.addRow();
		nodes.setInt(row,BrowserConstants.CXNS,0); 
		nodes.setInt(row,BrowserConstants.STATUS,BrowserConstants.STATUS_OK);  
		nodes.setString(row,BrowserConstants.NAME,name);
		nodes.setInt(row,BrowserConstants.KEY,row);
		nodes.setLong(row,BrowserConstants.RESOURCE,resourceId);
		return row;
	}
	// Configure a root node. Connect all nodes without other
	// parents to this.
	private void configureRootNode() {
		ROOT_ROW = nodes.getRowCount();
		nodes.addRow();
		log.debugf("%s.configureRootNode. root", TAG);
		nodes.setString(ROOT_ROW,BrowserConstants.NAME,".");
		nodes.setInt(ROOT_ROW,BrowserConstants.CXNS,0);
		nodes.setInt(ROOT_ROW,BrowserConstants.STATUS,BrowserConstants.STATUS_OK);
		nodes.setInt(ROOT_ROW,BrowserConstants.KEY,ROOT_ROW);
		nodes.setString(ROOT_ROW,BrowserConstants.PATH,"");
		nodes.setLong(ROOT_ROW,BrowserConstants.RESOURCE,-1);
	}
	// Configure the nodes table to display something reasonable
	// if it is otherwise empty.
	private void configureWhenEmpty() {
		int row = 0;
		nodes.addRow();
		log.warnf("%s.configureWhenEmpty. No charts", TAG);
		nodes.setString(row,BrowserConstants.NAME,"No charts");
		nodes.setInt(row,BrowserConstants.CXNS,0);
		nodes.setInt(row,BrowserConstants.STATUS,BrowserConstants.STATUS_OK);
		nodes.setInt(row,BrowserConstants.KEY,0);
		nodes.setString(row,BrowserConstants.PATH,"/");
		nodes.setLong(row,BrowserConstants.RESOURCE,-1);
	}
	// Iterate through the step in a chart looking for enclosures
	private void analyzeChartSteps(int row,ChartDefinition definition,String name) {
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof( "%s.analyzeChartSteps: chart %d (%s)", TAG,row,name);
		for( ElementDefinition def:definition.getBeginElement().getNextElements() ) {
			ElementDefinition.ElementType elementType = def.getElementType();
			if( elementType == ElementDefinition.ElementType.Step )  {
				StepDefinition stepDef = (StepDefinition)def;
				String stepname = stepDef.getProperties().get(ChartStepProperties.Name);
				analyzeStep(row,stepDef,stepname);
			}
			else if(elementType == ElementDefinition.ElementType.Parallel) {
				ParallelDefinition parallelDef = (ParallelDefinition)def;
				analyzeParallelSection(row,parallelDef);
			}
			else if( elementType == ElementDefinition.ElementType.Transition )  { 
				analyzeTransition(row,def);
			}
		}
	}
	// Iterate through the steps in a parallel section looking for enclosures
	private void analyzeParallelSection(Integer row,ParallelDefinition parallelDef) {
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof( "%s.analyzeParallelSection: chart %d", TAG,row);
		// Internal to the parallel section
		List<ElementDefinition> starters = parallelDef.getStartElements();
		for(ElementDefinition def:starters) {
			ElementDefinition.ElementType elementType = def.getElementType();
			if( elementType == ElementDefinition.ElementType.Step)  {
				StepDefinition stepDef = (StepDefinition)def;
				PropertySet ps = stepDef.getProperties();
				String stepname = ps.get(ChartStepProperties.Name);
				analyzeStep(row,stepDef,stepname);
			}
			else if( elementType == ElementDefinition.ElementType.Parallel ) {
				ParallelDefinition pd = (ParallelDefinition)def;
				analyzeParallelSection(row,pd);	
			}
			else if( elementType == ElementDefinition.ElementType.Transition ) {
				analyzeTransition(row,def);
			}
		}
		// External to the parallel section
		List<ElementDefinition> nextElements = parallelDef.getNextElements();
		for(ElementDefinition def:nextElements) {
			ElementDefinition.ElementType elementType = def.getElementType();
			if( elementType == ElementDefinition.ElementType.Step )  {
				StepDefinition stepDef = (StepDefinition)def;
				PropertySet ps = stepDef.getProperties();
				String stepname = ps.get(ChartStepProperties.Name);
				analyzeStep(row,stepDef,stepname);
			}
			else if( elementType == ElementDefinition.ElementType.Parallel ) {
				ParallelDefinition pd = (ParallelDefinition)def;
				analyzeParallelSection(row,pd);	
			}
			else if( elementType == ElementDefinition.ElementType.Transition ) {
				analyzeTransition(row,def);
			}
		}	
	}
	
	// Check and see if the referenced element is an enclosing step. If we've already seen
	// the step then ignore -- we're in a loop.
	// @param parentRow the row in the nodes table corresponding to the enclosing block. Guaranteed 
	//                  to be non-null
	private void analyzeStep(int row,StepDefinition stepDef,String stepName) {
		if( stepDef==null ) return;  // Compile failure
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof( "%s.analyzeStep: chart %d (%s)", TAG,row,stepName);

		Integer parentRow = new Integer(row);
		List<String> stepNames = stepMap.get(parentRow);
		if( stepNames==null) {
			stepNames = new ArrayList<>();
			stepMap.put(parentRow, stepNames);
		}
		if( stepNames.contains(stepName))  return;  // We've already seen this node
		if( stepName!=null ) stepNames.add(stepName);

		// Custom enclosures don't inherit from Enclosing step, but they all must have a path.
		if( EnclosingStepProperties.FACTORY_ID.equals(stepDef.getFactoryId()) ||
			(stepDef.getProperties()!=null && stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH)!=null) ) {

			String path = stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH);
			if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof("%s.analyzeStep:   enclosure %d.%s, references %s", TAG,parentRow,stepName,path);
			// Create the step-that-is-an-enclosure node (if it doesn't already exist)
			int newRow = addNodeTableRow(stepName,BrowserConstants.NO_RESOURCE);
			addEdgeTableRow(parentRow.intValue(),newRow);
			lineage.put(new Integer(newRow), new Integer(parentRow));
			// Link it to the base and create an EnclosingStep reference
			EnclosingStep es = new EnclosingStep(parentRow,newRow,stepName,path);
			enclosingSteps.add(es);
		}
		
		for(ElementDefinition def:stepDef.getNextElements()) {
			ElementDefinition.ElementType elementType = def.getElementType();
			if( elementType == ElementDefinition.ElementType.Step)  {
				StepDefinition sd = (StepDefinition)def;
				PropertySet ps = sd.getProperties();
				String stepname = ps.get(ChartStepProperties.Name);
				analyzeStep(row,sd,stepname);
			}
			else if( elementType == ElementDefinition.ElementType.Parallel ) {
				ParallelDefinition pd = (ParallelDefinition)def;
				analyzeParallelSection(row,pd);	
			}
			else if( elementType == ElementDefinition.ElementType.Transition ) {
				analyzeTransition(row,def);
			}
		}	
	}
	
	// A transition is not a step definition. Simply use this to continue on with the next steps
	private void analyzeTransition(int row,ElementDefinition elementDef) {
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof( "%s.analyzeTransition: chart %d", TAG,row);

		for(ElementDefinition def:elementDef.getNextElements()) {
			ElementDefinition.ElementType elementType = def.getElementType();
			if( elementType == ElementDefinition.ElementType.Step )  {
				StepDefinition sd = (StepDefinition)def;
				PropertySet ps = sd.getProperties();
				String stepname = ps.get(ChartStepProperties.Name);
				analyzeStep(row,sd,stepname);
			}
			else if( elementType == ElementDefinition.ElementType.Parallel ) {
				ParallelDefinition pd = (ParallelDefinition)def;
				analyzeParallelSection(row,pd);	
			}
			else if( elementType == ElementDefinition.ElementType.Transition ) {
				analyzeTransition(row,def);
			}
		}	
	}

	// The proposed row cannot appear anywhere in the lineage.
	// Note that we are using a specific copy of the subtree.
	private boolean isLoop(int row, Integer parent) {
		boolean loop = false;
		//log.infof("%s.isLoop: Testing %d against %d", TAG,row,parent.intValue());
		while( parent!=null && parent.intValue()!=ROOT_ROW ) {
			if( parent.intValue()==row) {
				loop = true;
				break;
			}
			parent = lineage.get(parent);
		}
		return loop;
	}

	// Loop through the enclosing steps, creating new linkages.
	private void linkEnclosingNodes() {
		for( EnclosingStep step:enclosingSteps) {
			linkEnclosureStepToReferencedNode(step.getStepRow(),step);
		}
	}
	
	// Connect the stepRow to the node indicated on the enclosure.
	// @param step contains actual parent, actual step. These may or may not be the same node as 
	//       was found originally when the step was created.
	private void linkEnclosureStepToReferencedNode(int newStepRow,EnclosingStep step) {
		Integer refrow = rowLookup.get(step.getChartPath());
		if( refrow!=null ) {
			if( !isLoop(step.getParentRow(),refrow) ) {
				int count = nodes.getInt(refrow, BrowserConstants.CXNS);
				//log.infof("%s.linkEnclosureStepToReferencedNode: enclosure %d.%s(%s)->%d, count=%d", TAG,newStepRow,step.getStepName(),step.getChartPath(),refrow,count);
				if( count==0) {
					// We're good to go, just make the connection
					addEdgeTableRow(newStepRow,refrow);   // Increments connection count
				}
				else {
					// Create a copy, then copy the rest of its node hierarchy as a completely new linkage.
					int newrow = addNodeTableRow( step.getReferenceName(),nodes.getInt(refrow, BrowserConstants.RESOURCE));
					lineage.put(new Integer(newrow), new Integer(step.getParentRow()));
					addEdgeTableRow(newStepRow,newrow);
					populateTargetNode(newrow,refrow.intValue());
				}
				
			}
			else {
				nodes.setInt(step.getStepRow(), BrowserConstants.STATUS, BrowserConstants.STATUS_LOOP);
				log.warnf("%s.linkEnclosureStepToReferencedNode. Detected loop at node %s referenced by enclosure %d:%d:%s", TAG,
						step.getReferenceName(),step.getParentRow(),step.getStepRow(),step.getStepName());
			}
		}
		else {
			nodes.setInt(step.getStepRow(), BrowserConstants.STATUS, BrowserConstants.STATUS_PATH);
			log.warnf("%s.linkEnclosureStepToReferencedNode. Unable to find node %s referenced by enclosure %d:%d:%s", TAG,
					step.getReferenceName(),step.getParentRow(),step.getStepRow(),step.getStepName());
		}
	}
	
	// Recursively scan the edge hierarchy for children of the reference. When found create a clone
	// linked to the actual row.
	private void populateTargetNode(int actualRow,int referenceRow) {
		if( actualRow==referenceRow ) return;   // We're linked into an existing sub-tree.
		
		log.tracef("%s.populateTargetNode. %d:%d",TAG,actualRow,referenceRow);
		// Search edges for children. Make a separate list to avoid concurrent modifications
		List<Integer> childrenToClone = new ArrayList<>();
		int count = edges.getRowCount();
		int index = 0;
		while( index<count ) {
			// We're only interested in the parent ..
			int parent = edges.getInt(index, Graph.DEFAULT_SOURCE_KEY);
			if( parent==referenceRow) {
				// Create a copy, link to the parent and recurse.
				int sibling = edges.getInt(index, Graph.DEFAULT_TARGET_KEY);
				childrenToClone.add(new Integer(sibling));
			}
			index++;
		}
		
		for(Integer sibling:childrenToClone) {
			Integer newRow = addNodeTableRow( nodes.getString(sibling.intValue(),BrowserConstants.NAME),BrowserConstants.NO_RESOURCE);
			addEdgeTableRow(actualRow,newRow.intValue());
			populateTargetNode(newRow.intValue(),sibling.intValue());
		}
	}
	// Loop through all of the nodes. Set the full paths based on resourceId.
	private void resolvePaths() {
		if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof("%s.resolvePaths ...", TAG);
		int maxRow = nodes.getRowCount();
		int row = 0;
		while(row<maxRow) {
			long resid = nodes.getLong(row,BrowserConstants.RESOURCE);
			if( resid>=0 ) {
				String path = project.getFolderPath(resid);
				nodes.setString(row,BrowserConstants.PATH,path);
				if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof("%s.resolvePaths: %d.%d = %s", TAG,row,resid,path);
				rowLookup.put(path, new Integer(row));  // So that we can find this for links
			}
			else {
				String path = nodes.getString(row,BrowserConstants.NAME);
				nodes.setString(row,BrowserConstants.PATH,path);
				if( log.isDebugEnabled() || DEBUG_STRUCTURE ) log.infof("%s.resolvePaths: %d.%d = %s", TAG,row,resid,path);
				rowLookup.put(path, new Integer(row));  // So that we can find this for links
			}
			row++;
		}
	}
		
	// Loop through all of the nodes. If they haven't been connected to by anything,
	// then they should be connected to the root node.
	private void resolveRootConnections() {
		log.debugf("%s.resolveRootConnections ...", TAG);
		int maxRow = nodes.getRowCount();
		int row = 0;
		while(row<maxRow) {
			int connections = nodes.getInt(row,BrowserConstants.CXNS);
			if( connections==0 && row!=ROOT_ROW) {
				addEdgeTableRow(ROOT_ROW,row);
			}
			row++;
		}
	}

	/**
	 * Information about an enclosing step so that we can retrieve and
	 * create/replicate all enclosing links.
	 */
	public class EnclosingStep {
		private final String referenceName;
		private final String stepName;
		private final String chartPath;   // Path to chart pointed to by this step
		private final int    parentRow;
		private final int    stepRow;
		
		public EnclosingStep(int pRow,int sRow,String sName,String referencePath ) {
			this.parentRow = pRow;
			this.stepRow = sRow;
			this.referenceName = getLastPathElement(referencePath);
			this.stepName   = sName;
			this.chartPath = referencePath;
			log.debugf("EnclosingStep.constructor: %s %d (parent %d) at %s",stepName,stepRow,parentRow,chartPath);
		}
		
		public String getReferenceName() { return referenceName; }
		public String getStepName() { return stepName; }     // May be null
		public String getChartPath() { return chartPath; }
		public int getParentRow() { return parentRow; }
		public int getStepRow() { return stepRow; }
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
