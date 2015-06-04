package com.ils.sfc.designer.stepEditor.rowEditor.reviewData;

import com.ils.sfc.common.rowconfig.CollectDataConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class ReviewDataTableModel extends RowTableModel {
	public static final int VALUE_COLUMN = 1;
	private static final String[] columnNames = {"Config Key", "Value Key", "Destination", "Prompt", "Unit Type", "Units"};
	private static final String[] columnNamesWithAdvice = {"Config Key", "Value Key", "Destination", "Prompt", "Advice", "Unit Type", "Units"};
	private boolean showAdvice;
	
	public ReviewDataTableModel(boolean showAdvice) {
		this.showAdvice = showAdvice;
	}
	
	public boolean isComboColumn(int col) {
		return isDestinationColumn(col) || isUnitTypesColumn(col) || isUnitsColumn(col);
	}

	public boolean isDestinationColumn(int col) {
		return col == 2;
	}

	public boolean isUnitTypesColumn(int col) {
		return col == (showAdvice ? 5 : 4);
	}

	public boolean isUnitsColumn(int col) {
		return col == (showAdvice ? 6 : 5);
	}

	public String[] getColumnNames() {
		return showAdvice ? columnNamesWithAdvice : columnNames;
	}
	
    public ReviewDataConfig.Row getRowObject(int i) {
    	return ((ReviewDataConfig)rowConfig).getRows().get(i);
    }   
    
    public Object getValueAt(int row, int col) {
    	ReviewDataConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.configKey;
    		case 1: return rowObj.valueKey;
    		case 2: return rowObj.recipeScope;
    		case 3: return rowObj.prompt;
    		case 4: return showAdvice ? rowObj.advice : rowObj.unitType;
    		case 5: return showAdvice ? rowObj.unitType :  rowObj.units;
    		case 6: return showAdvice ? rowObj.units : "";
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
    		case 4: 
    			if(showAdvice)
    				rowObj.advice = sValue; 
    			else {
        			rowObj.unitType = sValue; 
    				setValueAt(null, row, 5);
    			}
     			break;
    		case 5: 
    			if(showAdvice) {
    				rowObj.unitType = sValue; 
    				setValueAt(null, row, 6);
    			}
    			else
        			rowObj.units = sValue; 
    			break;
    		case 6: 
    			if(showAdvice)
    				rowObj.units = sValue; 
    			else
        			;//
    			break;
    	}
    }

}
