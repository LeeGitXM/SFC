package com.ils.sfc.designer.stepEditor.rowEditor.manualData;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class ManualDataPanel extends RowEditorPanel {
	private ManualDataEntryConfig config;
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());
	private static final String[] colHeaderTooltips = {
			"Recipe data key or tag name for the entered value", 
			"Recipe data destination or tag for the entered value ", 
			"Prompt", 
			"Units for automatic conversion if destination has different units", 
			"Default value.  Use Recipe to show current value in destination", 
			"Optional Low Limit for validating user input", 
			"Optional High Limit for validating user input"};
	private TableHeaderWithTooltips tableHeader;
	
	public ManualDataPanel(StepEditorController controller, int index) {
		super(controller, index, false);
		log.tracef("Creating a %s", getClass().getName());
	}

	public void setConfig(PropertyValue pvalue) {
		// Changing to a different config 
		log.trace("In setConfig() for a ManualDataPanel");
		this.pvalue = pvalue;
		String json = (String) pvalue.getValue();
		config = (ManualDataEntryConfig)RowConfig.fromJSON(json, ManualDataEntryConfig.class);
		tableModel = new ManualDataTableModel(stepController);
		
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
					col == ManualDataTableModel.UNITS_COL || 
					col == ManualDataTableModel.KEY_COL
					);
			}			
		});	
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
	@Override
	protected void doEdit() {
		log.trace("In doEdit() for a ManualDataPanel");
		super.doEdit();
		int selectedColumn = table.getSelectedColumn();
//PAH		if(selectedColumn == ManualDataTableModel.KEY_COL ) {
//PAH			stepController.getRecipeDataBrowser().setValue(getSelectedValue());
//PAH			stepController.getRecipeDataBrowser().activate(this);
//PAH		}
//PAH		else 
		if(selectedColumn == ManualDataTableModel.UNITS_COL) {
			stepController.getUnitChooser().activate(this);
		}
	}
}
