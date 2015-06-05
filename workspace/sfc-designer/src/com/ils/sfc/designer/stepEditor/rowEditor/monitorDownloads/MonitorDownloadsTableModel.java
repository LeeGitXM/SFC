package com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads;

import com.ils.sfc.common.rowconfig.MonitorDownloadConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class MonitorDownloadsTableModel extends RowTableModel {

private static final String[] columnNames = {"Key", "Label Attribute", "Units"};
	public static final int KEY_COLUMN = 0;
	public static final int LABEL_ATTRIB_COLUMN = 1;
	public static final int UNITS_COLUMN = 2;
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
	public MonitorDownloadConfig.Row getRowObject(int i) {
		return ((MonitorDownloadConfig)rowConfig).getRows().get(i);
	}   
	
	public Object getValueAt(int row, int col) {
		MonitorDownloadConfig.Row rowObj = getRowObject(row);
		switch(col) {
			case 0: return rowObj.key;
			case 1: return rowObj.labelAttribute;
			case 2: return rowObj.units;
			default: return null;
	 	}
	}
	
	public void setValueAt(Object value, int row, int col) {
		MonitorDownloadConfig.Row rowObj = getRowObject(row);
		switch(col) {
			case 0: rowObj.key = (String)value; break;
			case 1: rowObj.labelAttribute = (String)value; break;
			case 2: rowObj.units = (String)value; break;
		}
	}

}
