package com.ils.sfc.designer.stepEditor.rowEditor;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.stepEditor.EditorUtil;

@SuppressWarnings("serial")
public class RowCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		RowTableModel model = (RowTableModel) table.getModel();
		if(model.isComboColumn(col)) {
			String[] choices = model.getChoices(row, col);
			JComboBox<?> comboBox = EditorUtil.createChoiceCombo(choices, (String)value);
			return component = comboBox;
		}
		else if(model.isTextColumn(col) || model.isDoubleColumn(col)) {
			JTextField textField = EditorUtil.createTextField(value);
			return component = textField;
		}
		else if(model.isBooleanColumn(col)) {
			JCheckBox checkBox = EditorUtil.createCheckBox(value);
			return component = checkBox;
		}
		else {  // shouldn't happen, but keep compiler happy
			JTextField textField = EditorUtil.createTextField(value);
			return component = textField;			
		}
	}

	public Object getCellEditorValue() {
		return EditorUtil.getCellEditorValue(component);
	}
	  
}