package com.ils.sfc.designer.propertyEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.ButtonPanel;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.script.JythonExecException;

@SuppressWarnings("serial")
public class PropertyEditorPanel extends JPanel {
	public static java.awt.Color background = new java.awt.Color(238,238,238);
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, false, background);
	private PropertyEditor propertyEditor = new PropertyEditor();
	
	public PropertyEditorPanel() {
		super(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(propertyEditor, BorderLayout.CENTER);
		buttonPanel.getExecButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doExec();}			
		});
	}

	private void doExec() {
		BasicPropertySet propertyValues = propertyEditor.getPropertyValues();
		String sql = propertyValues.getOrDefault(IlsProperty.SQL);
		String database = propertyValues.getOrDefault(IlsProperty.DATABASE);
		Object[] args = {sql, database};
		try {
			PythonCall.TEST_QUERY.exec(args);
		} catch (JythonExecException e) {
			e.printStackTrace();
		}
	}
	
	public void setPropertyValues(BasicPropertySet propertyValues, boolean sortInternal) {
		propertyEditor.setPropertyValues(propertyValues, sortInternal);
		// HACK!! we recognize "testable" elements by looking for SQL related properties
		buttonPanel.getExecButton().setVisible(
			propertyValues.contains(IlsProperty.SQL) && 
			propertyValues.contains(IlsProperty.DATABASE));
	}
}
