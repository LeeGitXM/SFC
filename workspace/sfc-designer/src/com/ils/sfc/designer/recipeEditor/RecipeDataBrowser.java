package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import com.ils.sfc.util.RecipeDataManager;;

public class RecipeDataBrowser extends JPanel {
	private MapTreeNode top;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private Map<String,Object> data;
	private MapTreeNode selectedNode;
	
	@SuppressWarnings("serial")
	static class MapTreeNode extends DefaultMutableTreeNode {
		private String name;
		private boolean isMap;
		private boolean contentsAreEditable;
		
		public MapTreeNode(String name, Object userObject) {
			super(userObject);
			isMap = userObject instanceof Map;
			this.name = name;
		}
		
		@SuppressWarnings("unchecked") Map<String,Object> getMap() {
			return (Map<String,Object>)getUserObject();
		}
		
		public boolean valueIsEditable() {
			return !name.equals(RecipeDataTypes.TYPE) && !isMap; // i.e. value is primitive type
		}
		
		public boolean contentsAreEditable() {
			return contentsAreEditable;
		}

		public void setContentsEditable(boolean isEditable) {
			this.contentsAreEditable = isEditable;
		}

		public String toString() {
			StringBuilder buf = new StringBuilder();
			buf.append(name);
			if(isMap) {
				String type = (String)getMap().get("type");
				if(type != null) {
					buf.append(" (");
					buf.append(type);
					buf.append(")");
				}
			}
			else {
				buf.append(" = ");
				buf.append(getUserObject().toString());
			}
			return buf.toString();
		}
	}
	
	public RecipeDataBrowser(String topName) {
		top = new MapTreeNode(topName, new HashMap<String,Object>());
		top.setContentsEditable(true);
		treeModel = new DefaultTreeModel(top);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		this.setLayout(new BorderLayout());
		add(new JScrollPane(tree), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		add(buttonPanel, BorderLayout.SOUTH);
		final JButton addButton = new JButton("+");
		addButton.setEnabled(false);
		buttonPanel.add(addButton);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAdd();}
		});
		final JButton removeButton = new JButton("-");
		buttonPanel.add(removeButton);
		removeButton.setEnabled(false);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doRemove();}
		});
		final JButton editButton = new JButton("E");
		editButton.setEnabled(false);
		buttonPanel.add(editButton);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doEdit();}
		});
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectedNode = (MapTreeNode) tree.getLastSelectedPathComponent();
				addButton.setEnabled(canAdd());
				removeButton.setEnabled(canRemove());
				editButton.setEnabled(canEdit());
			}
		});
	}
	
	private void doAdd() {
		CreateRecipeDataDialog dlg = new CreateRecipeDataDialog(treeModel, selectedNode);
		dlg.setVisible(true);
		if(selectedNode.getChildCount() == 1) {
			// make the new subtree visible
			tree.makeVisible(new TreePath(((MapTreeNode)selectedNode.getChildAt(0)).getPath()));
		}
		tree.updateUI();
	}

	private boolean canAdd() {
		return selectedNode != null && selectedNode.contentsAreEditable();
	}
	
	private boolean canRemove() {
		return selectedNode != null && selectedNode != top &&
			((MapTreeNode)selectedNode.getParent()).contentsAreEditable();
	}
	
	private boolean canEdit() {
		return selectedNode != null && selectedNode.valueIsEditable();
	}
	
	private void doRemove() {
		int response = JOptionPane.showConfirmDialog(this, "Do you really want to remove " + selectedNode + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
		if(response == JOptionPane.NO_OPTION) return;
		MapTreeNode parent = (MapTreeNode)selectedNode.getParent();
		parent.getMap().remove(selectedNode.name);
		treeModel.removeNodeFromParent(selectedNode);
		tree.updateUI();
		selectedNode = null;
	}
		
	private void doEdit() {
		boolean inputValid = false;
		do {
			Object currentValue = selectedNode.getUserObject();
			String newValueString = JOptionPane.showInputDialog(this, "Enter new value", currentValue);
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

	private boolean nodeIsSelected() {
		if(selectedNode == null) {
			JOptionPane.showMessageDialog(this, "No node is selected");
			return false;
		}
		else {
			return true;
		}
	}

	public void setData(Map<String,Object> data, String node) {
		top.removeAllChildren();
		top.setUserObject(data);
		addNodes(data, top);
	}
	
	public Map<String,Object> getData() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	private void addNodes(Map<String,Object> data, MapTreeNode parent) {
		for(String key: data.keySet()) {
			Object value = data.get(key);
			MapTreeNode child = new MapTreeNode(key, value);
			parent.add(child);
			if(value instanceof Map) {
				addNodes((Map<String,Object>)value, child);
			}
			else {
				child.add(new MapTreeNode(value.toString(), value));
			}
		}
	}
	
	public static void open(String chartId, String node, Frame owner) {
		Map<String,Object> recipeData = RecipeDataManager.getData(chartId);
		JDialog dialog = new JDialog(owner, true);
		RecipeDataBrowser treePanel = new RecipeDataBrowser("Recipe Data");
		treePanel.setData(recipeData, node);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO: should this be on a Swing worker?
				RecipeDataManager.updateData();
			}
		});
		dialog.getContentPane().add(treePanel);
		dialog.setTitle("Recipe Data Browser");
		dialog.setSize(400, 200);
		dialog.setVisible(true);
	}

}
