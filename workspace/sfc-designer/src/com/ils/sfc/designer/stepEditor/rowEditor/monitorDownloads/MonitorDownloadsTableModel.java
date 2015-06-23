package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class MonitorDownloadsTableModel extends RowTableModel {

private static final String[] columnNames = {"Input Key", "Output Key", "Label Attribute", "Units"};
	
	public String[] getColumnNames() {
		return columnNames;
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
