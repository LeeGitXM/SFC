package com.ils.sfc.designer.stepEditor.confirmControllers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.designer.stepEditor.EditorUtil;

public class ConfirmControllersCellRenderer implements TableCellRenderer {		
	public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {		
	if(column == ConfirmControllersTableModel.CHECK_SP_COLUMN || 
	   column == ConfirmControllersTableModel.CHECK_PATH_COLUMN ) {
		return EditorUtil.getRendererComponent(Boolean.class, value);}
	else {
		return EditorUtil.getRendererComponent(String.class, value);}			
	}
}