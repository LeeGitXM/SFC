package com.ils.sfc.designer.stepEditor.confirmControllers;

import javax.swing.table.AbstractTableModel;

import com.ils.sfc.common.rowconfig.ConfirmControllersConfig;

@SuppressWarnings("serial")
public class ConfirmControllersTableModel extends AbstractTableModel {
	private static final String[] columnNames = {"Key", "Check SP for 0", "Check Path to Valve"};
	public static final int KEY_COLUMN = 0;
	public static final int CHECK_SP_COLUMN = 1;
	public static final int CHECK_PATH_COLUMN = 2;
	private ConfirmControllersConfig config;
	
	public String getColumnName(int col) {
        return  getColumnNames()[col];
    }
    
	public String[] getColumnNames() {
		return columnNames;
	}
	
    public ConfirmControllersConfig.Row getRowObject(int i) {
    	return config.getRows().get(i);
    }
    
	public int getRowCount() { return config.getRows().size(); }
    
    public int getColumnCount() { return  getColumnNames().length; }
    
    public boolean isCellEditable(int row, int col) { 
    	return true;
    }
 
    public Object getValueAt(int row, int col) {
    	ConfirmControllersConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.key;
    		case 1: return Boolean.valueOf(rowObj.checkSPFor0);
    		case 2: return Boolean.valueOf(rowObj.checkPathToValve);
    		default: return null;
     	}
    }

    public void setValueAt(Object value, int row, int col) {
    	ConfirmControllersConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: rowObj.key = (String)value; break;
    		case 1: rowObj.checkSPFor0 = ((Boolean)value).booleanValue(); break;
    		case 2: rowObj.checkPathToValve = ((Boolean)value).booleanValue(); break;
    	}
    }

	public void addRow() {
		config.getRows().add(new ConfirmControllersConfig.Row());
		fireTableStructureChanged();
	}

	public void removeSelectedRow(int row) {
		if(row == -1) return;
		config.getRows().remove(row);
		fireTableStructureChanged();
	}

	public void setConfig(ConfirmControllersConfig config) {
		this.config = config;		
	}
    
}
