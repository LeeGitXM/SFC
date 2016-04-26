package com.ils.sfc.designer.stepEditor.rowEditor.manualData;

import javax.swing.JTable;



import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.collectData.CollectDataTableModel;
import com.ils.sfc.designer.stepEditor.rowEditor.confirmControllers.ConfirmControllersTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class ManualDataPanel extends RowEditorPanel {
	private ManualDataEntryConfig config;
	
	public ManualDataPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ManualDataEntryConfig)RowConfig.fromJSON(json, ManualDataEntryConfig.class);
		tableModel = new ManualDataTableModel(stepController);
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tablePanel = createTablePanel(table, tablePanel, new RowCellEditor(),
			new GenericCellRenderer());
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(
					col == ManualDataTableModel.UNITS_COL || 
					col == ManualDataTableModel.KEY_COL
					);
			}			
		});	
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
	@Override
	protected void doEdit() {
		super.doEdit();
		int selectedColumn = table.getSelectedColumn();
		if(selectedColumn == ManualDataTableModel.KEY_COL ) {
			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
			stepController.getRecipeDataBrowser().activate(this);
		}
		else if(selectedColumn == ManualDataTableModel.UNITS_COL) {
			stepController.getUnitChooser().activate(this);
		}
	}
}
