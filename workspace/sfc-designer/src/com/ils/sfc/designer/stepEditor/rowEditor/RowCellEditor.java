package com.ils.sfc.designer.stepEditor.rowEditor;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.designer.stepEditor.EditorUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/***
 * This is used by the structure editor that is used for a step property that is a list of dictionaries.  
 * This is NOT used on the primary property editor pane.
 * 
 * @author phass
 */
@SuppressWarnings("serial")
public class RowCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		RowTableModel model = (RowTableModel) table.getModel();
		if(model.isComboColumn(col)) {
			log.infof("Returning a combo box");
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