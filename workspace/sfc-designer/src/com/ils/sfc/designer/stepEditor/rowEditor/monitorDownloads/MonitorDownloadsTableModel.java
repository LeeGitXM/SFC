package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class MonitorDownloadsTableModel extends RowTableModel {
	public static final int LABEL_COL = 1;
	public static final int UNITS_COL = 2;

	private static final String[] columnNames = {"Key", "Label Attribute", "Units"};
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
	@Override
	protected boolean isComboColumn(int col) {
		return col == LABEL_COL;
	}

	@Override
	protected boolean isTextColumn(int col) {
		return col != LABEL_COL;
	}

	@Override
	protected String[] getChoices(int row, int col) {
		String[] choices = null;
		if(col == LABEL_COL) { // recipe scope
			choices = Constants.MONITOR_DOWNLOADS_LABEL_CHOICES;
		}
		return choices;
	}		
	
	public MonitorDownloadsConfig.Row getRowObject(int i) {
		return ((MonitorDownloadsConfig)rowConfig).getRows().get(i);
	}   
	
	public Object getValueAt(int row, int col) {
		MonitorDownloadsConfig.Row rowObj = getRowObject(row);
		switch(col) {
			case 0: return rowObj.key;
			case 1: return rowObj.labelAttribute;
			case 2: return rowObj.units;
			default: return null;
	 	}
	}
	
	public void setValueAt(Object value, int row, int col) {
		MonitorDownloadsConfig.Row rowObj = getRowObject(row);
		switch(col) {
			case 0: rowObj.key = (String)value; break;
			case 1: rowObj.labelAttribute = (String)value; break;
			case 2: rowObj.units = (String)value; break;
		}
	}

}
