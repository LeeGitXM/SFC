package com.ils.sfc.designer;

/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.inductiveautomation.ignition.client.sqltags.tree.TagRenderer;
import com.inductiveautomation.ignition.client.sqltags.tree.TagTreeNode;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Display a panel to find and select tag paths.   
 */
public class TagBrowser extends JPanel {
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
	
	/** Get the selected tag path, or null if none is selected. */
	public String getTagPath() {
		TreePath[] selectedPaths = tagTreeSelectionModel.getSelectionPaths();
		if(selectedPaths.length == 1) {
			// It's possible to select something that's not a node.
			if(selectedPaths[0].getLastPathComponent() instanceof TagTreeNode ) {
				TagTreeNode node = (TagTreeNode)(selectedPaths[0].getLastPathComponent());
				selectedPath = node.getTagPath().toString();			
				return selectedPath;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
}

