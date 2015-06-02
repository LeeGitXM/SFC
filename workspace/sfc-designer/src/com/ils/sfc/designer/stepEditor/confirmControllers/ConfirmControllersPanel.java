package com.ils.sfc.designer.stepEditor.confirmControllers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTable;

import com.ils.sfc.common.rowconfig.ConfirmControllersConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class ConfirmControllersPanel extends EditorPanel {
	private static LoggerEx logger = LogUtil.getLogger(ConfirmControllersPanel.class.getName());
	private final StepEditorController controller;
	private ButtonPanel buttonPanel = new ButtonPanel(true, true, true, false, false, false);
	private ConfirmControllersTableModel tableModel;
	private JTable table;
	private PropertyValue<String> pvalue;
	private ConfirmControllersConfig config;
	private JPanel tablePanel;
	
	public ConfirmControllersPanel(StepEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}
		});
		buttonPanel.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}
		});
		buttonPanel.getRemoveButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}
		});
	}

	@Override
	public void accept() {
		try {
			String json = config.toJSON();
			controller.getPropertyEditor().getPropertyEditor().setSelectedValue(json);
		} 
		catch(Exception e ) {
			logger.error("Error serializing confirm controllers config");
		}
		super.accept();
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
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ConfirmControllersConfig)RowConfig.fromJSON(json, ConfirmControllersConfig.class);
		tableModel = new ConfirmControllersTableModel();
		tableModel.setConfig(config);		
		tablePanel = createTablePanel(tableModel, tablePanel, new ConfirmControllersCellEditor(),
			new ConfirmControllersCellRenderer());
	}
	
}
