package com.ils.sfc.designer.editor;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** A table cell editor for the property grid */
class PropertyCellRenderer implements TableCellRenderer {
	private DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
	private PropertyCellComponentFactory factory = new PropertyCellComponentFactory();
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		int alignment = factory.getHorizontalAlignment(column);
		PropertyRow rowObj = ((PropertyTableModel) table.getModel()).getRowObject(row);
		if(column == 1) {
			return factory.getComponentForValue(rowObj.getPropertyValue(), alignment);
		}
		else {
			JComponent component = (JComponent) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(component instanceof JLabel) {
				JLabel label = (JLabel)component;
				if(rowObj.isCategory()) {
					alignment = SwingConstants.LEFT;
					label.setFont(label.getFont().deriveFont(Font.BOLD));
				}
				label.setBorder(new EmptyBorder(3,3,3,3));
				label.setHorizontalAlignment(alignment);
			}
			return component;
		}
	}
		
}