package com.ils.sfc.designer.stepEditor.rowEditor.writeOutput;

import com.ils.sfc.common.rowconfig.WriteOutputConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class WriteOutputTableModel extends RowTableModel {

	public static final int KEY_COLUMN = 0;
	public static final int WRITE_CONFIRM_COLUMN = 1;
	private static final String[] columnNames = {"Key", "Write Confirm"};
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
    public WriteOutputConfig.Row getRowObject(int i) {
    	return ((WriteOutputConfig)rowConfig).getRows().get(i);
    }   
     
    public Object getValueAt(int row, int col) {
    	WriteOutputConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.key;
    		case 1: return rowObj.confirmWrite;
    		default: return null;
    	}
    }

    public void setValueAt(Object value, int row, int col) {
    	WriteOutputConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: rowObj.key = (String)value; break;
    		case 1: rowObj.confirmWrite = (Boolean)value; break;
    	}
    	fireTableCellUpdated(row, col);
    }

}
