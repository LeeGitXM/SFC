package com.ils.sfc.designer.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.util.IlsProperty;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
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

	public PropertyEditor() {
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel testPanel = new JPanel(new FlowLayout());
		testPanel.add(testButton);
		add(testPanel, BorderLayout.SOUTH);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doTest();}
		});
		
		table.setDefaultEditor(Object.class, new PropertyCellEditor());
		table.setDefaultRenderer(Object.class, new PropertyCellRenderer());
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
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
					int valueCol = 1;
					String currentValue = (String)tableModel.getValueAt(row, valueCol);
					JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(table);
					TextEditorDialog dlg = new TextEditorDialog(currentValue,
						frame, tableModel, row, valueCol);
					dlg.setVisible(true);
				}
			}
		});
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
		ChartUIElement element = tableModel.getElement();
		String sql = element.getOrDefault(IlsProperty.SQL);
		String database = element.getOrDefault(IlsProperty.DATABASE);
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

	public static void main(String[] args) {
		final PropertyEditor editor = new PropertyEditor();
		List<PropertyRow> plist = new ArrayList<PropertyRow>();
		
		BasicProperty<Double> pdub = new BasicProperty<Double>("dvalue", Double.class);
		PropertyValue<Double> pval = new PropertyValue<Double>(pdub, Double.valueOf(2.));
		plist.add(new PropertyRow(pval,null));
		/*
		plist.add(new PropertyValue());
		plist.add(new StringPropertyValue("", new Property<String>(0, "name", "Cat1", "advice",  String.class, null), "fred"));
		Property<String> choiceProperty = new Property<String>(0, "name", "Cat1", "advice",  String.class, null);
		choiceProperty.setChoices(new String[] { "rob", "john", "richard" });
		plist.add(new StringPropertyValue("", choiceProperty, "rob"));
		plist.add(new BooleanPropertyValue("", new Property<Boolean>(0, "canUse", "Cat2", "advice",  Boolean.class, null), Boolean.TRUE));
		//plist.add(new Property("bool", Boolean.TRUE, null));
		//plist.add(new Property("int", Integer.valueOf(3), null));
		 * */
		 
		//editor.setProperties(plist);

	}

	public void setElement(ChartUIElement element) {
		tableModel.setElement(element);
		// HACK!! "testable" and "doTest" should be more general, but for now
		// we special-case it to be just SQL queries...
		testButton.setVisible(element.contains(IlsProperty.SQL) && 
			element.contains(IlsProperty.DATABASE));
	}
	
	public List<PropertyValue> getValues() {
		List<PropertyValue> values = new ArrayList<PropertyValue>();
		for(PropertyRow row: tableModel.getRows()) {
			values.add(row.getPropertyValue());
		}
		return values;
	}
	
}