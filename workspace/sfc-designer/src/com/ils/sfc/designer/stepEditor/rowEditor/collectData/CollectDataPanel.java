package com.ils.sfc.designer.stepEditor.rowEditor.collectData;

import javax.swing.JTable;




import com.ils.sfc.common.rowconfig.CollectDataConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class CollectDataPanel extends RowEditorPanel {
	private CollectDataConfig config;
	
	public CollectDataPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}
	
	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (CollectDataConfig)RowConfig.fromJSON(json, CollectDataConfig.class);
		tableModel = new CollectDataTableModel(stepController);
		tableModel.setConfig(config);		
		table = new JTable(tableModel);
		tablePanel = createTablePanel(table, tablePanel, new RowCellEditor(),
			new GenericCellRenderer());
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
