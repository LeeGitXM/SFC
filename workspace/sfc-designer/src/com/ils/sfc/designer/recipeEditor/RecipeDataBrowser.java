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
import javax.swing.JOptionPane;
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
import com.ils.sfc.common.recipe.RecipeDataMap;
import com.ils.sfc.common.recipe.objects.S88RecipeData;
import com.ils.sfc.common.recipe.objects.S88RecipeDataGroup;
import com.ils.sfc.util.IlsSfcNames;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;

@SuppressWarnings("serial")
public class RecipeDataBrowser extends JPanel {
	private static Icon addIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/add.png"));
	private static Icon removeIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/delete.png"));
	private static Icon editIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pencil.png"));
	final JButton addButton = new JButton(addIcon);
	final JButton editButton = new JButton(editIcon);
	final JButton removeButton = new JButton(removeIcon);
	private JTree tree;
	private DefaultTreeModel treeModel;
	private RecipeDataTreeNode selectedNode;
	private Map<String,Object> recipeData;
	private final RecipeEditorController controller;

	@SuppressWarnings("serial")
	private static class RecipeDataTreeNode extends DefaultMutableTreeNode {
		private String name;
		private boolean isFolder;
		
		public RecipeDataTreeNode(String name, Map<String,Object> map, boolean isFolder) {
			super(map);
			this.name = name;
			this.isFolder = isFolder;
		}
		
		@SuppressWarnings("unchecked") 
		Map<String,Object> getMap() {
			return (Map<String,Object>)getUserObject();
		}

		public boolean isFolder() {return isFolder;}

		//public boolean isLeaf() { return getUserObject() == null; }
		
		public String getName() {
			return name;
		}

		public String toString() {
			return name;
		}
		
	}
	/** This ctor is just for testing */
	public RecipeDataBrowser() {
		setLayout(new BorderLayout());
		controller = null;
	}

	public RecipeDataBrowser(RecipeEditorController controller) {
		this.controller = controller;
		setLayout(new BorderLayout());
	}
	
	public void setRecipeData(Map<String,Object> recipeData){
		this.recipeData = recipeData;
		this.removeAll();
		createButtonPanel();				
		createTree();
		validate();
		repaint();
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
				boolean nodeIsSelected = selectedNode != null;
				addButton.setEnabled(nodeIsSelected && selectedNode.isFolder());
				removeButton.setEnabled(nodeIsSelected && !selectedNode.isLeaf());
				editButton.setEnabled(nodeIsSelected && !selectedNode.isFolder() && !selectedNode.isLeaf());
			}
		});		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	@SuppressWarnings("unchecked")
	private void addLayer(Map<String,Object> map, RecipeDataTreeNode parent) {
		String name = (String)map.get(IlsSfcNames.KEY);
		boolean isFolder = S88RecipeDataGroup.className.equals(map.get(IlsSfcNames.CLASS));
		RecipeDataTreeNode node = null;
		if(isFolder) {
			node = new RecipeDataTreeNode(name, map, true);
			List<Map<String,Object>> children = (List<Map<String,Object>>)map.get(IlsSfcNames.CHILDREN);
			for(Map<String,Object> childMap: children) {
				addLayer(childMap, node);
			}
		}
		else {  // leaf node
			node = new RecipeDataTreeNode(name, map, false);
			for(String key: map.keySet()) {
				Object value = map.get(key);
				node.add(new RecipeDataTreeNode(key + "=" + value, null, false));
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
	
	public void addToSelectedNode(Map<String,Object> map) {
		if(selectedNode.getChildCount() == 1) {
			// make the new subtree visible
			tree.makeVisible(new TreePath(((RecipeDataTreeNode)selectedNode.getChildAt(0)).getPath()));
		}
		tree.updateUI();		
	}
	
	private void doAdd() {				
		controller.slideToCreator();
	}
	
	private void doRemove() {
		RecipeDataTreeNode parent = (RecipeDataTreeNode)selectedNode.getParent();
		parent.getMap().remove(selectedNode.getName());
		treeModel.removeNodeFromParent(selectedNode);
		tree.updateUI();
		selectedNode = null;
	}

	private void doEdit() {
		boolean inputValid = false;
		do {
			Object currentValue = selectedNode.getUserObject();
			String propertyName = selectedNode.getName();
			String newValueString = null;
			if(currentValue instanceof String) {
				//RecipeStringEditorDialog dlg = new RecipeStringEditorDialog(null, (String)currentValue, "Edit " + propertyName);
				//dlg.setVisible(true);
				//newValueString = dlg.getResult();
			}
			else {
				newValueString = JOptionPane.showInputDialog(this, "Enter new value for " + propertyName, currentValue);
			}
			if(newValueString != null) {
				Object newValue = getValidValue(newValueString.trim(), currentValue);
				if(newValue != null) {
					selectedNode.setUserObject(newValue);
					inputValid = true;
				}
				else {
					JOptionPane.showMessageDialog(this, "This value is not valid for type " + currentValue.getClass().getSimpleName());
				}
			}
			else {
				inputValid = true; // cancelled
			}
		} while(!inputValid);
		tree.updateUI();
	}

	private Object getValidValue(String newValueString, Object currentValue) {
		if(currentValue instanceof String) {
			return newValueString;
		}
		else if(currentValue instanceof Double) {
			try {
				return Double.parseDouble(newValueString);
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		else if(currentValue instanceof Integer) {
			try {
				return Integer.parseInt(newValueString);
			}
			catch(NumberFormatException e) {
				return null;
			}			
		}
		else if(currentValue instanceof Boolean) {
			return Boolean.parseBoolean(newValueString);			
		}
		else {
			return null; // keep the compiler happy
		}
	}

}
