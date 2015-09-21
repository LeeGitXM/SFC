package com.ils.sfc.designer.stepEditor.rowEditor.reviewData;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig.Row;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;
import com.inductiveautomation.ignition.common.script.JythonExecException;

@SuppressWarnings("serial")
public class ReviewDataTableModel extends RowTableModel {
	public static final int VALUE_COLUMN = 1;
	public static final int UNITS_COLUMN = 4;
	private static final String[] columnNames = {"Config Key", "Value Key", "Destination", "Prompt", "Units"};
	private static final String[] columnNamesWithAdvice = {"Config Key", "Value Key", "Destination", "Prompt", "Units", "Advice"};
	private boolean showAdvice;
	
	public ReviewDataTableModel(boolean showAdvice) {
		this.showAdvice = showAdvice;
	}
	
	@Override
	protected boolean isComboColumn(int col) {
		return isDestinationColumn(col);
	}

	@Override
	protected boolean isTextColumn(int col) {
		return !isComboColumn(col);
	}
	
	@Override
	protected String[] getChoices(int row, int col) {
		String[] choices = null;
		if(isDestinationColumn(col)) { // recipe scope
			choices = Constants.RECIPE_LOCATION_CHOICES;
		}
		return choices;
	}
		
	private boolean isDestinationColumn(int col) {
		return col == 2;
	}

	public boolean isUnitsColumn(int col) {
		return col == UNITS_COLUMN;
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
    		case 4: return rowObj.units;
    		case 5: return showAdvice ? rowObj.advice : "";
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
    		case 4: rowObj.units = sValue; break;
    		case 5: 
    			if(showAdvice) {
    				rowObj.advice = sValue; 
     			}
    			break;
    	}
    }

}
