package com.ils.sfc.designer.propertyEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** A property editor grid/table */
@SuppressWarnings("serial")
public class PropertyEditor extends JPanel {
	private final PropertyTableModel tableModel = new PropertyTableModel();
	private final JTable table = new JTable(tableModel);
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());
	
	public PropertyEditor() {
		log.tracef("Creating a %s", getClass().getName());
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		table.setDefaultEditor(Object.class, new PropertyCellEditor());
		table.setDefaultRenderer(Object.class, new PropertyCellRenderer());
		table.setCellSelectionEnabled(false);  // has side effect of setting row/col selection as well!
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	public PropertyRow getSelectedRow() {
		int selectionIndex = table.getSelectedRow();
		return selectionIndex >= 0 ? tableModel.getRowObject(selectionIndex) : null;
	}

	/** Get the row that external string editing was last invoked on. */
	public Object getSelectedValue() {
		PropertyRow selectedRow = getSelectedRow();
		return selectedRow != null ? selectedRow.getValue() : null;
	}
	
	/** set the externally edited string */
	public void setSelectedValue(Object value) {
		int selectionIndex = table.getSelectedRow();
		if(selectionIndex >= 0) {
			tableModel.setValueAt(value, selectionIndex, PropertyTableModel.VALUE_COLUMN);
		}
	}
	
	public ListSelectionModel getSelectionModel() {
		return table.getSelectionModel();
	}
	
	public JTable getTable() {
		return table;
	}
	
	public PropertyTableModel getTableModel() {
		return tableModel;
	}

	public boolean hasChanged() {
		return tableModel.hasChanged();
	}
	
	public void setPropertyValues(BasicPropertySet propertyValues, Property<?>[] orderedPropertiesOrNull) {
		tableModel.setPropertyValues(propertyValues, orderedPropertiesOrNull);
		table.clearSelection();
	}
	
	@SuppressWarnings("rawtypes")
	public BasicPropertySet getPropertyValues() {
		return tableModel.getPropertyValues();
	}

	public PropertyValue getSelectedPropertyValue() {
		PropertyRow selectedRow = getSelectedRow();
		return selectedRow != null ? selectedRow.getPropertyValue() : null;
	}

	public void stopCellEditing() {
		if(table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
	}

	public boolean rowIsEditable() {
		int selectedRow = table.getSelectedRow();
		return selectedRow != -1 && 
			tableModel.isCellEditable(selectedRow, 1);
	}
	
}