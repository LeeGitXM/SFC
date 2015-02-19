package com.ils.sfc.designer.reviewDataEditor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class ReviewDataCellRenderer implements TableCellRenderer {		
	public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
		JTextField textField = new JTextField();
		textField.setText((String)value);
    	textField.setBorder(null);	
    	textField.setBackground(Color.white);
    	return textField;
	}	
}
