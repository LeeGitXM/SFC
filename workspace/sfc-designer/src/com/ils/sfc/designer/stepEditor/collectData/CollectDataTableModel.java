package com.ils.sfc.designer.stepEditor.collectData;

import javax.swing.table.AbstractTableModel;

import com.ils.sfc.common.rowconfig.CollectDataConfig;

@SuppressWarnings("serial")
public class CollectDataTableModel extends AbstractTableModel {

	public static final int LOCATION_COLUMN = 1;
	public static final int TAG_COLUMN = 2;
	public static final int VALUE_TYPE_COLUMN = 3;
	public static final int PAST_WINDOW_COLUMN = 4;
	private static final String[] columnNames = {"Recipe Key", "Location", "Tag Path", "Value Type",  "Past Window", "Default Value"};
	private CollectDataConfig config;
	
	public String getColumnName(int col) {
        return  getColumnNames()[col];
    }
    
	public String[] getColumnNames() {
		return columnNames;
	}
	
    public CollectDataConfig.Row getRowObject(int i) {
    	return config.getRows().get(i);
    }
    
	public int getRowCount() { return config.getRows().size(); }
    
    public int getColumnCount() { return  getColumnNames().length; }
    
    public boolean isCellEditable(int row, int col) { 
    	return true;
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

	public void addRow() {
		config.getRows().add(new CollectDataConfig.Row());
		fireTableStructureChanged();
	}

	public void removeSelectedRow(int row) {
		if(row == -1) return;
		config.getRows().remove(row);
		fireTableStructureChanged();
	}

	public void setConfig(CollectDataConfig config) {
		this.config = config;		
	}

}
