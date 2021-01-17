package com.ils.sfc.designer.exim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;

import com.inductiveautomation.ignition.client.sqltags.tree.TagRenderer;
import com.inductiveautomation.ignition.client.sqltags.tree.TagTreeNode;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

class ChartSelectionPane extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final DesignerContext context;
	private final JTree chartTree;
	private final TagRenderer cellRenderer;
	public static final Dimension TREE_SIZE = new Dimension(600,500);
	
	
	// The constructor
	public ChartSelectionPane(DesignerContext ctx) {
		super(new BorderLayout(20, 30));
		this.context = ctx;
		System.out.println("In ChartSelector pane constructor");
		JPanel mainPanel = new JPanel(new MigLayout("", "[right]"));
		
		this.cellRenderer = new TagRenderer();
		setLayout(new BorderLayout());
		chartTree = new JTree();
		chartTree.setOpaque(true);
		chartTree.setCellRenderer(cellRenderer);
		//chartTree.setModel(context.getTagBrowser().getSqlTagTreeModel());
		TreeSelectionModel tagTreeSelectionModel;tagTreeSelectionModel = chartTree.getSelectionModel();
		tagTreeSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		chartTree.setBackground(getBackground());
		cellRenderer.setBackgroundSelectionColor(Color.cyan);
		cellRenderer.setBackgroundNonSelectionColor(getBackground());
		JScrollPane treePane = new JScrollPane(chartTree);
		treePane.setPreferredSize(TREE_SIZE);
		mainPanel.add(treePane,BorderLayout.CENTER);
		add(mainPanel,BorderLayout.CENTER);
	}
}
