package com.ils.sfc.designer.stepEditor.rowEditor.collectData;

import com.ils.sfc.common.rowconfig.CollectDataConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class CollectDataTableModel extends RowTableModel {

	public static final int LOCATION_COLUMN = 1;
	public static final int TAG_COLUMN = 2;
	public static final int VALUE_TYPE_COLUMN = 3;
	public static final int PAST_WINDOW_COLUMN = 4;
	private static final String[] columnNames = {"Recipe Key", "Location", "Tag Path", "Value Type",  "Past Window (min)", "Default Value"};
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
    public CollectDataConfig.Row getRowObject(int i) {
    	return ((CollectDataConfig)rowConfig).getRows().get(i);
    }   

    public Object getValueAt(int row, int col) {
    	CollectDataConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.recipeKey;
    		case 1: return rowObj.location;
    		case 2: return rowObj.tagPath;
    		case 3: return rowObj.valueType;
    		case 4: return rowObj.pastWindow;
    		case 5: return rowObj.defaultValue;
    		default: return null;
    	}
    }

    public void setValueAt(Object value, int row, int col) {
    	CollectDataConfig.Row rowObj = getRowObject(row);
    	String sValue = (String)value;
    	switch(col) {
    		case 0: rowObj.recipeKey = sValue; break;
    		case 1: rowObj.location = sValue; break;
    		case 2: rowObj.tagPath = sValue; break;
    		case 3: rowObj.valueType = sValue; break;
    		case 4: rowObj.pastWindow = sValue; break;
    		case 5: rowObj.defaultValue = sValue; break;
    	}
    	fireTableCellUpdated(row, col);
    }

}
