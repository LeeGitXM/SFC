package com.ils.sfc.designer.stepEditor.rowEditor.confirmControllers;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.rowconfig.ConfirmControllersConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class ConfirmControllersTableModel extends RowTableModel {
	private static final String[] columnNames = {"Location", "Key", "Check SP for 0", "Check Path to Valve"};
	public static final int LOCATION_COLUMN = 0;
	public static final int KEY_COLUMN = 1;
	public static final int CHECK_SP_COLUMN = 2;
	public static final int CHECK_PATH_COLUMN = 3;

	public String[] getColumnNames() {
		return columnNames;
	}
	
    public ConfirmControllersConfig.Row getRowObject(int i) {
    	return ((ConfirmControllersConfig)rowConfig).getRows().get(i);
    }   
    
    public Object getValueAt(int row, int col) {
    	ConfirmControllersConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.location; 
    		case 1: return rowObj.key;
    		case 2: return Boolean.valueOf(rowObj.checkSPFor0);
    		case 3: return Boolean.valueOf(rowObj.checkPathToValve);
    		default: return null;
     	}
    }

    public void setValueAt(Object value, int row, int col) {
    	ConfirmControllersConfig.Row rowObj = getRowObject(row);
    	switch(col) {
			case 0: 
				rowObj.location = (String)value; break;
    		case 1: 
    			rowObj.key = (String)value; break;
    		case 2: rowObj.checkSPFor0 = ((Boolean)value).booleanValue(); break;
    		case 3: rowObj.checkPathToValve = ((Boolean)value).booleanValue(); break;
    	}
    }
    
    @Override
    protected boolean isBooleanColumn(int col) {
    	return col == CHECK_SP_COLUMN || col == CHECK_PATH_COLUMN;
    }
    
    @Override
    protected boolean isTextColumn(int col) {
    	return !isBooleanColumn(col);
    }
    
	@Override
	protected String[] getChoices(int row, int col) {
		String[] choices = null;
		if(col == LOCATION_COLUMN) { // recipe scope
			choices = Constants.RECIPE_LOCATION_CHOICES;
		}
		return choices;
	}

	@Override
	protected boolean isComboColumn(int col) {
		return col == LOCATION_COLUMN;
	}

}
