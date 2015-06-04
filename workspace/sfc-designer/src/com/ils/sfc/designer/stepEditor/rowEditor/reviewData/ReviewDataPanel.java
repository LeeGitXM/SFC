package com.ils.sfc.designer.stepEditor.rowEditor.reviewData;

import javax.swing.JTable;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
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
				new ReviewDataCellEditor(), new GenericCellRenderer());		
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
