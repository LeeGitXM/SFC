package com.ils.sfc.designer.exim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;

import net.miginfocom.swing.MigLayout;

class ChartSelectionPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String ROOT_NAME = "Sequential Function Charts";
	private final DesignerContext context;
	private final JTree chartTree;
	private final ChartCellRenderer cellRenderer;
	public static final Dimension TREE_SIZE = new Dimension(600,500);
	private SimpleHierarchyAnalyzer hierarchyAnalyzer = null;
	private TreeSelectionModel chartSelectionModel;
	private DefaultMutableTreeNode root;
	
	// The constructor
	public ChartSelectionPane(DesignerContext ctx) {
		super(new BorderLayout(20, 30));
		this.context = ctx;

		JPanel mainPanel = new JPanel(new MigLayout("", "[right]"));
		this.cellRenderer = new ChartCellRenderer();
		setLayout(new BorderLayout());
		
		root = new DefaultMutableTreeNode(ROOT_NAME);
		createNodes(root);
		chartTree = new JTree(root);
		chartTree.setOpaque(true);
		chartTree.setCellRenderer(cellRenderer);
		chartTree.setBackground(getBackground());
		chartSelectionModel = chartTree.getSelectionModel();
		chartSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		cellRenderer.setBackgroundSelectionColor(Color.cyan);
		cellRenderer.setBackgroundNonSelectionColor(getBackground());
		JScrollPane treePane = new JScrollPane(chartTree);
		treePane.setPreferredSize(TREE_SIZE);
		mainPanel.add(treePane,BorderLayout.CENTER);
		add(mainPanel,BorderLayout.CENTER);
	}
	
	public JTree getTree() { return this.chartTree; }
	public TreeSelectionModel getSelectionModel() { return this.chartSelectionModel; }
	public SimpleHierarchyAnalyzer getAnalyzer() { return this.hierarchyAnalyzer; }
	
	// Traverse the hierarchy
	private void createNodes(DefaultMutableTreeNode root) {
		SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		hierarchyAnalyzer = new SimpleHierarchyAnalyzer(context.getGlobalProject().getProject(),stepRegistry);
		hierarchyAnalyzer.analyze();
		for( String path:hierarchyAnalyzer.getChartPaths()) {
			System.out.println(String.format("ChartSelectionPane.createNodes: %s",path));
			String partial = ROOT_NAME;
			DefaultMutableTreeNode node = root;
			String[] components = path.split("/");
			int index = 0;
			for(String component:components) {
				index++;
				partial = partial + "/" + component;
				ChartTreeNode partialNode = new ChartTreeNode(partial,index<components.length); // Leaf does not allow children
				// Add to node if it doesn't already exist
				@SuppressWarnings("unchecked")
				Enumeration<ChartTreeNode> childWalker = node.children();
				boolean exists = false;
				while(childWalker.hasMoreElements()) {
					ChartTreeNode child = childWalker.nextElement();
					if( child.getUserObject().equals(partial) ) {
						exists = true;
						node = child;
						break;
					}
				}
				if( !exists ) {
					node.add(partialNode);
					node = partialNode;
				}
			}
		}
	}
}
