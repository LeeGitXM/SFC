package com.ils.sfc.designer.stepEditor.rowEditor.pvMonitor;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.ils.sfc.designer.stepEditor.rowEditor.manualData.ManualDataTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class PVMonitorPanel extends RowEditorPanel {
	private PVMonitorConfig config;
	private static final String[] colHeaderTooltips = {
			"Enabled",
			"PV Key",
			"Target Type",
			"Target Name",
			"Strategy",
			"Limits",
			"Download",
			"Persistence",
			"Consistency",
			"Deadtime",  
			"Tolerance", 
			"Type",
			"Status"};
	private TableHeaderWithTooltips tableHeader;
	
	public PVMonitorPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (PVMonitorConfig)RowConfig.fromJSON(json, PVMonitorConfig.class);
		tableModel = new PVMonitorTableModel(stepController);
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tableHeader = new TableHeaderWithTooltips(table.getColumnModel(), colHeaderTooltips);
		table.setTableHeader(tableHeader);
		tablePanel = createTablePanel(table, tablePanel, new RowCellEditor(),
			new GenericCellRenderer());
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(
					col == PVMonitorTableModel.PV_KEY_COLUMN || 
					col == PVMonitorTableModel.TARGET_NAME_COLUMN
				);
			}			
		});	
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
//PAH	@Override
//PAH	protected void doEdit() {
//PAH		super.doEdit();
//PAH		int selectedColumn = table.getSelectedColumn();
//PAH		if(selectedColumn == PVMonitorTableModel.PV_KEY_COLUMN || 
//PAH		   selectedColumn == PVMonitorTableModel.TARGET_NAME_COLUMN) {
//PAH			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
//PAH			stepController.getRecipeDataBrowser().activate(this);
//PAH		}
//PAH	}
}
