package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.common.rowconfig.RowConfig;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.ils.sfc.designer.stepEditor.rowEditor.GenericCellRenderer;
import com.ils.sfc.designer.stepEditor.rowEditor.RowCellEditor;
import com.ils.sfc.designer.stepEditor.rowEditor.RowEditorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.TableHeaderWithTooltips;
import com.ils.sfc.designer.stepEditor.rowEditor.manualData.ManualDataTableModel;
import com.inductiveautomation.ignition.common.config.PropertyValue;

@SuppressWarnings("serial")
public class MonitorDownloadsPanel extends RowEditorPanel {
	private MonitorDownloadsConfig config;
	private static final String[] colHeaderTooltips = {
			"Recipe data key that specifies the tag being monitored",  
			"Specifies what will be displayed, the tagname or ithe item id", 
			"Display units, value will automatically be converted if necessary"};
	private TableHeaderWithTooltips tableHeader;
	
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
		tableHeader = new TableHeaderWithTooltips(table.getColumnModel(), colHeaderTooltips);
		table.setTableHeader(tableHeader);
		tablePanel = createTablePanel(table, tablePanel, new RowCellEditor(),
			new GenericCellRenderer());
		buttonPanel.getEditButton().setEnabled(false);
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int col = table.getSelectedColumn();
				buttonPanel.getEditButton().setEnabled(col == MonitorDownloadsTableModel.UNITS_COL);
			}			
		});	
	}
	
	@Override
	protected void doEdit() {
		super.doEdit();
		stepController.getUnitChooser().activate(this);
	}
	
	@Override
	public RowConfig getConfig() {
		return config;
	}
	
}
