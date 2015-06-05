package com.ils.sfc.designer.stepEditor.rowEditor.confirmControllers;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.stepEditor.EditorUtil;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
public class ConfirmControllersCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		if(col == ConfirmControllersTableModel.CHECK_SP_COLUMN || 
		   col == ConfirmControllersTableModel.CHECK_PATH_COLUMN ) {
			JCheckBox checkBox = EditorUtil.createCheckBox((Boolean)value);
			return component = checkBox;
		}
		else {
			JTextField textField = EditorUtil.createTextField((String)value);
	    	return component = textField;
		}
	}

	public Object getCellEditorValue() {
		return EditorUtil.getCellEditorValue(component);
	}
	  
}