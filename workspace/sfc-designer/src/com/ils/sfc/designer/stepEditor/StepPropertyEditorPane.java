package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.ils.sfc.designer.propertyEditor.PropertyRow;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class StepPropertyEditorPane extends EditorPanel implements ValueHolder {
	private StepEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, true,  false, false, true);

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
		buttonPanel.enableEditButton(getPropertyEditor());
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
		PropertyRow selectedRow = getPropertyEditor().getSelectedRow();
		boolean hasChoices = selectedRow != null && selectedRow.getChoices() != null;
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;

		if(selectedPropertyValue.getProperty().equals(IlsProperty.TAG_PATH)) {
			editor.stopCellEditing();
			controller.getTagBrowser().setValue(selectedPropertyValue.getValue());
			controller.getTagBrowser().activate(this);
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE )) {
			// REVIEW_DATA properties hold a complex configuration in a stringified JSON object
			controller.getReviewDataPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getReviewDataPanel().activate(myIndex);				
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.REVIEW_FLOWS)) {
			controller.getReviewFlowsPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getReviewFlowsPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.COLLECT_DATA_CONFIG)) {
			// COLLECT_DATA properties hold a complex configuration in a stringified JSON object
			controller.getCollectDataPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getCollectDataPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.CONFIRM_CONTROLLERS_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getConfirmControllersPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getConfirmControllersPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.MONITOR_DOWNLOADS_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getMonitorDownloadsPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getMonitorDownloadsPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.PV_MONITOR_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getPvMonitorPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getPvMonitorPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.WRITE_OUTPUT_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getWriteOutputPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getWriteOutputPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.MANUAL_DATA_CONFIG)) {
			controller.getManualDataEntryPanel().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getManualDataEntryPanel().activate(myIndex);							
		}
		else if(selectedPropertyValue.getProperty().getName().endsWith(Constants.UNIT_SUFFIX) && !hasChoices) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate(myIndex);
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setValue(editor.getSelectedValue());
		}
		else if(selectedPropertyValue.getProperty().getType() == String.class) {
			editor.stopCellEditing();
			Object value = selectedPropertyValue.getValue();
	    	if(selectedPropertyValue.getProperty().equals(IlsProperty.G2_XML)) {
	    		value = IlsSfcCommonUtils.unescapeXml((String)value);
	    	}
			controller.getStringEditor().setValue(value);
			controller.getStringEditor().activate(this);
		}
		// else do nothing
	}
	
	public PropertyEditor getPropertyEditor() {
		return editor;
	}

	@Override
	public void commitEdit() {
		editor.stopCellEditing();
	}

	@Override
	public void setValue(Object value) {
		getPropertyEditor().setSelectedValue(value);
	}

}
