package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.recipe.RecipeData;
import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.recipe.RecipeDataMap;

@SuppressWarnings("serial")
public class RecipeDataBrowser extends JPanel {
	private static Icon addIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/add.png"));
	private static Icon removeIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/delete.png"));
	private static Icon editIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pencil.png"));
	final JButton addButton = new JButton(addIcon);
	final JButton editButton = new JButton(editIcon);
	final JButton removeButton = new JButton(removeIcon);
	private RecipeDataTreeNode top = new RecipeDataTreeNode("", null);
	private JTree tree;
	private DefaultTreeModel treeModel;
	private RecipeDataTreeNode selectedNode;
	private RecipeData recipeData;
	private RecipeDataMap stepDataMap;
	private final RecipeEditorController controller;

	/** This ctor is just for testing */
	public RecipeDataBrowser(RecipeDataMap stepDataMap ) {
		setLayout(new BorderLayout());
		this.stepDataMap = stepDataMap;
		controller = null;
		createButtonPanel();				
		createTree();
	}

	public RecipeDataBrowser(RecipeEditorController controller) {
		this.controller = controller;
		setLayout(new BorderLayout());
		recipeData = RecipeDataManager.getData();
	}
	
	public void setStep(String stepId) {
		setStepDataMap(recipeData.getStepData(stepId));
	}
	
	public void setStepDataMap(RecipeDataMap map){
		stepDataMap = map;
		this.removeAll();
		createButtonPanel();				
		createTree();
		validate();
		repaint();
	}
	
	private void createTree() {		
		// Check that the step exists--if it doesn't we are probably out of sync:
		top.setContentsEditable(true);
		treeModel = new DefaultTreeModel(top);
		
		addNodes(stepDataMap, top);	
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectedNode = (RecipeDataTreeNode) tree.getLastSelectedPathComponent();
				addButton.setEnabled(canAdd());
				removeButton.setEnabled(canRemove());
				editButton.setEnabled(canEdit());
			}
		});		
		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}
	
	@SuppressWarnings("unchecked")
	private void addNodes(Map<String,Object> data, RecipeDataTreeNode parent) {
		for(String key: data.keySet()) {
			Object value = data.get(key);
			RecipeDataTreeNode child = new RecipeDataTreeNode(key, value);
			parent.add(child);
			if(value instanceof Map) {
				addNodes((Map<String,Object>)value, child);
			}
			else {
				child.add(new RecipeDataTreeNode(value.toString(), value));
			}
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
		// TODO: slide the pane, new pane can call this to get selected node etc
		if(selectedNode.getChildCount() == 1) {
			// make the new subtree visible
			tree.makeVisible(new TreePath(((RecipeDataTreeNode)selectedNode.getChildAt(0)).getPath()));
		}
		tree.updateUI();
	}

	private boolean canAdd() {
		return selectedNode != null && selectedNode.contentsAreEditable();
	}
	
	private boolean canRemove() {
		return selectedNode != null && selectedNode != top &&
			((RecipeDataTreeNode)selectedNode.getParent()).contentsAreEditable();
	}
	
	private boolean canEdit() {
		return selectedNode != null && selectedNode.valueIsEditable();
	}
	
	private void doRemove() {
		int response = JOptionPane.showConfirmDialog(this, "Do you really want to remove " + selectedNode + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
		if(response == JOptionPane.NO_OPTION) return;
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

	public static void main(String[] args) {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		RecipeDataMap map = new RecipeDataMap();
		map.put("key", "value");
		RecipeDataBrowser browser = new RecipeDataBrowser(map);
		frame.setContentPane(browser);
		frame.setSize(200,200);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
