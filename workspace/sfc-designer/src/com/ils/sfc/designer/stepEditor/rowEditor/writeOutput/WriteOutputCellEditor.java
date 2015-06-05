package com.ils.sfc.designer.stepEditor.rowEditor.writeOutput;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.stepEditor.EditorUtil;

@SuppressWarnings("serial")
public class WriteOutputCellEditor extends AbstractCellEditor implements TableCellEditor {
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		if(col == WriteOutputTableModel.KEY_COLUMN) {
			JTextField textField = EditorUtil.createTextField((String)value);
			return component = textField;
		}
		else {
			JCheckBox checkBox = EditorUtil.createCheckBox((Boolean)value);
			return component = checkBox;
		}
	}

	public Object getCellEditorValue() {
		return EditorUtil.getCellEditorValue(component);
	}
	  
}
