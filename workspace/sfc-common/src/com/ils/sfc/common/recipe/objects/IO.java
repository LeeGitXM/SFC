package com.ils.sfc.common.recipe.objects;

import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol TAG,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol SYMBOL,
    ATTRIBUTE-INITIAL-VALUE: the symbol G2),
  structure (
    ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol VALUE,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol ERROR-CODE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol VALUE,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (ATTRIBUTE-NAME: the symbol ERROR-TEXT,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol PV-MONITOR-STATUS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol PV-MONITOR-ACTIVE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TRUTH-VALUE,
    ATTRIBUTE-INITIAL-VALUE: false),
  structure (ATTRIBUTE-NAME: the symbol PV-VALUE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol VALUE,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (ATTRIBUTE-NAME: the symbol TARGET-VALUE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol VALUE,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (ATTRIBUTE-NAME: the symbol _GUI-UNITS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol _GUI-LABEL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""))
 */
public abstract class IO extends DataWithUnits {

	public IO() {
		addProperty(IlsProperty.TAG_PATH);
		addProperty(IlsProperty.VALUE);
		addProperty(IlsProperty.VALUE_TYPE);
		addProperty(IlsProperty.ERROR_CODE);
		addProperty(IlsProperty.ERROR_TEXT);
		addProperty(IlsProperty.PV_MONITOR_STATUS);
		addProperty(IlsProperty.SETPOINT_STATUS);
		addProperty(IlsProperty.PV_MONITOR_ACTIVE);
		addProperty(IlsProperty.PV_VALUE);
		addProperty(IlsProperty.TARGET_VALUE);
		addProperty(IlsProperty.UNITS);
		addProperty(IlsProperty.LABEL);
	}
	
	public void setValuesFromDatabase(PyDictionary pyDict){
		super.setValuesFromDatabase(pyDict);
		log.info("Setting values from dictionary in IO");
		
		this.setValue(IlsProperty.TAG_PATH, (String) pyDict.get("Tag"));
		this.setValue(IlsProperty.VALUE, (String) pyDict.get(""));
		this.setValue(IlsProperty.VALUE_TYPE, (String) pyDict.get("ValueType"));
//		this.setValue(IlsProperty.ERROR_CODE, (String) pyDict.get("ErrorCode"));	// read-only?
		this.setValue(IlsProperty.ERROR_TEXT, (String) pyDict.get("ErrorText"));
		this.setValue(IlsProperty.PV_MONITOR_STATUS, (String) pyDict.get("PVMonitorStatus"));
//		this.setValue(IlsProperty.SETPOINT_STATUS, (String) pyDict.get(""));	// I don't see this attribute in DB
		this.setValue(IlsProperty.PV_MONITOR_ACTIVE, (Boolean) pyDict.get("PVMonitorActive"));
		this.setValue(IlsProperty.PV_VALUE, (Double) pyDict.get("PVValue"));
		this.setValue(IlsProperty.TARGET_VALUE, (Double) pyDict.get("TargetValue"));
		this.setValue(IlsProperty.UNITS, (String) pyDict.get("Units"));
		this.setValue(IlsProperty.LABEL, (String) pyDict.get("Label"));
	}
}
