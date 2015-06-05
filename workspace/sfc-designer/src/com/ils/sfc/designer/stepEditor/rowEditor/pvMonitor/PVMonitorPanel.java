package com.ils.sfc.designer.stepEditor.rowEditor.pvMonitor;

import javax.swing.JTable;

import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class PVMonitorPanel extends RowEditorPanel {
	private PVMonitorConfig config;
	
	public PVMonitorPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (PVMonitorConfig)RowConfig.fromJSON(json, PVMonitorConfig.class);
		tableModel = new PVMonitorTableModel(stepController);
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tablePanel = createTablePanel(table, tablePanel, new PVMonitorCellEditor(),
			new GenericCellRenderer());
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
	
}
