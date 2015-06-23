package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import javax.swing.JTable;

import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class MonitorDownloadsPanel extends RowEditorPanel {
	private MonitorDownloadsConfig config;
	
	public MonitorDownloadsPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (MonitorDownloadsConfig)RowConfig.fromJSON(json, MonitorDownloadsConfig.class);
		tableModel = new MonitorDownloadsTableModel();
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tablePanel = createTablePanel(table, tablePanel, new MonitorDownloadsCellEditor(),
			new GenericCellRenderer());
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
