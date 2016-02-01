package com.ils.sfc.designer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.workspace.editors.ChartPathComboBox;

public class DesignerUtil {
	static DesignerContext context; 
	
	public static ChartPathComboBox getChartPathComboBox(String currentValue) {
		ChartPathComboBox cb = new ChartPathComboBox();
		Project globalProject = context.getGlobalProject().getProject();
		cb.initRoot(globalProject);
		cb.setSelectedItem(currentValue);
		cb.setBorder(new EmptyBorder(0,0,0,0));
		return cb;
	}
	
	public static void setConstraints(GridBagConstraints con, int anchor, int fill,
		int gridheight, int gridwidth, int gridx, int gridy, Insets insets, double weightx, double weighty ) {
		con.anchor = anchor;
		con.fill = fill;
		con.gridheight = gridheight;
		con.gridwidth = gridwidth;
		con.gridx = gridx;
		con.gridy = gridy;
		con.insets = insets;
		con.weightx = weightx;
		con.weighty = weighty;
	}

	/** Collapse all nodes in a tree. */
	public static void collapseTree(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		collapseAll(tree, new TreePath(root));
	}

	private static void collapseAll(JTree tree, TreePath parentPath) {
		TreeNode node = (TreeNode) parentPath.getLastPathComponent();
	    if (node.getChildCount() > 0) {
	    	for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
	    		TreeNode n = (TreeNode) e.nextElement();
	    		TreePath path = parentPath.pathByAddingChild(n);
	    		collapseAll(tree, path);
	    	}
	    }
	    tree.collapsePath(parentPath);
	 }

	/** Set the selected tree path and make (only) it visible */
	public static void setSelectedTreePath(JTree tree, TreePath treePath) {
		tree.clearSelection();
		// The current implementation of collapseTree causes big performance
		// problems on Pete's machine, possibly because he has a lot of tags.
		// we remove the close-existing-opened-paths functionality until
		// we can figure out a more efficient way to do it...
		//DesignerUtil.collapseTree(tree);
		tree.expandPath(treePath);
		tree.scrollPathToVisible(treePath);
		tree.getSelectionModel().setSelectionPath(treePath);
	}

}
