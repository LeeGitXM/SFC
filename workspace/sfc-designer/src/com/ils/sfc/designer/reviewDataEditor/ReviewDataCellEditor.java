package com.ils.sfc.designer.reviewDataEditor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.common.recipe.ReviewDataConfig.Row;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
class ReviewDataCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		ReviewDataTableModel tableModel = (ReviewDataTableModel)table.getModel();
		if(tableModel.isComboColumn(col)) {
			Object[] choices = null;
			if(tableModel.isDestinationColumn(col)) { // recipe scope
				choices = IlsSfcNames.RECIPE_LOCATION_CHOICES;
			}
			else if(tableModel.isUnitTypesColumn(col)) { // unit types
				try {
					choices = PythonCall.toArray(PythonCall.GET_UNIT_TYPES.exec());
				} catch (JythonExecException e) {
					e.printStackTrace();
				}
			}
			else {//  units
				try {
					Row rowObj = tableModel.getRowObject(row);
					if(!IlsSfcCommonUtils.isEmpty(rowObj.unitType)) {
						System.out.println("unit type " + rowObj.unitType);
						choices = PythonCall.toArray(PythonCall.GET_UNITS_OF_TYPE.exec(rowObj.unitType));
					}
					else {
						System.out.println("no unit type");
						// no unit type has been chosen
						choices = new String[0];						
					}
					} catch (JythonExecException e) {
					e.printStackTrace();
				}
			}
			JComboBox<Object> combo = new JComboBox<Object>(choices);
			combo.setBackground(java.awt.Color.white);
			return component = combo;
		}
		else {
			JTextField textField = new JTextField();
			textField.setText((String)value);
	    	textField.setBorder(null);	
	    	textField.setBackground(Color.white);
	    	return component = textField;
		}
	}

	@SuppressWarnings("unchecked")
	public Object getCellEditorValue() {
		if(component instanceof JTextField) {
			return ((JTextField)component).getText();
		}
	    else if(component instanceof JComboBox) {
	    	return ((JComboBox<Object>)component).getSelectedItem();
	    }
	    else {	    	
	    	return "?";
	    }
	}
	  
}