package com.ils.sfc.designer.stepEditor.collectData;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.designer.stepEditor.EditorUtil;
import com.ils.sfc.designer.stepEditor.EditorUtil;

/** A table cell editor for the property grid */
@SuppressWarnings("serial")
public class CollectDataCellEditor extends AbstractCellEditor implements TableCellEditor{
	private Component component;

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
		int row, int col) {
		if(col == CollectDataTableModel.LOCATION_COLUMN) {
			JComboBox<?> comboBox = EditorUtil.createChoiceCombo(IlsProperty.RECIPE_LOCATION.getChoices());
			return component = comboBox;
		}
		else if(col == CollectDataTableModel.VALUE_TYPE_COLUMN) {
			JComboBox<?> comboBox = EditorUtil.createChoiceCombo(IlsSfcNames.COLLECT_DATA_VALUE_TYPE_CHOICES);
			return component = comboBox;
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