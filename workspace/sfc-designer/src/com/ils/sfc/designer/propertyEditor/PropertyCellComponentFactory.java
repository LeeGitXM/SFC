package com.ils.sfc.designer.propertyEditor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** A helper class to make Swing components for editing/rendering various
 *  property types.
 */
public class PropertyCellComponentFactory {
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());
	
	public PropertyCellComponentFactory() {
		log.infof("Creating a PropertyCellComponentFactory.");
 	}

	private JTextField createTextField() {
		JTextField textField = new JTextField();
    	textField.setBorder(null);	
    	return textField;
	}
	
	private JCheckBox createCheckBox() {
		JCheckBox checkBox = new JCheckBox();
    	checkBox.setBackground(Color.white);
    	checkBox.setBorder(null);
    	checkBox.setHorizontalAlignment(SwingConstants.CENTER);
    	return checkBox;
	}
	
	/** Get the appropriate horizontal alignment for elements
	 *  in a particular column of the property grid.
	 */
	public int getHorizontalAlignment(int vColIndex) {
		if(vColIndex == 0) {
			return SwingConstants.RIGHT;
		} else if(vColIndex == 2) {
			return SwingConstants.LEFT;
		} else {
			return SwingConstants.CENTER;
		}
	}

	/** Get a swing component suitable for editing/rendering a value of
	 *  the given type.
	 */
	protected Component getComponentForValue(PropertyRow rowObj, int alignment, boolean isEditable) {
	    Component component = null;
	    log.infof("...getting a component for a value....");
		if(rowObj.getProperty().getType() == Boolean.class) {
	    	JCheckBox checkBox = createCheckBox();
	    	boolean value = rowObj.getValue() != null ? ((Boolean)rowObj.getValue()).booleanValue() : false;
	    	checkBox.setSelected(value);
	    	component = checkBox;
	    }
	    else {
	    	JTextField textField = createTextField();
	    	textField.setHorizontalAlignment(alignment);
	    	String sValue = null;
	    	if(IlsProperty.isSerializedObject(rowObj.getProperty())) {
	    		sValue = "<Use Editor>";
	    	}
	    	else {
	    		sValue = rowObj.getValueFormatted();		
	    	}
	    	if(Constants.TRANSLATION_ERROR.equals(sValue)) {
	    		textField.setBackground(Color.red);
	    	}
	    	textField.setText(sValue);
	    	textField.setToolTipText(sValue);
	    	component = textField;
	    }		
		component.setEnabled(isEditable);
		return component;
	}



}
