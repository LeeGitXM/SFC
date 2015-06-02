package com.ils.sfc.designer.stepEditor.reviewData;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.rowconfig.ReviewDataConfig.Row;
import com.ils.sfc.designer.stepEditor.EditorUtil;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
public class ReviewDataCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		ReviewDataTableModel tableModel = (ReviewDataTableModel)table.getModel();
		if(tableModel.isComboColumn(col)) {
			String[] choices = null;
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
			JComboBox<Object> combo = EditorUtil.createChoiceCombo(choices);
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}				
			});
			return component = combo;
		}
		else {
			JTextField textField = EditorUtil.createTextField((String)value);
	    	return component = textField;
		}
	}

	@SuppressWarnings("unchecked")
	public Object getCellEditorValue() {
		return EditorUtil.getCellEditorValue(component);
	}
	  
}