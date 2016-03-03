package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class RecipePropertyEditorPane extends EditorPanel implements ValueHolder {
	private RecipeEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(true, true, true, true, false,  false, false, false);

	private Data recipeData;
	
	public RecipePropertyEditorPane(RecipeEditorController controller, int index) {
		super(controller, index);
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
		
		// listen for selection so we can enable/disable buttons e.g.
		getPropertyEditor().getSelectionModel().addListSelectionListener(
			new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e) {
					selectionChanged();
				}
		});
		selectionChanged();
	}
	
	private void selectionChanged() {
		buttonPanel.getEditButton().setEnabled(
			getPropertyEditor().selectionIsEditable());		
	}
	
	private void doAdd() {
		controller.getFieldCreator().setRecipeData((Structure)recipeData);
		controller.getFieldCreator().activate(myIndex);
	}

	private void doEdit() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;
		if(selectedPropertyValue.getProperty().equals(IlsProperty.TAG_PATH)) {
			editor.stopCellEditing();
			controller.getTagBrowser().setValue(editor.getSelectedValue());
			controller.getTagBrowser().activate(this);
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.UNITS)) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate(this);
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setValue(editor.getSelectedValue());
		}
		else if(selectedPropertyValue.getProperty().getType() == String.class) {
			editor.stopCellEditing();
			controller.getTextEditor().setValue(selectedPropertyValue.getValue());
			controller.getTextEditor().activate(this);
		}
		// else do nothing
	}
	
	private void doRemove() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;
		Structure structureData = (Structure) recipeData;
		structureData.removeDynamicProperty(selectedPropertyValue.getProperty());
		getPropertyEditor().setPropertyValues(recipeData.getProperties(), null);
	}
	
	private void doCancel() {
		this.cancel();
	}
	
	private void doOK() {
		recipeData.setProperties(editor.getPropertyValues());
		String provider = IlsClientScripts.getProviderName(false);
		recipeData.setProvider(provider);
		recipeData.writeToTags();		
		String validationErrorMsg = recipeData.validate();
		if(validationErrorMsg != null) {
			controller.showMessage(validationErrorMsg, RecipeEditorController.OBJECT_EDITOR);
		}
		else if(IlsSfcCommonUtils.isEmpty(recipeData.getKey())) {
			controller.showMessage("A key is required", RecipeEditorController.OBJECT_EDITOR);
		}
		else {
			controller.getBrowser().activate(myIndex);			
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
		getPropertyEditor().setPropertyValues(recipeData.getProperties(), null);
	}

	@Override
	public void setValue(Object value) {
		getPropertyEditor().setSelectedValue(value);
	}

	@Override
	public int getIndex() {
		return myIndex;
	}

}
