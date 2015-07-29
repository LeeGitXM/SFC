package com.ils.sfc.designer.stepEditor.rowEditor.collectData;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.rowconfig.CollectDataConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class CollectDataPanel extends RowEditorPanel  {
	private CollectDataConfig config;
	private JComboBox<Object> errorHandlingCombo;
	
	public CollectDataPanel(StepEditorController controller, int index) {
		super(controller, index, true);
		JPanel errorHandlingPanel = new JPanel(new FlowLayout());
		upperPanel.add(errorHandlingPanel, BorderLayout.CENTER);
		errorHandlingCombo = new JComboBox<Object>(Constants.ERROR_HANDLING_CHOICES);
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

	protected void doEdit() {
		super.doEdit();
		if(selectedColumn == CollectDataTableModel.TAG_COLUMN) {
			stepController.getTagBrowser().activate(this);
		}
	}				

	public void setConfig(PropertyValue<String> pvalue) {
		// generic part:
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (CollectDataConfig)RowConfig.fromJSON(json, CollectDataConfig.class);
		
		tableModel = new CollectDataTableModel();
		tableModel.setConfig(config);
		table = new JTable(tableModel);

		tablePanel = createTablePanel(table, tablePanel,
			new RowCellEditor(), new GenericCellRenderer());
		
		// specific part:
		errorHandlingCombo.setSelectedItem(config.errorHandling);
	}

	@Override
	protected void columnSelected(int col ) {
		buttonPanel.getEditButton().setEnabled(col == CollectDataTableModel.TAG_COLUMN);
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
}
