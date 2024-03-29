package com.ils.sfc.designer.stepEditor.rowEditor.reviewFlows;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class ReviewFlowsTableModel extends RowTableModel {
	public static final int DESTINATION_COLUMN = 4;
	public static final int UNITS_COLUMN = 6;
	private static final String[] columnNames = {"Config Key", "Flow 1", "Flow 2", "Total Flow", "Destination", "Prompt", "Units", "Advice"};

	@Override
	protected boolean isComboColumn(int col) {
		return col == DESTINATION_COLUMN;
	}

	@Override
	protected boolean isRecipeKeyColumn(int col) {
		return col < DESTINATION_COLUMN;
	}

	@Override
	protected boolean isTextColumn(int col) {
		return col != DESTINATION_COLUMN;
	}
	
	@Override
	protected String[] getChoices(int row, int col) {
		String[] choices = null;
		if(col == DESTINATION_COLUMN) { // recipe scope
			choices = Constants.RECIPE_PLUS_VALUE_LOCATION_CHOICES;
		}
		return choices;
	}		

	public String[] getColumnNames() {
		return columnNames;
	}
	
    public ReviewFlowsConfig.Row getRowObject(int i) {
    	return ((ReviewFlowsConfig)rowConfig).getRows().get(i);
    }   
    
    public Object getValueAt(int row, int col) {
    	ReviewFlowsConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.configKey;
    		case 1: return rowObj.flow1Key;
    		case 2: return rowObj.flow2Key;
    		case 3: return rowObj.flow3Key;
    		case 4: return rowObj.destination;
    		case 5: return rowObj.prompt;
       		case 6: return rowObj.units;
       		case 7: return rowObj.advice;
       	    default: return null;
    	}
    }

    public void setValueAt(Object value, int row, int col) {
    	ReviewFlowsConfig.Row rowObj = getRowObject(row);
    	String sValue = (String)value;
    	switch(col) {
    		case 0: {
    			String[] scopeKey = IlsProperty.parseRecipeScopeValue(sValue);
    			rowObj.configKey = scopeKey[1]; 
    			if(scopeKey[0] != null) {
    				rowObj.destination = scopeKey[0];
    			}
    			break;
    		}
    		case 1: {
       			String[] scopeKey = IlsProperty.parseRecipeScopeValue(sValue);
    			rowObj.flow1Key = scopeKey[1]; 
    			if(scopeKey[0] != null) {
    				rowObj.destination = scopeKey[0];
    			}
    			break;
    		}
    		case 2: {
       			String[] scopeKey = IlsProperty.parseRecipeScopeValue(sValue);
    			rowObj.flow2Key = scopeKey[1]; 
    			if(scopeKey[0] != null) {
    				rowObj.destination = scopeKey[0];
    			}
    			break;
    		}
    		case 3: {
       			String[] scopeKey = IlsProperty.parseRecipeScopeValue(sValue);
    			rowObj.flow3Key = scopeKey[1]; 
    			if(scopeKey[0] != null) {
    				rowObj.destination = scopeKey[0];
    			}
    			break;
    		}
    		case 4: 
    			rowObj.destination = sValue; 
    			break;
    		case 5: rowObj.prompt = sValue; break;
    		case 6: rowObj.units = sValue; break;
    		case 7: rowObj.advice = sValue; break;
     	}
    }

}
