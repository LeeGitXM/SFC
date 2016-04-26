package com.ils.sfc.designer.stepEditor.rowEditor.confirmControllers;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.ConfirmControllersConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.collectData.CollectDataTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class ConfirmControllersPanel extends RowEditorPanel {
	private ConfirmControllersConfig config;
	
	public ConfirmControllersPanel(StepEditorController controller, int index) {
		super(controller, index, false);
		}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ConfirmControllersConfig)RowConfig.fromJSON(json, ConfirmControllersConfig.class);
		tableModel = new ConfirmControllersTableModel();
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tablePanel = createTablePanel(table, tablePanel, new RowCellEditor(),
			new GenericCellRenderer());
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(col == ConfirmControllersTableModel.KEY_COLUMN);
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
		if(selectedColumn == ConfirmControllersTableModel.KEY_COLUMN ) {
			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
			stepController.getRecipeDataBrowser().activate(this);
		}
	}
}
