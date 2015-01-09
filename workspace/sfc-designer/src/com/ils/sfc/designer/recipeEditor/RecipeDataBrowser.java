package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

import com.ils.sfc.common.chartStructure.IlsSfcStepStructure;
import com.ils.sfc.common.recipe.RecipeData;
import com.ils.sfc.common.recipe.RecipeDataMap;
import com.ils.sfc.common.recipe.RecipeDataManager;

@SuppressWarnings("serial")
public class RecipeDataBrowser extends JDialog {
	final JButton addButton = new JButton("+");
	final JButton editButton = new JButton("E");
	final JButton removeButton = new JButton("-");
	private MapTreeNode top = new MapTreeNode("", null);
	private JTree tree;
	private DefaultTreeModel treeModel;
	private MapTreeNode selectedNode;
	private RecipeData recipeData;

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
		
		public boolean isBytes() {
			return userObject instanceof byte[];
		}
		
		public boolean contentsAreEditable() {
			return contentsAreEditable && !isBytes();
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
			else if(isBytes()) {
				buf.append("<bytes>");
			}
			else {
				buf.append(" = ");
				String valueString = getUserObject() != null ? getUserObject().toString() : "null";
				buf.append(valueString);
			}
			return buf.toString();
		}
	}
	
	public RecipeDataBrowser(Frame owner, String stepId) {
		super(owner,true);
		recipeData = RecipeDataManager.getData();
		createTree(stepId);
		createMenu();
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		panel.add(new JScrollPane(tree), BorderLayout.CENTER);
		createButtonPanel(panel);		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO: should this be on a Swing worker?
				// TODO: check for failure in updating data?
				RecipeDataManager.updateData();
			}
		});
		setTitle("Recipe Data Browser");
		setSize(400, 200);
		setLocation(owner.getX() + owner.getWidth()/2 - getWidth()/2, owner.getY() + owner.getHeight()/2 - getHeight()/2);
	}

	private void createTree(String stepId) {
		top.setContentsEditable(false);
		treeModel = new DefaultTreeModel(top);

		createSubTree(stepId, "Local");
		IlsSfcStepStructure step = recipeData.getStructureMgr().getStepWithId(stepId);
		IlsSfcStepStructure parentStep = step.getParent();
		IlsSfcStepStructure procedureStep = step.getProcedure();
		IlsSfcStepStructure operationStep = step.getOperation();
		IlsSfcStepStructure phaseStep = step.getPhase();
		if(parentStep != null) {
			createSubTree(step.getParent().getId(), "Superior");
		}
		if(procedureStep != null) {
			createSubTree(procedureStep.getId(), "Procedure");
		}
		if(operationStep != null) {
			createSubTree(operationStep.getId(), "Operation");
		}
		if(phaseStep != null) {
			createSubTree(phaseStep.getId(), "Phase");
		}
		createNamedSubtree();
		
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectedNode = (MapTreeNode) tree.getLastSelectedPathComponent();
				addButton.setEnabled(canAdd());
				removeButton.setEnabled(canRemove());
				editButton.setEnabled(canEdit());
			}
		});		
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		fileMenu.add(saveAsItem);
		JMenuItem closeItem = new JMenuItem("Close");
		fileMenu.add(closeItem);
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doClose();
			}
		});
		
		JMenu toolsMenu = new JMenu("Tools");
		menuBar.add(toolsMenu);
		JMenuItem findByIdItem = new JMenuItem("Find by ID...");
		toolsMenu.add(findByIdItem);
		
		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);
		JMenuItem allItem = new JMenuItem("All");
		toolsMenu.add(allItem);
	}

	private void createButtonPanel(JPanel panel) {
		JPanel buttonPanel = new JPanel(new FlowLayout());
		panel.add(buttonPanel, BorderLayout.SOUTH);
		addButton.setEnabled(false);
		buttonPanel.add(addButton);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAdd();}
		});
		buttonPanel.add(removeButton);
		removeButton.setEnabled(false);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doRemove();}
		});
		editButton.setEnabled(false);
		buttonPanel.add(editButton);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doEdit();}
		});
		
	}

	private void createNamedSubtree() {
		RecipeDataMap dataMap = recipeData.getNamedData();
		createSubtreeForMap("Named", dataMap);		
	}
	
	private void createSubTree(String stepId, String name) {
		RecipeDataMap stepDataMap = recipeData.getStepData(stepId);
		createSubtreeForMap(name, stepDataMap);
	}

	private void createSubtreeForMap(String name, RecipeDataMap stepDataMap) {
		MapTreeNode subTop = new MapTreeNode(name, stepDataMap );
		top.add(subTop);
		subTop.setContentsEditable(true);
		addNodes(stepDataMap, subTop);
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
			String propertyName = selectedNode.name;
			String newValueString = null;
			if(currentValue instanceof String) {
				RecipeStringEditorDialog dlg = new RecipeStringEditorDialog(this, (String)currentValue, "Edit " + propertyName);
				dlg.setVisible(true);
				newValueString = dlg.getResult();
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

	private void doClose() {
		dispose();
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

}
