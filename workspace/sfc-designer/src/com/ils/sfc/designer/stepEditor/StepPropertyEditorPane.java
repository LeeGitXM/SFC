package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class StepPropertyEditorPane extends JPanel implements EditorPane {
	private StepEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, true,  true);

	public StepPropertyEditorPane(StepEditorController controller) {
		super(new BorderLayout());
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
	public void activate() {
		// HACK!! we recognize "testable" elements by looking for SQL related properties
		buttonPanel.getExecButton().setVisible(
			editor.getPropertyValues().contains(IlsProperty.SQL));

		controller.slideTo(StepEditorController.PROPERTY_EDITOR);
	}
	
	@SuppressWarnings("unchecked")
	private void doEdit() {
		PropertyValue<?> selectedPropertyValue = getPropertyEditor().getSelectedPropertyValue();
		if(selectedPropertyValue == null) return;
		System.out.println("editing " + selectedPropertyValue.getProperty().getName());

		if(selectedPropertyValue.getProperty().equals(IlsProperty.TAG_PATH)) {
			editor.stopCellEditing();
			controller.getTagBrowser().activate();
		}
		else if(selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA) ||
				selectedPropertyValue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE )) {
			// kind of a hack here...REVIEW_DATA is a pseudo-property
			// the complex values are held in the local recipe data of the step
			System.out.println("initial congfig " + selectedPropertyValue.getProperty().getName());
			controller.getReviewDataPane().setConfig((PropertyValue<String>) selectedPropertyValue);
			controller.getReviewDataPane().activate();				
		}
		else if(selectedPropertyValue.getProperty().getName().endsWith(IlsSfcNames.UNIT_SUFFIX)) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate();
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setUnit((String)editor.getSelectedValue());
		}
		else if(selectedPropertyValue.getProperty().getType() == String.class) {
			editor.stopCellEditing();
			controller.getStringEditor().setText((String)selectedPropertyValue.getValue());
			controller.getStringEditor().activate();
		}
		// else do nothing
	}
	
	public PropertyEditor getPropertyEditor() {
		return editor;
	}

}
