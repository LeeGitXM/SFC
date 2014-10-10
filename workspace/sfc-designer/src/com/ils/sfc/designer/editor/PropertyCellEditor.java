package com.ils.sfc.designer.editor;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.inductiveautomation.ignition.common.config.PropertyValue;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;
	private PropertyCellComponentFactory factory = new PropertyCellComponentFactory();

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		PropertyRow rowObj = ((PropertyTableModel) table.getModel()).getRowObject(row);
		Object[] choicesOrNull = col == 1 ? rowObj.getChoices() : rowObj.getUnitChoices();
		if(choicesOrNull != null) {
			JComboBox<Object> combo = new JComboBox<Object>(choicesOrNull);
			combo.setBackground(java.awt.Color.white);
			return component = combo;
		}
		else {
			int alignment = factory.getHorizontalAlignment(col);
			return component = factory.getComponentForValue(rowObj.getPropertyValue(), alignment);
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