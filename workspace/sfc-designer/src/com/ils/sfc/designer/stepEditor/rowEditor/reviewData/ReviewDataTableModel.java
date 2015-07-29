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
	private static final String[] columnNames = {"Config Key", "Value Key", "Destination", "Prompt", "Unit Type", "Units"};
	private static final String[] columnNamesWithAdvice = {"Config Key", "Value Key", "Destination", "Prompt", "Advice", "Unit Type", "Units"};
	private boolean showAdvice;
	
	public ReviewDataTableModel(boolean showAdvice) {
		this.showAdvice = showAdvice;
	}
	
	@Override
	protected boolean isComboColumn(int col) {
		return isDestinationColumn(col) || isUnitTypesColumn(col) || isUnitsColumn(col);
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
		else if(isUnitTypesColumn(col)) { // unit types
			try {
				choices =  PythonCall.toArray(PythonCall.GET_UNIT_TYPES.exec());
			} catch (JythonExecException e) {
				return null;
			}
		}
		else {//  units
			try {
				int unitTypeCol = (showAdvice ? 5 : 4);
				String unitType = (String)getValueAt(row, unitTypeCol);
				if(!IlsSfcCommonUtils.isEmpty(unitType)) {
					choices =  PythonCall.toArray(PythonCall.GET_UNITS_OF_TYPE.exec(unitType));
				}
				else {
					// no unit type has been chosen
					choices =  new String[0];						
				}
			} 
			catch (JythonExecException e) {
				e.printStackTrace();
			}
		}
		return choices;
	}
		
	private boolean isDestinationColumn(int col) {
		return col == 2;
	}

	private boolean isUnitTypesColumn(int col) {
		return col == (showAdvice ? 5 : 4);
	}

	private boolean isUnitsColumn(int col) {
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
