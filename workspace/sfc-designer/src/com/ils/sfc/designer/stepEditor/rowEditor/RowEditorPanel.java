package com.ils.sfc.designer.stepEditor.rowEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.ColumnSelectionAdapter;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/** An editor panel for row-value properties, typically serialized in JSON form. */
@SuppressWarnings("serial")
public abstract class RowEditorPanel extends EditorPanel implements ValueHolder {
	private static LoggerEx logger = LogUtil.getLogger(RowEditorPanel.class.getName());
	protected PropertyValue pvalue;
	protected JTable table;
	protected int selectedColumn;
	protected JPanel tablePanel;
	protected RowTableModel tableModel;
	protected StepEditorController stepController;
	protected JPanel upperPanel;
	protected ButtonPanel buttonPanel = new ButtonPanel(true, true, true, true, false, true, false, false);

	protected RowEditorPanel(StepEditorController controller, int index, boolean addUpperPanel) {
		super(controller, index);
		logger.infof("Creating a RowEditorPanel()...");
		this.stepController = controller;
		upperPanel = new JPanel(new BorderLayout());
		if(addUpperPanel) {
			add(upperPanel, BorderLayout.NORTH);
			upperPanel.add(buttonPanel, BorderLayout.NORTH);
		}
		else {
			add(buttonPanel, BorderLayout.NORTH);
		}
		
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAccept();}
		});
		buttonPanel.getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}
		});
		buttonPanel.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}
		});
		buttonPanel.getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}
		});
		buttonPanel.getEditButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doEdit();}
		});
		buttonPanel.getEditButton().setEnabled(false);		
	}
	
	/** Subclasses should extend for specific editing behavior. */
	protected void doEdit() {
		selectedColumn = table.getSelectedColumn();
		if(table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
	}				
	
	/** Create the typical table for an editor panel (holds multi-row configuration step property). */
	protected JPanel createTablePanel(final JTable table, JPanel tablePanel,
		TableCellEditor cellEditor, TableCellRenderer cellRenderer) {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.getColumnModel().addColumnModelListener(new ColumnSelectionAdapter() {
			 public void columnSelectionChanged(ListSelectionEvent e) {
				columnSelected(table.getSelectedColumn());
			 }
		});
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setShowGrid(true);
		table.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == 9) {
					table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
					table.getEditorComponent().requestFocusInWindow();
				}
			}
			
		});
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(
			new CompoundBorder(
				new EmptyBorder(10,10,0,10), 
				new LineBorder(Color.black)));
		if(tablePanel != null) remove(tablePanel);
		tablePanel = new JPanel(new BorderLayout());	
		tablePanel.add(scroll, BorderLayout.CENTER);
		tablePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(tablePanel, BorderLayout.CENTER);		
		table.setDefaultEditor(Object.class, cellEditor);
		table.setDefaultRenderer(Object.class, cellRenderer);
		return tablePanel;
	}
	
	protected void doAdd() {
		int selectedRow = table.getSelectedRow();
		logger.tracef("In doAdd(), the selectedRow is %d", selectedRow);
		if (selectedRow >= 0){
			tableModel.addRow(selectedRow);
		}
		else {
			tableModel.addRow();
		}
	}
	
	protected void doRemove() {
		int selectedRow = table.getSelectedRow();
		tableModel.removeSelectedRow(selectedRow);
	}
	
	protected void doCancel() {
		cancel();
	}
	
	protected void doAccept() {
		try {
			String json = getConfig().toJSON();
			stepController.getPropertyEditor().getPropertyEditor().setSelectedValue(json);
			stepController.getPropertyEditor().activate(myIndex);
			super.accept();
		} 
		catch(Exception e ) {
			logger.error("Error serializing data config");
		}
	}

	public Object getSelectedValue() {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		if(row != -1 && col != -1) {
		return tableModel.getValueAt(row, col);
		}
		else {
			return null;
		}
	}

	protected abstract RowConfig getConfig();
	
	@Override
	public void setValue(Object value) {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		tableModel.setValueAt(value, row, col);
	}
	
	@Override
	public void commitEdit() {
		if(table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
	}
}
