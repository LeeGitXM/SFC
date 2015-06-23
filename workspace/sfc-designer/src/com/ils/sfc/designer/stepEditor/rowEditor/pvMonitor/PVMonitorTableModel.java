package com.ils.sfc.designer.stepEditor.rowEditor.pvMonitor;

import java.text.ParseException;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.designer.EditorErrorHandler;
import com.ils.sfc.designer.stepEditor.EditorUtil;
import com.ils.sfc.designer.stepEditor.rowEditor.RowTableModel;

@SuppressWarnings("serial")
public class PVMonitorTableModel extends RowTableModel {
	private EditorErrorHandler errorHandler;
	public static final int ENABLED_COLUMN = 0;
	public static final int PV_KEY_COLUMN = 1;
	public static final int TARGET_TYPE_COLUMN = 2;
	public static final int TARGET_NAME_COLUMN = 3;
	public static final int STRATEGY_COLUMN = 4;
	public static final int LIMITS_COLUMN = 5;
	public static final int DOWNLOAD_COLUMN = 6;
	public static final int PERSISTENCE_COLUMN = 7;
	public static final int CONSISTENCY_COLUMN = 8;
	public static final int DEADTIME_COLUMN = 9;
	public static final int TOLERANCE_COLUMN = 10;
	public static final int TYPE_COLUMN = 11;
	public static final int STATUS_COLUMN = 12;
	
	private static final String[] columnNames = {
		"Enabled",
		"PV Key",
		"Target Type",
		"Target Name/Id/Value",
		"Strategy",
		"Limits",
		"Download",
		"Persistence",
		"Consistency",
		"Deadtime",
		"Tolerance",
		"Type",
		"Status"
	};
	
	public static boolean isBooleanColumn(int col) {
		return col == ENABLED_COLUMN;
	}
	
	public static boolean isComboColumn(int col) {
		return col == TARGET_TYPE_COLUMN ||
			col == STRATEGY_COLUMN ||
			col == LIMITS_COLUMN ||
			col == DOWNLOAD_COLUMN ||
			col == TYPE_COLUMN;
	}

	public static boolean isTextColumn(int col) {
		return col == PV_KEY_COLUMN ||
			col == TARGET_NAME_COLUMN;
	}

	public static boolean isDoubleColumn(int col) {
		return col == PERSISTENCE_COLUMN ||
			col == CONSISTENCY_COLUMN ||
			col == DEADTIME_COLUMN ||
			col == TOLERANCE_COLUMN;
	}

	public PVMonitorTableModel(EditorErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	public String[] getColumnNames() {
		return columnNames;
	}
	
    public PVMonitorConfig.Row getRowObject(int i) {
    	return ((PVMonitorConfig)rowConfig).getRows().get(i);
    }   

    public Object getValueAt(int row, int col) {
    	PVMonitorConfig.Row rowObj = getRowObject(row);
    	switch(col) {
    		case 0: return rowObj.enabled;
    		case 1: return rowObj.pvKey;
    		case 2: return rowObj.targetType;
    		case 3: return rowObj.targetNameIdOrValue;
    		case 4: return rowObj.strategy;
    		case 5: return rowObj.limits;
    		case 6: return rowObj.download;
    		case 7: return rowObj.persistence;
    		case 8: return rowObj.consistency;
    		case 9: return rowObj.deadTime;
    		case 10: return rowObj.tolerance;
       		case 11: return rowObj.toleranceType;
       		case 12: return rowObj.status;
    		default: return null;
    	}
    }

    public void setValueAt(Object value, int row, int col) {
    	PVMonitorConfig.Row rowObj = getRowObject(row);
     	try {
     		String sValue = EditorUtil.toString(value);
	    	switch(col) {
			case 0: rowObj.enabled = (Boolean)value; break;
			case 1: rowObj.pvKey = sValue; break;
			case 2: rowObj.targetType = sValue; break;
			case 3: rowObj.targetNameIdOrValue = IlsProperty.parseObjectValue(sValue, String.class); break;
			case 4: rowObj.strategy = sValue; break;
			case 5: rowObj.limits = sValue; break;
			case 6: rowObj.download = sValue; break;
			case 7: rowObj.persistence = IlsProperty.parseDouble(sValue); break;
			case 8: rowObj.consistency = IlsProperty.parseDouble(sValue); break;
			case 9: rowObj.deadTime = IlsProperty.parseDouble(sValue); break;
			case 10: rowObj.tolerance = IlsProperty.parseDouble(sValue); break;
	   		case 11: rowObj.toleranceType = sValue; break;
	    	}
	    	fireTableCellUpdated(row, col);
    	}
    	catch(ParseException e) {
			if(errorHandler != null) {
				errorHandler.handleError(e.getMessage());
			}
    	}
    }

	public static String[] getChoices(int col) {
		if(col == TARGET_TYPE_COLUMN) {
			return Constants.PV_TARGET_TYPE_CHOICES;					
		}
		else if(col == STRATEGY_COLUMN) {
			return Constants.PV_STRATEGY_CHOICES;								
		}
		else if(col == LIMITS_COLUMN) {
			return Constants.PV_LIMITS_CHOICES;
		}
		else if(col == DOWNLOAD_COLUMN) {
			return Constants.PV_DOWNLOAD_CHOICES;			
		}
		else if(col == TYPE_COLUMN) {
			return Constants.PV_TYPE_CHOICES;						
		}
		else { // shouldn't happen, but keep compiler happy
			return new String[0];
		}
	}

}
