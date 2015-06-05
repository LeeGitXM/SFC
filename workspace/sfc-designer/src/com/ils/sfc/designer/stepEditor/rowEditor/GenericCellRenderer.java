package com.ils.sfc.designer.stepEditor.rowEditor;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.designer.stepEditor.EditorUtil;

public class GenericCellRenderer implements TableCellRenderer {		
	
	public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
		if(value != null) {
			return EditorUtil.getRendererComponent(value.getClass(), value);
		}
		else {
			return EditorUtil.getRendererComponent(String.class, value);			
		}
	}	
}