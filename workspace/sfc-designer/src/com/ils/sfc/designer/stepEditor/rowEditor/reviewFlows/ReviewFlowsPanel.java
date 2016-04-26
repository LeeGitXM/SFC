package com.ils.sfc.designer.stepEditor.rowEditor.reviewFlows;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.manualData.ManualDataTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class ReviewFlowsPanel extends RowEditorPanel {
	private ReviewFlowsConfig config;
	
	public ReviewFlowsPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ReviewFlowsConfig)RowConfig.fromJSON(json, ReviewFlowsConfig.class);
		tableModel = new ReviewFlowsTableModel();
		table = new JTable(tableModel);
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
		else if(((ReviewFlowsTableModel)table.getModel()).isRecipeKeyColumn(col)) {
			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
			stepController.getRecipeDataBrowser().activate(this);			
		}
	}

	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
