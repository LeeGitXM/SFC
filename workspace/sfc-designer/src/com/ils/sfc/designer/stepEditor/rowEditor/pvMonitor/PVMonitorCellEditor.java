package com.ils.sfc.designer.stepEditor.rowEditor.pvMonitor;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.stepEditor.EditorUtil;

@SuppressWarnings("serial")
public class PVMonitorCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		if(PVMonitorTableModel.isComboColumn(col)) {
			String[] choices = PVMonitorTableModel.getChoices(col);
			JComboBox<?> comboBox = EditorUtil.createChoiceCombo(choices, (String)value);
			return component = comboBox;
		}
		else if(PVMonitorTableModel.isTextColumn(col) || PVMonitorTableModel.isDoubleColumn(col)) {
			JTextField textField = EditorUtil.createTextField(value);
			return component = textField;
		}
		else if(PVMonitorTableModel.isBooleanColumn(col)) {
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