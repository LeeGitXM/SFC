package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class RecipePropertyEditorPane extends JPanel implements EditorPane {
	private RecipeEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(true, true, true, true, false,  false);

	private Data recipeData;
	
	public RecipePropertyEditorPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		add(editor, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}			
		});
		buttonPanel.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}			
		});
		buttonPanel.getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}			
		});
		buttonPanel.getEditButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doEdit();}			
		});
	}

	@Override
	public void activate() {
		controller.slideTo(RecipeEditorController.OBJECT_EDITOR);
	}
	
	private void doAdd() {
		controller.getFieldCreator().setRecipeData((Structure)recipeData);
		controller.getFieldCreator().activate();
	}

	private void doEdit() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;
		if(selectedPropertyValue.getProperty().equals(IlsProperty.TAG_PATH)) {
			editor.stopCellEditing();
			controller.getTagBrowser().activate();
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.UNITS)) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate();
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setUnit((String)editor.getSelectedValue());
		}
		else if(selectedPropertyValue.getProperty().getType() == String.class) {
			editor.stopCellEditing();
			controller.getTextEditor().setText((String)selectedPropertyValue.getValue());
			controller.getTextEditor().activate();
		}
		// else do nothing
	}
	
	private void doRemove() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;
		Structure structureData = (Structure) recipeData;
		structureData.removeDynamicProperty(selectedPropertyValue.getProperty());
		getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
	}
	
	private void doOK() {
		recipeData.setProperties(editor.getPropertyValues());
		if(IlsSfcCommonUtils.isEmpty(recipeData.getKey())) {
			controller.showMessage("A key is required", RecipeEditorController.OBJECT_EDITOR);
		}
		else {
			controller.getBrowser().activate();			
		}
	}

	public PropertyEditor getPropertyEditor() {
		return editor;
	}

	public void setRecipeData(Data recipeData) {
		this.recipeData = recipeData;
		boolean isStructure = recipeData instanceof Structure;
		buttonPanel.getAddButton().setEnabled(isStructure);
		buttonPanel.getRemoveButton().setEnabled(isStructure);
		getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
	}

}