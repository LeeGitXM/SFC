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
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class CollectDataPanel extends RowEditorPanel {
	private CollectDataConfig config;
	private static final String[] colHeaderTooltips = {
			"Recipe data location that specifies the controller", 
			"Recipe data key that specifies the controller",  
			"Full tag path whose value will be read",
			"Type of value aggregation (current, average, minimum, maximum, standard deviation)", 
			"Time window that is used for value types other than current",
			"Value that will be returned if value cannot be read"};
	private TableHeaderWithTooltips tableHeader;
	
	public CollectDataPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}
	
	public void setConfig(PropertyValue pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (CollectDataConfig)RowConfig.fromJSON(json, CollectDataConfig.class);
		tableModel = new CollectDataTableModel(stepController);
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
					col == CollectDataTableModel.TAG_COLUMN || 
					col == CollectDataTableModel.KEY_COLUMN);
			}			
		});
	}
		
	@Override
	protected void doEdit() {
		super.doEdit();
		int selectedColumn = table.getSelectedColumn();
//PAH		if(selectedColumn == CollectDataTableModel.KEY_COLUMN ) {
//PAH			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
//PAH			stepController.getRecipeDataBrowser().activate(this);
//PAH		}
		if(selectedColumn == CollectDataTableModel.TAG_COLUMN ) {
			stepController.getTagBrowser().setValue(getSelectedValue());
			stepController.getTagBrowser().activate(this);			
		}
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
