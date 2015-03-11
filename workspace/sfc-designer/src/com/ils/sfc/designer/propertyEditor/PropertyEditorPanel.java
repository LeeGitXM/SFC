package com.ils.sfc.designer.propertyEditor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.ils.sfc.designer.ButtonPanel;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;

@SuppressWarnings("serial")
public class PropertyEditorPanel extends JPanel {
	public static java.awt.Color background = new java.awt.Color(238,238,238);
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, false, false);
	private PropertyEditor propertyEditor = new PropertyEditor();
	
	public PropertyEditorPanel() {
		super(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(propertyEditor, BorderLayout.CENTER);
	}
	
	public void setPropertyValues(BasicPropertySet propertyValues, boolean sortInternal) {
		propertyEditor.setPropertyValues(propertyValues, sortInternal);
	}
}
