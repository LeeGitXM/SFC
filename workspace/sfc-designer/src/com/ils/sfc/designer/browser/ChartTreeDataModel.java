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
	// Table column names
	private static final String ID       = "Id";       // Chart UUID as String
	private static final String NAME     = "Name";
	private static final String PARENT   = "Parent";
	private static final String RESOURCE = "Resource"; // ResourceId
	private final Map<String,Integer> idMap;                 // index given UUID string
	private final List<LinkHolder> links;    // link elements
	
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final Table nodes;
	private final Table edges;
	private int nodeCount = 0;
	
	public ChartTreeDataModel(DesignerContext ctx) {
		context = ctx;
		idMap = new HashMap<>();
		links = new ArrayList<>();
		nodes = new Table();
		nodes.addColumn(Graph.DEFAULT_NODE_KEY, int.class,new Integer(0));   // Table row
		nodes.addColumn(ID, String.class);
		nodes.addColumn(RESOURCE, long.class);
		nodes.addColumn(NAME, String.class);
		
		edges = new Table();
		// The keys match the node key in the node table
		// The node direction is from parent to child.
		edges.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class,new Integer(0));
		edges.addColumn(Graph.DEFAULT_TARGET_KEY, int.class,new Integer(0));
		
		initialize();
	}

	/**
	 * Initialize the UI
	 */
	private void initialize() {
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResources();
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				log.infof("%s.initialize: found chart %s", TAG,res.getName());
				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
				}
				catch(IOException ioe ) {
					log.warnf("%s.initialize: IO Exception for %s (%s)", TAG,res.getName(),ioe.getLocalizedMessage());
				}
				catch(XMLParseException xpe ) {
					log.warnf("%s.initialize: Parse Exception for %s (%s)", TAG,res.getName(),xpe.getLocalizedMessage());
				}
			}
		}
		// Make table legal if it was empty
		if(nodes.getRowCount()==0)  configureNodesAsEmpty();
	}
	
	/**
	 * @return the chart resources as a tree.
	 */
	public Tree getTree() {
		return new Tree(nodes,edges,Graph.DEFAULT_NODE_KEY,Graph.DEFAULT_SOURCE_KEY,Graph.DEFAULT_TARGET_KEY);
	}
	
	// Configure the nodes table to display something reasonable
	// if it is otherwise empty.
	private void configureNodesAsEmpty() {
		nodes.addRow();
		nodes.setInt(0,Graph.DEFAULT_NODE_KEY,0);
		nodes.setString(0,ID,"Not-a-uuid");
		nodes.setLong(0,RESOURCE,-1);
		nodes.setString(0,NAME,"No charts");
	}
	/**
	 * Class to hold UI element link information.
	 */
	private class LinkHolder {
		public LinkHolder(String parentId,ChartUIElement element) {
			
		}
		
	}
	
}
