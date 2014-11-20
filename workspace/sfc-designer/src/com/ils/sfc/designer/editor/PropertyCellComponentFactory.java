package com.ils.sfc.designer.editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.inductiveautomation.ignition.common.config.PropertyValue;

/** A helper class to make Swing components for editing/rendering various
 *  property types.
 */
public class PropertyCellComponentFactory {
	
	public PropertyCellComponentFactory() {

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
	protected Component getComponentForValue(PropertyValue<?> pvalue, int alignment) {
	    if(pvalue.getProperty().getType() == Boolean.class) {
	    	JCheckBox checkBox = createCheckBox();
	    	boolean value = pvalue.getValue() != null ? ((Boolean)pvalue.getValue()).booleanValue() : false;
	    	checkBox.setSelected(value);
	    	return checkBox;
	    }
	    else {
	    	JTextField textField = createTextField();
	    	textField.setHorizontalAlignment(alignment);
	    	String value = pvalue.getValue() != null ? pvalue.getValue().toString() : "";
	    	textField.setText(value.toString());
	    	textField.setToolTipText(value.toString());
	    	return textField;
	    }		
	}
}
