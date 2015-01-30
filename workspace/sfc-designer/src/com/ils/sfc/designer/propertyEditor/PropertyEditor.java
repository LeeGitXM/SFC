package com.ils.sfc.designer.propertyEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.inductiveautomation.ignition.common.config.Property;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.reviewDataEditor.ReviewDataEditorDialog;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** A property editor grid/table */
@SuppressWarnings("serial")
public class PropertyEditor extends JPanel {
	private final PropertyTableModel tableModel = new PropertyTableModel();
	private final JTable table = new JTable(tableModel);
	private boolean okPressed;
	private JButton testButton = new JButton("Test");
	
	// some support for modeless external string editing:
	private String stringEditValue;
	private int stringEditRow;
	private ActionListener stringEditListener;
	
	public PropertyEditor() {
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel testPanel = new JPanel(new FlowLayout());
		testButton.setVisible(false);
		testPanel.add(testButton);
		add(testPanel, BorderLayout.SOUTH);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doTest();}
		});
		
		table.setDefaultEditor(Object.class, new PropertyCellEditor());
		table.setDefaultRenderer(Object.class, new PropertyCellRenderer());
		table.setCellSelectionEnabled(false);  // has side effect of setting row/col selection as well!
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(20);
		table.setRowMargin(3);	
		
		table.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				setColumnWidths();
			}

			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {
				setColumnWidths();
			}
			public void componentHidden(ComponentEvent e) {}
		});
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point pnt = e.getPoint();
				int row = table.rowAtPoint(pnt);
				int col = table.columnAtPoint(pnt);
				if(col == 3) {
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
			}
		});
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	/** Set a listener that will be invoked for external string editing (in
	 *  place of the normal default behavior)
	 */
	public void setStringEditListener(ActionListener stringEditListener) {
		this.stringEditListener = stringEditListener;
	}

	/** Get the row that external string editing was last invoked on. */
	public String getStringEditValue() {
		return stringEditValue;
	}
	
	/** set the externally edited string */
	public void setStringEditValue(String value) {
		tableModel.setValueAt(value, stringEditRow, 1);
	}
	
	private void setColumnWidths() {
		int toolColumnWidth = 20;
		int defaultWidth = (table.getWidth() - toolColumnWidth) / 3;
		for(int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			int width = i == 3 ? toolColumnWidth : defaultWidth;
			column.setMinWidth(width);
			column.setWidth(width);
			column.setPreferredWidth(width);
		}
		table.validate();
	}
	
	private void doTest() {
		BasicPropertySet propertyValues = tableModel.getPropertyValues();
		String sql = propertyValues.getOrDefault(IlsProperty.SQL);
		String database = propertyValues.getOrDefault(IlsProperty.DATABASE);
		Object[] args = {sql, database};
		try {
			PythonCall.TEST_QUERY.exec(args);
		} catch (JythonExecException e) {
			e.printStackTrace();
		}
	}
	
	public PropertyTableModel getTableModel() {
		return tableModel;
	}

	public boolean hasChanged() {
		return tableModel.hasChanged();
	}
	
	public boolean okPressed() {
		return okPressed;
	}

	public void setPropertyValues(BasicPropertySet propertyValues, boolean sortInternal) {
		tableModel.setPropertyValues(propertyValues, sortInternal);
		// HACK!! "testable" and "doTest" should be more general, but for now
		// we special-case it to be just SQL queries...
		testButton.setVisible(propertyValues.contains(IlsProperty.SQL) && 
				propertyValues.contains(IlsProperty.DATABASE));
		setColumnWidths();
	}
	
	@SuppressWarnings("rawtypes")
	public BasicPropertySet getPropertyValues() {
		return tableModel.getPropertyValues();
	}

	public Property<?> getSelectedProperty() {
		int selectedRow = table.getSelectedRow();
		if(selectedRow > 0) {
			PropertyRow selectedRowObject = tableModel.getRowObject(selectedRow);
			return selectedRowObject.getProperty();
		}
		else {
			return null;
		}
	}
	
}