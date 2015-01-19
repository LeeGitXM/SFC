package com.ils.sfc.designer.reviewDataEditor;

import javax.swing.table.AbstractTableModel;

import com.ils.sfc.common.recipe.ReviewDataConfig;

@SuppressWarnings("serial")
public class ReviewDataTableModel extends AbstractTableModel {
	public static final int VALUE_COLUMN = 1;
	private static final String[] columnNames = {"Configuration Key", "Value Key", "Destination", "Prompt", "Unit Type", "Units"};
	private ReviewDataConfig config;
	
	public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public ReviewDataConfig.Row getRowObject(int i) {
    	return config.getRows().get(i);
    }
    
	public int getRowCount() { return config.getRows().size(); }
    
    public int getColumnCount() { return columnNames.length; }
    
    public boolean isCellEditable(int row, int col) { 
    	return true;
    }
 
    public Object getValueAt(int row, int col) {
    	ReviewDataConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.configKey;
    		case 1: return rowObj.valueKey;
    		case 2: return rowObj.recipeScope;
    		case 3: return rowObj.prompt;
    		case 4: return rowObj.unitType;
    		case 5: return rowObj.units;
    		default: return null;
    	}
    }

    public void setValueAt(Object value, int row, int col) {
    	ReviewDataConfig.Row rowObj = getRowObject(row);
    	String sValue = (String)value;
    	switch(col) {
    		case 0: rowObj.configKey = sValue; break;
    		case 1: rowObj.valueKey = sValue; break;
    		case 2: rowObj.recipeScope = sValue; break;
    		case 3: rowObj.prompt = sValue; break;
    		case 4: rowObj.unitType = sValue; break;
    		case 5: rowObj.units = sValue; break;
    	}
    }

	public void addRow() {
		config.getRows().add(new ReviewDataConfig.Row());
		fireTableStructureChanged();
	}

	public void removeSelectedRow(int row) {
		if(row == -1) return;
		config.getRows().remove(row);
		fireTableStructureChanged();
	}

	public void setConfig(ReviewDataConfig config) {
		this.config = config;		
	}
    
}
