package com.ils.sfc.designer.stepEditor.rowEditor.reviewFlows;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class ReviewFlowsPanel extends RowEditorPanel {
	private ReviewFlowsConfig config;
	private static final String[] colHeaderTooltips = {
			"Unique identifier for a row for internal use", 
			"Recipe data key or tag name for the first flow",  
			"Recipe data key or tag name for the second flow", 
			"Recipe data destination or tag for the total flow or the special keyword 'sum' to add flow1 and flow2 ", 
			"Prompt", 
			"Units for automatic conversion if destination has different units",
			"Special Instructions"};
	private TableHeaderWithTooltips tableHeader;
	
	public ReviewFlowsPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ReviewFlowsConfig)RowConfig.fromJSON(json, ReviewFlowsConfig.class);
		tableModel = new ReviewFlowsTableModel();
		table = new JTable(tableModel);
		tableHeader = new TableHeaderWithTooltips(table.getColumnModel(), colHeaderTooltips);
		table.setTableHeader(tableHeader);
		tableModel.setConfig(config);
		tablePanel = createTablePanel(table, tablePanel,
				new RowCellEditor(), new GenericCellRenderer());		
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(
					col == ReviewFlowsTableModel.UNITS_COLUMN ||
					((ReviewFlowsTableModel)table.getModel()).isRecipeKeyColumn(col));
			}			
		});	
	}

	@Override
	protected void doEdit() {
		super.doEdit();
		int col = table.getSelectedColumn();
		if(col == ReviewFlowsTableModel.UNITS_COLUMN) {
			stepController.getUnitChooser().activate(this);
		}
//PAH		else if(((ReviewFlowsTableModel)table.getModel()).isRecipeKeyColumn(col)) {
//PAH			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
//PAH			stepController.getRecipeDataBrowser().activate(this);			
//PAH		}
	}

	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
