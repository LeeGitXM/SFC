package com.ils.sfc.designer.stepEditor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class EditorUtil {
	
	public static Component getRendererComponent(Class<?> aClass, Object value) {
		if(aClass == Boolean.class) {
			JCheckBox checkBox = createCheckBox((Boolean)value);
			return checkBox;
		}
		else {
			JTextField textField = createTextField( value != null ? value.toString() : null);
			return textField;
		}
	}
	
	// Some utility functions for UI creation:
	public static JCheckBox createCheckBox(Boolean value) {
		JCheckBox checkBox = new JCheckBox();
    	checkBox.setBackground(Color.white);
    	checkBox.setBorder(null);
    	if(value != null) {
    		checkBox.setSelected((Boolean)value.booleanValue());
    	}
    	checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    	return checkBox;
	}
	
	public static JTextField createTextField(String value) {
		JTextField textField = new JTextField();
		textField.setText(value);
    	textField.setBorder(null);	
    	textField.setBackground(Color.white);
    	return textField;
	}
	
	public static JComboBox<Object> createChoiceCombo(String[] choices) {
		JComboBox<Object> combo = new JComboBox<Object>(choices);
		combo.setBackground(java.awt.Color.white);
		return combo;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getCellEditorValue(Component component) {
		if(component instanceof JTextField) {
			return ((JTextField)component).getText();
		}
	    else if(component instanceof JComboBox) {
	    	return ((JComboBox<Object>)component).getSelectedItem();
	    }
	    else if(component instanceof JCheckBox) {
	    	return ((JCheckBox)component).isSelected();
	    }
	    else {	    	
	    	return "?";
	    }
	}

}
