package com.ils.sfc.designer.stepEditor.reviewData;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.designer.stepEditor.EditorUtil;


public class ReviewDataCellRenderer implements TableCellRenderer {		
	public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
		return EditorUtil.createTextField((String)value);
	}	
}
