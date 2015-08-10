package com.ils.sfc.designer.stepEditor.rowEditor.manualData;

import java.text.ParseException;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.ils.sfc.designer.EditorErrorHandler;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class ManualDataTableModel extends RowTableModel {
private EditorErrorHandler errorHandler;

private static final String[] columnNames = {"Key", "Destination", "Prompt", "Units", "Default Value", "Low Limit", "High Limit"};
	
	public ManualDataTableModel(EditorErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
	public ManualDataEntryConfig.Row getRowObject(int i) {
		return ((ManualDataEntryConfig)rowConfig).getRows().get(i);
	}   
	
	public Object getValueAt(int row, int col) {
		ManualDataEntryConfig.Row rowObj = getRowObject(row);
		switch(col) {
			case 0: return rowObj.key;
			case 1: return rowObj.destination;
			case 2: return rowObj.prompt;
			case 3: return rowObj.units;
			case 4: return rowObj.defaultValue;
			case 5: return rowObj.lowLimit;
			case 6: return rowObj.highLimit;
			default: return null;
	 	}
	}
	
	public void setValueAt(Object value, int row, int col) {
		ManualDataEntryConfig.Row rowObj = getRowObject(row);
		try {
			String svalue = (String) value;
			switch(col) {
				case 0: rowObj.key = svalue;break;
				case 1: rowObj.destination = svalue;break;
				case 2: rowObj.prompt = svalue;break;
				case 3: rowObj.units = svalue;break;
				case 4: rowObj.defaultValue = IlsProperty.parseObjectValue(svalue, null);break;
				case 5: rowObj.lowLimit = IlsProperty.parseDouble(svalue);break;
				case 6: rowObj.highLimit = IlsProperty.parseDouble(svalue);break;
			}
	    	fireTableCellUpdated(row, col);
		}
		catch(ParseException e) {
			if(errorHandler != null) {
				errorHandler.handleError(e.getMessage());
			}
		}
	}

	protected boolean isComboColumn(int col) {
		return col == 1;
	}

	protected String[] getChoices(int row, int col) {
		if(col == 1) {
			return Constants.S88_LEVEL_CHOICES;
		}
		else {
			return null;
		}
	}
	
	protected boolean isTextColumn(int col) {
		return col != 1;
	}
}
