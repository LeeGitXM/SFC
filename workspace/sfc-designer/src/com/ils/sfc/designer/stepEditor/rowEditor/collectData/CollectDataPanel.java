package com.ils.sfc.designer.stepEditor.rowEditor.collectData;

import javax.swing.JTable;




import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(col == CollectDataTableModel.TAG_COLUMN);
			}			
		});
	}
		
	@Override
	protected void doEdit() {
		super.doEdit();
		stepController.getTagBrowser().setValue(getSelectedValue());
		stepController.getTagBrowser().activate(this);
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
