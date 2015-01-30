package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class RecipeDataBrowser extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private static Icon addIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/add.png"));
	private static Icon removeIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/delete.png"));
	private static Icon editIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pencil.png"));
	final JButton addButton = new JButton(addIcon);
	final JButton editButton = new JButton(editIcon);
	final JButton removeButton = new JButton(removeIcon);
	private JTree tree;
	private DefaultTreeModel treeModel;
	private RecipeDataTreeNode selectedNode;
	private Data recipeData;
	private final RecipeEditorController controller;

	private static class RecipeDataTreeNode extends DefaultMutableTreeNode {
		private String label;
		
		public RecipeDataTreeNode(String label, Data data) {
			super(data);
			this.label = label;
		}
		 
		Data getRecipeData() {
			return (Data)getUserObject();
		}

		public String toString() {
			return label;
		}
		
		@Override
		public boolean isLeaf() {
			return getUserObject() == null;
		}
		
	}
	/** This ctor is just for testing */
	public RecipeDataBrowser() {
		setLayout(new BorderLayout());
		controller = null;
	}

	/** Do anything that needs to be done before re-showing this. */
	public void onShow() {
		rebuildTree();
	}

	public RecipeDataBrowser(RecipeEditorController controller) {
		this.controller = controller;
		setLayout(new BorderLayout());
	}
	
	public void setRecipeData(Data recipeData){
		this.recipeData = recipeData;
		rebuildTree();
	}
	
	private void createTree() {		
		// Check that the step exists--if it doesn't we are probably out of sync:
		treeModel = new DefaultTreeModel(null);
		addLayer(recipeData, null);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectedNode = (RecipeDataTreeNode) tree.getLastSelectedPathComponent();
				Data selectedData = selectedNode != null ? selectedNode.getRecipeData() : null;
				addButton.setEnabled(selectedData != null && selectedData.isGroup());
				removeButton.setEnabled(selectedData != null && selectedNode != treeModel.getRoot());
				editButton.setEnabled(selectedData != null && !selectedData.isGroup());
			}
		});		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	private void addLayer(Data recipeData, RecipeDataTreeNode parent) {
		String label = recipeData.getKey() + " (" + recipeData.getClass().getSimpleName() + ")";
		RecipeDataTreeNode node = new RecipeDataTreeNode(label, recipeData);
		if(recipeData.isGroup()) {
			Group group = (Group)recipeData;
			for(Data child: group.getChildren()) {
				addLayer(child, node);
			}
		}
		else {  // leaf node
			for(PropertyValue<?> pvalue: recipeData.getProperties().getValues()) {
				String key = pvalue.getProperty().getName();
				Object value = pvalue.getValue();
				String valueLabel = value != null ? value.toString() : "<null>";
				node.add(new RecipeDataTreeNode(key + "=" + valueLabel, null));
			}
		}
		if(parent == null) {
			treeModel.setRoot(node);
		}
		else {
			parent.add(node);
		}
	}
	
	private void createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		add(buttonPanel, BorderLayout.EAST);
		Dimension dimension = new Dimension(32,32);
		
		addButton.setEnabled(false);
		addButton.setPreferredSize(dimension);
		addButton.setBorder(null);
		buttonPanel.add(addButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAdd();}
		});
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		removeButton.setEnabled(false);
		removeButton.setPreferredSize(dimension);
		removeButton.setBorder(null);
		buttonPanel.add(removeButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doRemove();}
		});
		
		editButton.setEnabled(false);
		editButton.setPreferredSize(dimension);
		editButton.setBorder(null);
		buttonPanel.add(editButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doEdit();}
		});		
	}
	
	private void doAdd() {				
		controller.slideTo(controller.getCreator());
	}
	
	private void doRemove() {
		RecipeDataTreeNode parent = (RecipeDataTreeNode)selectedNode.getParent();
		Group group = (Group) parent.getRecipeData();
		group.getChildren().remove(selectedNode.getRecipeData());
		selectedNode = null;
	}

	private void doEdit() {
		controller.getEditor().getPropertyEditor().setPropertyValues(selectedNode.getRecipeData().getProperties(), false);
		controller.slideTo(controller.getEditor());
	}

	/** Rebuild the tree in response to a change in recipe data. */
	public void rebuildTree() {
		this.removeAll();
		createButtonPanel();				
		createTree();
		validate();	
	}
	
	/** Set the properties on the currently selected recipe data object--typically follows editing. */
	public void setSelectedProperties(BasicPropertySet propertyValues) {
		selectedNode.getRecipeData().setProperties(propertyValues);
	}

	/** Add a new object under the selected node, and select the node. */
	public void add(Data newObject) {
		((Group)selectedNode.getRecipeData()).getChildren().add(newObject);
	}

}
