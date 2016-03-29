package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/** Provide a tree view of all recipe data. */
@SuppressWarnings("serial")
public class RecipeBrowserPane extends EditorPanel implements ValueHolder {
	private ButtonPanel buttonPanel = new ButtonPanel(false, true, true, true, false, false, false, false);
	private JTree tree;
	private JScrollPane treeScroll;
	private DefaultTreeModel treeModel;
	private RecipeDataTreeNode selectedNode;
	private RecipeDataTreeNode rootNode = new RecipeDataTreeNode(null);
	private boolean showLeafNodes = false;
	private RecipeEditorController controller;

	/** Helper class to show recipe data objects as tree nodes. */
	private class RecipeDataTreeNode extends DefaultMutableTreeNode {
				 
		public RecipeDataTreeNode(Data data) {
			super(data);
		}
		
		Data getRecipeData() {
			return (Data)getUserObject();
		}

		public String toString() {
			return getRecipeData().getLabel();
		}
		
		@Override
		public boolean isLeaf() {
			return showLeafNodes ? false : !getRecipeData().isGroup();
		}
		
	}
	
	public RecipeBrowserPane(RecipeEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		rootNode.setUserObject(new Group());  // set some dummy data to prevent NPEs
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
	}
	
	private void createTree(List<Data> recipeData) {		
		selectedNode = null;
		rootNode.removeAllChildren();
		treeModel = new DefaultTreeModel(rootNode);
		Collections.sort(recipeData, new Comparator<Data>() {
			@Override
			public int compare(Data d1, Data d2) {
				return d1.getKey().toLowerCase().compareTo(d2.getKey().toLowerCase());
			}			
		});
		for(Data data: recipeData) {
			addLayer(rootNode, data);
		}
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
	
	private Data getSelectedData() {
		selectedNode = (RecipeDataTreeNode) tree.getLastSelectedPathComponent();
		return selectedNode != null ? selectedNode.getRecipeData() : null;
	}
	
	/** Enable/Disable buttons based on tree selection. */
	private void setButtonState() {
		Data selectedData = getSelectedData();
		buttonPanel.getAddButton().setEnabled(selectedData == null || selectedData.isGroup());
		buttonPanel.getRemoveButton().setEnabled(selectedData != null);
		buttonPanel.getEditButton().setEnabled(selectedData != null);
	}
	
	/** Recursively add a layer of nested recipe data to the tree. */
	private void addLayer(DefaultMutableTreeNode parentNode, Data data) {
		RecipeDataTreeNode childNode = new RecipeDataTreeNode(data);
		parentNode.add(childNode);
		if(showLeafNodes) {
			for(PropertyValue<?> pval: data.getProperties().getValues()) {
				DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(formatValue(pval));
				childNode.add(leafNode);
			}			
		}
		if(data.isGroup()) {
			Group group = (Group)data;
			for(Data childData: group.getChildren()) {
				addLayer(childNode, childData);
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
		Data parent = selectedNode!= null ? selectedNode.getRecipeData() : null;
		controller.getCreator().activate(this, parent);
	}
	
	private void doRemove() {
		RecipeDataTreeNode parent = (RecipeDataTreeNode)selectedNode.getParent();
		if(parent == rootNode) {
			controller.getRecipeData().remove(selectedNode.getRecipeData());
		}
		else if(parent.getRecipeData() != null) {
			Group group = (Group) parent.getRecipeData();
			group.getChildren().remove(selectedNode.getRecipeData());
		}
		selectedNode.getRecipeData().deleteTag();
		controller.commit();
		// need to rebuild explicitly since we're not sliding away + back
		rebuildTree();
	}

	private void doEdit() {
		//controller.getEditor().getPropertyEditor().setPropertyValues(selectedNode.getRecipeData().getProperties(), false);
		controller.getEditor().setRecipeData(selectedNode.getRecipeData());
		controller.getEditor().activate(myIndex);
	}

	/** Rebuild the tree in response to a change in recipe data. */
	public void rebuildTree() {
		if(controller.getRecipeData() == null) return;
		if(treeScroll != null) {
			this.remove(treeScroll);	
		}
		createTree(controller.getRecipeData());
		validate();	
		repaint();
	}
	
	@Override
	public void activate(int returnIndex) {
		rebuildTree();
		super.activate(returnIndex);
	}

	@Override
	public void setValue(Object value) {
		Data newData = (Data) value;
		Data selectedData = getSelectedData();
		if(selectedData != null && selectedData.isGroup()) {
			((Group)selectedData).addChild(newData);
		}
		else {
			controller.getRecipeData().add(newData);
		}
		rebuildTree();
	}

}
