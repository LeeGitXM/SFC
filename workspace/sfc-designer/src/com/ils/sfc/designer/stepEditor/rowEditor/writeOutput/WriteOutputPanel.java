package com.ils.sfc.designer.stepEditor.rowEditor.writeOutput;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.common.rowconfig.WriteOutputConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.ils.sfc.designer.stepEditor.rowEditor.reviewFlows.ReviewFlowsTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class WriteOutputPanel extends RowEditorPanel {
	private WriteOutputConfig config;
	private static final String[] colHeaderTooltips = {
			"Recipe data key that specifies the tag being monitored",  
			"Wait until the write can be confirmed"}; 
	private TableHeaderWithTooltips tableHeader;
	
	public WriteOutputPanel(StepEditorController controller,
			int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (WriteOutputConfig)RowConfig.fromJSON(json, WriteOutputConfig.class);
		tableModel = new WriteOutputTableModel();
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
					col == WriteOutputTableModel.KEY_COLUMN);
			}			
		});	
	}

	@Override
	protected void doEdit() {
		super.doEdit();
		int col = table.getSelectedColumn();
		if(col == WriteOutputTableModel.KEY_COLUMN) {		
			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
			stepController.getRecipeDataBrowser().activate(this);			
		}
	}

	@Override
	public RowConfig getConfig() {
		return config;
	}}
