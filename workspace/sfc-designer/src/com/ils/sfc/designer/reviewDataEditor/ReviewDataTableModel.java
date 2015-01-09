package com.ils.sfc.designer.reviewDataEditor;

import javax.swing.table.AbstractTableModel;

import com.ils.sfc.common.recipe.ReviewDataConfig;

@SuppressWarnings("serial")
public class ReviewDataTableModel extends AbstractTableModel {
	public static final int VALUE_COLUMN = 1;
	private static final String[] columnNames = {"Configuration Key", "Value Key", "Destination", "Prompt", "Units"};
	private ReviewDataConfig config;
	
	public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public ReviewDataConfig.Row getRowObject(int i) {
    	return config.getRows().get(i);
    }
    
	public int getRowCount() { return config.getRows().size(); }
    
    public int getColumnCount() { return columnNames.length; }
    
    public Object getValueAt(int row, int col) {
    	ReviewDataConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.configKey;
    		case 1: return rowObj.valueKey;
    		case 2: return rowObj.recipeScope;
    		case 3: return rowObj.prompt;
    		case 4: return rowObj.units;
    		default: return null;
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
