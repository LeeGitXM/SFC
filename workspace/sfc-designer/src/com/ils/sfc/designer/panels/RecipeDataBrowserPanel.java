package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.EnclosureInfo;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.ils.sfc.designer.panels.PanelController;
import com.ils.sfc.designer.panels.ValueHoldingEditorPanel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** A browser that can be invoked on a given SFC step in the Designer and that will show 
 *  a tree view of all recipe data available in scopes visible from (i.e. at and above) that step.
 */
public class RecipeDataBrowserPanel extends ValueHoldingEditorPanel {
	private final ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, true, false, false);
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
	private JTree tree = new JTree(rootNode);
	
	public RecipeDataBrowserPanel(PanelController controller, int index) {
		super(controller, index);
		this.myIndex = index;
		setLayout(new BorderLayout());	
		add(buttonPanel, BorderLayout.NORTH);
		rootNode.add(new DefaultMutableTreeNode("Hi Rob"));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(true);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}			
		});
		buttonPanel.getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {cancel();}			
		});
	}

	/** Build a tree of all the recipe data currently defined in a step and all its enclosing
	 *  parents. Any chart enclosure is flattened out; the s88 scopes are listed sequentially at
	 *  the highest level of the tree. Return the root node.
	 */
	private void buildRecipeKeysTree(String childChartPath, ChartUIElement element) throws Exception {
		rootNode.removeAllChildren();
		SimpleHierarchyAnalyzer analyzer = new SimpleHierarchyAnalyzer(
			panelController.getContext().getGlobalProject().getProject(), 
			IlsSfcDesignerHook.getStepRegistry());
		analyzer.analyze();
		List<EnclosureInfo> enclosureHierarchy = analyzer.getEnclosureHierarchyBottomUp(childChartPath,false);
		// Go through the chart enclosure hierarchy top down searching for recipe data
		// the level index is 0 for the lowest level (non-enclosing step)
		for(int level = enclosureHierarchy.size(); level >= 1 ; level--) {
			EnclosureInfo enclosureInfo = enclosureHierarchy.get(level-1);
			addRecipeDataKeysNode(rootNode, enclosureInfo.parentElement, level);
		}
		addRecipeDataKeysNode(rootNode, element, 0);
		tree.expandPath(new TreePath(rootNode));
		validate();	
		repaint();
	}
	
	/** For a given chart step/s88 scope, add a tree node for recipe data if it has any. */
	private void addRecipeDataKeysNode(DefaultMutableTreeNode rootNode, 
		ChartUIElement element, int level) throws Exception {
		String factoryId = element.get(SimpleHierarchyAnalyzer.factoryIdProperty);
		JSONObject associatedDataJson = element.get(SimpleHierarchyAnalyzer.associatedDataProperty);
		if(associatedDataJson != null) {
			String scopeLabel = getLabelForScope(factoryId, level);
			// If this level has an associated S88 scope, show the data. It is possible
			// that an ordinary enclosing step in the hierarchy may have recipe data
			// but is not associated with a scope.
			if(scopeLabel != null) {
				DefaultMutableTreeNode scopeNode = new DefaultMutableTreeNode(scopeLabel);
				rootNode.add(scopeNode);
				List<Data> recipeData = Data.fromAssociatedData(associatedDataJson);
				for(Data data: recipeData) {
					addNodeForData(scopeNode, data);
				}
			}
		}
	}
	
	/** Add a sub-tree of recipe data keys under the given parent for a piece of recipe data 
	 *  (which may in fact be a group with child data). */
	private void addNodeForData(DefaultMutableTreeNode parentNode, Data data) {
		DefaultMutableTreeNode dataNode = new DefaultMutableTreeNode(data.getKey());
		parentNode.add(dataNode);
		if(data.isGroup()) {
			Group group = (Group)data;
			for(Data child: group.getChildren()) {
				addNodeForData(dataNode, child);
			}
		}
		else {
			for(PropertyValue<?> pv: data.getProperties()) {
				dataNode.add(new DefaultMutableTreeNode(pv.getProperty().getName()));				
			}
		}
	}

	/** Get the user-friendly label for the associated s88 scope,
	 *  or null if the step has no particular scope.
	 *  Level indicates the level in the enclosure hierarchy;
	 *  level 0 is the lowest step. */
	private String getLabelForScope(String factoryId, int level) {
		if(factoryId.equals(PhaseStepProperties.FACTORY_ID)) {
				return Constants.PHASE;
		}
		else if(factoryId.equals(OperationStepProperties.FACTORY_ID)) {
			return Constants.OPERATION;
		}
		else if(factoryId.equals(ProcedureStepProperties.FACTORY_ID)) {
			return Constants.GLOBAL;
		}
		else if(level == 1) {
			return Constants.SUPERIOR;
		}
		else if(level == 0) {
			return Constants.LOCAL;
		}
		else {
			return null;
		}
	}

	@Override
	/** Get the selected recipe scope and key as <scope>.<key>. There is a bit of a hack going
	 *  on here, as these get split in the property editor into values for
	 *  the key field--which is the one being edited--and the corresponding
	 *  scope field, which is inferred. See PropertyTableModel.setValueAt().
	 */
	public Object getValue() {
		TreePath tpath = tree.getSelectionPath();
		// by starting at index 1 we omit the root node
		if(tpath != null && tpath.getPath().length > 1) {
			Object[]elements = tpath.getPath();
			StringBuilder bldr = new StringBuilder();
			for(int i = 1; i < elements.length; i++) {
				if(i == 2) {
					bldr.append("#");  // scope separator
				}
				else if(i > 2) {
					bldr.append(".");  // "regular" recipe key separator
				}
				bldr.append(elements[i].toString());
			}
			return bldr.toString();
		}
		else {
			return null;
		}
	}

	@Override
	public void activate(ValueHolder valueHolder) {
		StepEditorController controller = (StepEditorController)panelController;
		try {
			buildRecipeKeysTree(controller.getChartPath(), controller.getElement());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.activate(valueHolder);
	}
	
	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		
	}

}
