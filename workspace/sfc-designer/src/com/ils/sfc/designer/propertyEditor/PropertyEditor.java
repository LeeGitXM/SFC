package com.ils.sfc.designer.propertyEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.designer.reviewDataEditor.ReviewDataEditorDialog;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;

/** A property editor grid/table */
@SuppressWarnings("serial")
public class PropertyEditor extends JPanel {
	private final PropertyTableModel tableModel = new PropertyTableModel();
	private final JTable table = new JTable(tableModel);
	
	// some support for modeless external string editing:
	private String stringEditValue;
	private int stringEditRow;
	private ActionListener stringEditListener;
	
	public PropertyEditor() {
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

	/** Set a listener that will be invoked for external string editing (in
	 *  place of the normal default behavior)
	 */
	public void setStringEditListener(ActionListener stringEditListener) {
		this.stringEditListener = stringEditListener;
	}

	private void doEdit(int row, int col) {
		PropertyRow stringEditRowObject = tableModel.getRowObject(row);
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(table);
		if(stringEditRowObject.getProperty().equals(IlsProperty.REVIEW_DATA)) {
			// kind of a hack here...REVIEW_DATA is a pseudo-property
			// the complex values are held in the local recipe data of the step
			ReviewDataEditorDialog dlg = new ReviewDataEditorDialog(frame, tableModel.getStepId(), false);
			dlg.setVisible(true);						
		}
		else if(stringEditRowObject.getProperty().equals(IlsProperty.REVIEW_DATA_WITH_ADVICE)) {
			// kind of a hack here...REVIEW_DATA_WITH_ADVICE is a pseudo-property
			// the complex values are held in the local recipe data of the step
			ReviewDataEditorDialog dlg = new ReviewDataEditorDialog(frame, tableModel.getStepId(), true);
			dlg.setVisible(true);						
		}
		else {
			if(stringEditListener == null) {
				if(!stringEditRowObject.isEditableString()) return;
					PropertyStringEditorDialog dlg = new PropertyStringEditorDialog(frame, tableModel, row);
				dlg.setVisible(true);
			}
			else {
				stringEditRow = row;
				stringEditValue = (String) tableModel.getValueAt(row, col);
				stringEditListener.actionPerformed(null);
			}
		}
	}

	/** Get the row that external string editing was last invoked on. */
	public String getStringEditValue() {
		return stringEditValue;
	}
	
	/** set the externally edited string */
	public void setStringEditValue(String value) {
		tableModel.setValueAt(value, stringEditRow, 1);
	}
	
	public PropertyTableModel getTableModel() {
		return tableModel;
	}

	public boolean hasChanged() {
		return tableModel.hasChanged();
	}
	
	public void setPropertyValues(BasicPropertySet propertyValues, boolean sortInternal) {
		tableModel.setPropertyValues(propertyValues, sortInternal);
	}
	
	@SuppressWarnings("rawtypes")
	public BasicPropertySet getPropertyValues() {
		return tableModel.getPropertyValues();
	}

	public PropertyValue<?> getSelectedPropertyValue() {
		int selectedRow = table.getSelectedRow();
		if(selectedRow > 0) {
			PropertyRow selectedRowObject = tableModel.getRowObject(selectedRow);
			return selectedRowObject.getPropertyValue();
		}
		else {
			return null;
		}
	}
	
}