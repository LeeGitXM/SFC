package com.ils.sfc.designer;

/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.common.IlsClientScripts;
import com.inductiveautomation.ignition.client.sqltags.tree.AbstractTagPathTreeNode;
import com.inductiveautomation.ignition.client.sqltags.tree.TagPathTreeNode;
import com.inductiveautomation.ignition.client.sqltags.tree.TagRenderer;
import com.inductiveautomation.ignition.client.sqltags.tree.TagTreeNode;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Display a panel to find and select tag paths.   
 */
public class TagBrowser extends JPanel {
	private static LoggerEx logger = LogUtil.getLogger(TagBrowser.class.getName());
	private static final long serialVersionUID = 1L;
	private final DesignerContext context;
	private String selectedPath = "";
	private final JTree tagTree;
	private final TagRenderer cellRenderer;
	private final TreeSelectionModel tagTreeSelectionModel;
	
	public TagBrowser(DesignerContext ctx) {
		this.context = ctx;
		this.cellRenderer = new TagRenderer();
		setLayout(new BorderLayout());
		tagTree = new JTree();
		tagTree.setOpaque(true);
		tagTree.setCellRenderer(cellRenderer);
		tagTree.setModel(context.getTagBrowser().getSqlTagTreeModel());
		tagTreeSelectionModel = tagTree.getSelectionModel();
		tagTreeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tagTree.setBackground(getBackground());
		cellRenderer.setBackgroundSelectionColor(Color.cyan);
		cellRenderer.setBackgroundNonSelectionColor(getBackground());
		JScrollPane treePane = new JScrollPane(tagTree);
		//treePane.setPreferredSize(BlockEditConstants.TREE_SIZE);
		add(treePane,BorderLayout.CENTER);		
	}	
	
	public void setSelectedTagPath(String tagPathString) {
		List<String> names = new ArrayList<String>();
		names.add("All Providers");
		String provider = IlsClientScripts.getProviderName(false);
		names.add(provider);
		String[] parts = tagPathString.split("/|\\.");
		for(String part: parts) {
			names.add(part);
		}
		TagPathTreeNode node = (AbstractTagPathTreeNode)tagTree.getModel().getRoot();
		TreePath treePath = new TreePath(node);
		for(String name: names) {
			node.blockLoad();  // synchronously populate the children
			node = node.findChildNodeByName(name);
			if(node != null) {
				treePath = treePath.pathByAddingChild(node);
			}
			else {
				break;
			}
		}
		DesignerUtil.setSelectedTreePath(tagTree, treePath);
	}
	
	/** Get the selected tag path, or null if none is selected. */
	public String getTagPath() {
		TreePath[] selectedPaths = tagTreeSelectionModel.getSelectionPaths();
		if(selectedPaths.length == 1) {
			Object lastPathComponent = selectedPaths[0].getLastPathComponent();
			// It's possible to select something that's not a node.
			if(lastPathComponent instanceof TagPathTreeNode ) {
				TagTreeNode node = (TagTreeNode)lastPathComponent;
				selectedPath = node.getTagPath().toString();			
				return selectedPath;
			}
			/*
			else if(lastPathComponent instanceof TagPropNode ) {
				TagPropNode node = (TagPropNode)lastPathComponent;
				selectedPath = node.getTagPath().toString() + "." + node.getName();			
				return selectedPath;
			}
			*/
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
}

