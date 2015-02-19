package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/** Provide a tree view of all recipe data. */
@SuppressWarnings("serial")
public class RecipeBrowserPane extends JPanel implements EditorPane {
	private ButtonPanel buttonPanel = new ButtonPanel(false, true, true, true, false, true);
	private JTree tree;
	private JScrollPane treeScroll;
	private DefaultTreeModel treeModel;
	private RecipeDataTreeNode selectedNode;
	private RecipeEditorController controller;
	private RecipeDataTreeNode rootNode;
	private boolean showLeafNodes = false;

	/** Helper class to show recipe data objects as tree nodes. */
	private class RecipeDataTreeNode extends DefaultMutableTreeNode {
				 
		public RecipeDataTreeNode(Data data) {
			super(data);
		}
		
		Data getRecipeData() {
			return (Data)getUserObject();
		}

		public String toString() {
			return getRecipeData().getKey();
		}
		
		@Override
		public boolean isLeaf() {
			return showLeafNodes ? false : !getRecipeData().isGroup();
		}
		
	}

	/** This constructor is for development/testing use */
	public RecipeBrowserPane(Data data) {
		rootNode = new RecipeDataTreeNode(data);
		showLeafNodes = true;
		setLayout(new BorderLayout());
		createTree();
		validate();	
	}
	
	public RecipeBrowserPane(RecipeEditorController controller) {
		this.controller = controller;
		setLayout(new BorderLayout());
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAdd();}
		});
		buttonPanel.getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doRemove();}
		});
		buttonPanel.getEditButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doEdit();}
		});		
		buttonPanel.getComboLabel().setText("S88Level:");
		for(String level: IlsSfcNames.S88_LEVEL_CHOICES) {
			buttonPanel.getComboBox().addItem(level);
		}
		buttonPanel.getComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doSetS88Level();}
		});		
	}
	
	private void createTree() {		
		selectedNode = rootNode;
		rootNode.removeAllChildren();
		treeModel = new DefaultTreeModel(rootNode);
		addLayer(rootNode);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				setButtonState();
			}
		});		
		treeScroll = new JScrollPane(tree);
		this.add(treeScroll, BorderLayout.CENTER);
		setButtonState();
	}
	
	/** Enable/Disable buttons based on tree selection. */
	private void setButtonState() {
		selectedNode = (RecipeDataTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null) selectedNode = rootNode;
		Data selectedData = selectedNode != null ? selectedNode.getRecipeData() : null;
		buttonPanel.getAddButton().setEnabled(selectedData != null && selectedData.isGroup());
		buttonPanel.getRemoveButton().setEnabled(selectedData != null && selectedNode != treeModel.getRoot());
		buttonPanel.getEditButton().setEnabled(selectedData != null && !selectedData.isGroup());
	}
	
	/** Recursively add a layer of nested recipe data to the tree. */
	private void addLayer(RecipeDataTreeNode node) {
		if(showLeafNodes) {
			for(PropertyValue<?> pval: node.getRecipeData().getProperties().getValues()) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(formatValue(pval));
				node.add(childNode);
			}			
		}
		if(node.getRecipeData().isGroup()) {
			Group group = (Group)node.getRecipeData();
			for(Data child: group.getChildren()) {
				RecipeDataTreeNode childNode = new RecipeDataTreeNode(child);
				node.add(childNode);
				addLayer(childNode);
			}
		}
	}
	
	private Object formatValue(PropertyValue<?> pval) {
		Object value = pval.getValue();
		if(value instanceof double[][]) {
			StringBuilder buf = new StringBuilder();
			double[][] arrValue = (double[][]) value;
			for(int i = 0; i < arrValue.length; i++) {
				buf.append('[');
				for(int j = 0; j < arrValue[i].length; j++) {
					if(j > 0) {
						buf.append(", ");
					}
					buf.append(Double.toString(arrValue[i][j]));
				}	
				buf.append(']');
			}
			return buf.toString();
		}
		else {
			String valDesc = pval.getValue() != null ?
				value.toString() + " (" + value.getClass().getSimpleName() + ")" : "<null>";
			String desc = pval.getProperty().getName() + ": " + valDesc;
			return desc;
		}
	}

	private void doAdd() {				
		controller.getCreator().activate();
	}
	
	private void doRemove() {
		RecipeDataTreeNode parent = (RecipeDataTreeNode)selectedNode.getParent();
		if(parent.getRecipeData() != null) {
			Group group = (Group) parent.getRecipeData();
			group.getChildren().remove(selectedNode.getRecipeData());
		}
		// need to rebuild explicitly since we're not sliding away + back
		rebuildTree();
	}

	private void doEdit() {
		//controller.getEditor().getPropertyEditor().setPropertyValues(selectedNode.getRecipeData().getProperties(), false);
		controller.getEditor().setRecipeData(selectedNode.getRecipeData());
		controller.getEditor().activate();
	}

	private void doSetS88Level() {
		controller.getRecipeData().setS88Level((String)buttonPanel.getComboBox().getSelectedItem());
	}
	
	/** Rebuild the tree in response to a change in recipe data. */
	public void rebuildTree() {
		if(controller.getRecipeData().getS88Level() != null) {
			buttonPanel.getComboBox().setSelectedItem(
				controller.getRecipeData().getS88Level());
		}
		else {
			buttonPanel.getComboBox().setSelectedItem(IlsSfcNames.NONE);
		}
		if(treeScroll != null) {
			this.remove(treeScroll);	
		}
		rootNode = new RecipeDataTreeNode(controller.getRecipeData());
		createTree();
		validate();	
	}
	
	/** Add a new object under the selected node, and select the node. */
	public void add(Data newObject) {
		((Group)selectedNode.getRecipeData()).getChildren().add(newObject);
	}

	@Override
	public void activate() {
		rebuildTree();
		controller.slideTo(RecipeEditorController.BROWSER);
	}

}
