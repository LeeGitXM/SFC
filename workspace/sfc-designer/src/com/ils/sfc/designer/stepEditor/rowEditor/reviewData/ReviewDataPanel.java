package com.ils.sfc.designer.stepEditor.rowEditor.reviewData;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.manualData.ManualDataTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class ReviewDataPanel extends RowEditorPanel {
	private ReviewDataConfig config;
	
	public ReviewDataPanel(StepEditorController controller, int index) {
		super(controller, index, false);
	}

	public void setConfig(PropertyValue<String> pvalue) {
		// Changing to a different config 
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ReviewDataConfig)RowConfig.fromJSON(json, ReviewDataConfig.class);
		boolean showAdvice = pvalue.getProperty().equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				pvalue.getProperty().equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE);
		tableModel = new ReviewDataTableModel(showAdvice);
		table = new JTable(tableModel);
		tableModel.setConfig(config);
		tablePanel = createTablePanel(table, tablePanel,
				new RowCellEditor(), new GenericCellRenderer());		
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				boolean isUnitsCol = 
					((ReviewDataTableModel)tableModel).isUnitsColumn(col) ||
					((ReviewDataTableModel)tableModel).isRecipeKeyColumn(col);
				buttonPanel.getEditButton().setEnabled(isUnitsCol);
			}			
		});	
	}

	@Override
	protected void doEdit() {
		super.doEdit();
		int col = table.getSelectedColumn();
		if(((ReviewDataTableModel)tableModel).isUnitsColumn(col)) {
			stepController.getUnitChooser().activate(this);
		}
		else if(((ReviewDataTableModel)tableModel).isRecipeKeyColumn(col)) {
			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
			stepController.getRecipeDataBrowser().activate(this);
		}		
	}

	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
