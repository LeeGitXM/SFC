package com.ils.sfc.designer.stepEditor.collectData;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcNames;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
public class CollectDataCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		if(col == CollectDataTableModel.LOCATION_COLUMN) {
			return createChoiceCombo(IlsProperty.RECIPE_LOCATION.getChoices());
		}
		else if(col == CollectDataTableModel.VALUE_TYPE_COLUMN) {
			return createChoiceCombo(IlsSfcNames.COLLECT_DATA_VALUE_TYPE_CHOICES);
		}
		else {
			return createTextField(value);
		}

		/*
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
						choices = PythonCall.toArray(PythonCall.GET_UNITS_OF_TYPE.exec(rowObj.unitType));
					}
					else {
						// no unit type has been chosen
						choices = new String[0];						
					}
					} catch (JythonExecException e) {
					e.printStackTrace();
				}
			}
			JComboBox<Object> combo = new JComboBox<Object>(choices);
			combo.setBackground(java.awt.Color.white);
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}				
			});
			return component = combo;
		}
		else {
			JTextField textField = new JTextField();
			textField.setText((String)value);
	    	textField.setBorder(null);	
	    	textField.setBackground(Color.white);
	    	return component = textField;
		}
		*/
	}

	private Component createTextField(Object value) {
		JTextField textField = new JTextField();
		textField.setText((String)value);
		textField.setBorder(null);	
		textField.setBackground(Color.white);
		return component = textField;
	}
	
	private JComboBox<Object> createChoiceCombo(String[] choices) {
		JComboBox<Object> combo = new JComboBox<Object>(choices);
		combo.setBackground(java.awt.Color.white);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}				
		});
		component = combo;
		return combo;
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