package com.ils.sfc.designer.stepEditor.collectData;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.designer.stepEditor.EditorUtil;

public class CollectDataCellRenderer implements TableCellRenderer {		
	public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
		// no boolean values, so can just use a text field to render:
		return EditorUtil.getRendererComponent(String.class, value);
	}	
}