package com.ils.sfc.designer.stepEditor.collectData;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.CollectDataConfig;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.designer.ColumnSelectionAdapter;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class CollectDataPanel extends EditorPanel implements ValueHolder {
	private static LoggerEx logger = LogUtil.getLogger(CollectDataPanel.class.getName());
	private final StepEditorController controller;
	private ButtonPanel buttonPanel = new ButtonPanel(true, true, true, true, false, false);
	private CollectDataTableModel tableModel;
	private JTable table;
	private PropertyValue<String> pvalue;
	private CollectDataConfig config;
	private JPanel tablePanel;
	private JComboBox<Object> errorHandlingCombo;
	
	public CollectDataPanel(StepEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		JPanel upperPanel = new JPanel(new BorderLayout());
		add(upperPanel, BorderLayout.NORTH);
		upperPanel.add(buttonPanel, BorderLayout.NORTH);

		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAccept();}
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
		
		JPanel errorHandlingPanel = new JPanel(new FlowLayout());
		upperPanel.add(errorHandlingPanel, BorderLayout.CENTER);
		errorHandlingCombo = new JComboBox<Object>(IlsSfcNames.ERROR_HANDLING_CHOICES);
		errorHandlingCombo.setBackground(java.awt.Color.white);
		errorHandlingCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doErrorHandling();
			}
		});
		errorHandlingPanel.add(new JLabel("Error Handling:"));
		errorHandlingPanel.add(errorHandlingCombo);
	}

	private void doErrorHandling() {
		config.errorHandling = (String) errorHandlingCombo.getSelectedItem();
	}				

	private void doEdit() {
		int selectedColumn = table.getSelectedColumn();
		if(table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
		if(selectedColumn == CollectDataTableModel.TAG_COLUMN) {
			controller.getTagBrowser().activate(this);
		}
	}				

	private void doAccept() {
		try {
			String json = config.toJSON();
			controller.getPropertyEditor().getPropertyEditor().setSelectedValue(json);
			controller.getPropertyEditor().activate(myIndex);
		} 
		catch(Exception e ) {
			logger.error("Error serializing collect data config");
		}
	}

	private void doAdd() {
		tableModel.addRow();
	}
	
	private void doRemove() {
		int selectedRow = table.getSelectedRow();
		tableModel.removeSelectedRow(selectedRow);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		if(config != null) {
			doAccept();
		}
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		if(json != null && json.length() > 0) {
			try {
				config = CollectDataConfig.fromJSON(json);
			} catch (Exception e) {
				config = new CollectDataConfig();
				logger.error("Error deserializing collect data config");
			} 
		}
		else {
			config = new CollectDataConfig();
		}
		errorHandlingCombo.setSelectedItem(config.errorHandling);
		tableModel = new CollectDataTableModel();
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.getColumnModel().addColumnModelListener(new ColumnSelectionAdapter() {
			 public void columnSelectionChanged(ListSelectionEvent e) {
				int selectedColumn = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(
					selectedColumn == CollectDataTableModel.TAG_COLUMN);
			 }
		});
		table.setDefaultEditor(Object.class, new CollectDataCellEditor());
		table.setDefaultRenderer(Object.class, new CollectDataCellRenderer());
		table.setRowHeight(20);
		table.setRowMargin(3);	
		table.setShowGrid(true);
		tableModel.setConfig(config);
		if(tablePanel != null) remove(tablePanel);
		tablePanel = new JPanel(new BorderLayout());	
		tablePanel.setBorder(new EmptyBorder(10,10,10,10));
		add(tablePanel, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(
			new CompoundBorder(
				new EmptyBorder(10,10,0,10), 
				new LineBorder(Color.black)));
		tablePanel.add(scroll, BorderLayout.CENTER);
	}

	@Override
	public void setValue(Object value) {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		tableModel.setValueAt(value, row, col);
	}
	
}
