package com.ils.sfc.designer.propertyEditor;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;
	private PropertyCellComponentFactory factory = new PropertyCellComponentFactory();

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		PropertyRow rowObj = ((PropertyTableModel) table.getModel()).getRowObject(row);
		if(rowObj.getChoices() != null) {
			JComboBox<Object> combo = new JComboBox<Object>(rowObj.getChoices());
			combo.setBackground(java.awt.Color.white);
			return component = combo;
		}
		else {
			int alignment = factory.getHorizontalAlignment(col);
			return component = factory.getComponentForValue(rowObj, alignment);
		}
	}

	@SuppressWarnings("unchecked")
	public Object getCellEditorValue() {
		if(component instanceof JTextField) {
			return ((JTextField)component).getText();
		}
	    else if(component instanceof JCheckBox) {
	    	return Boolean.valueOf(((JCheckBox)component).isSelected()).toString();
	    }
	    else if(component instanceof JComboBox) {
	    	return ((JComboBox<Object>)component).getSelectedItem();
	    }
	    else {	    	
	    	return "?";
	    }
	}
	  
}