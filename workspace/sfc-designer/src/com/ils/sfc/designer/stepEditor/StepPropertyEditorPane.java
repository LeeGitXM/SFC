package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class StepPropertyEditorPane extends EditorPanel {
	private StepEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, true,  true);

	public StepPropertyEditorPane(StepEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		add(editor, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getEditButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doEdit();}			
		});
		buttonPanel.getCheckBox().setText("Isolation Mode");
		buttonPanel.getExecButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doExec();}			
		});
	}

	private void doExec() {
		BasicPropertySet propertyValues = editor.getPropertyValues();
		String sql = propertyValues.getOrDefault(IlsProperty.SQL);
		boolean isolationMode = buttonPanel.getCheckBox().isSelected();
		String database = IlsClientScripts.getDatabaseName(isolationMode); // propertyValues.getOrDefault(IlsProperty.DATABASE);
		Object[] args = {sql, database};
		try {
			PythonCall.TEST_QUERY.exec(args);
		} catch (JythonExecException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void activate(int returnIndex) {
		// HACK!! we recognize "testable" elements by looking for SQL related properties
		buttonPanel.getExecButton().setVisible(
			editor.getPropertyValues().contains(IlsProperty.SQL));

		super.activate(returnIndex);
	}
	
	@SuppressWarnings("unchecked")
	private void doEdit() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;

		if(selectedPropertyValue.getProperty().equals(IlsProperty.TAG_PATH)) {
			editor.stopCellEditing();
			controller.getTagBrowser().activate(myIndex);
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE )) {
			// REVIEW_DATA properties hold a complex configuration in a stringified JSON object
			controller.getReviewDataPane().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getReviewDataPane().activate(myIndex);				
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.COLLECT_DATA_CONFIG)) {
			// COLLECT_DATA properties hold a complex configuration in a stringified JSON object
			controller.getCollectDataPane().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getCollectDataPane().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().getName().endsWith(IlsSfcNames.UNIT_SUFFIX)) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate(myIndex);
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setValue(editor.getSelectedValue());
		}
		else if(selectedPropertyValue.getProperty().getType() == String.class) {
			editor.stopCellEditing();
			controller.getStringEditor().setValue(selectedPropertyValue.getValue());
			controller.getStringEditor().activate(myIndex);
		}
		// else do nothing
	}
	
	public PropertyEditor getPropertyEditor() {
		return editor;
	}

}
