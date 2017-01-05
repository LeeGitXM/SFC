package com.ils.sfc.common.recipe.objects;

import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-IO-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol VAL-TYPE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol SYMBOL,
    ATTRIBUTE-INITIAL-VALUE: the symbol VALUE,
    ATTRIBUTE-RANGE: sequence (
      the symbol VALUE,
      the symbol MODE,
      the symbol OUTPUT,
      the symbol SETPOINT,
      the symbol RAMP-SETPOINT,
      the symbol RAMP-OUTPUT)),
  structure (
    ATTRIBUTE-NAME: the symbol WRITE-CONFIRM,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TRUTH-VALUE,
    ATTRIBUTE-INITIAL-VALUE: true),
  structure (
    ATTRIBUTE-NAME: the symbol WRITE-CONFIRMED,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TRUTH-VALUE,
    ATTRIBUTE-INITIAL-VALUE: true),
  structure (
    ATTRIBUTE-NAME: the symbol DOWNLOAD,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TRUTH-VALUE,
    ATTRIBUTE-INITIAL-VALUE: true),
  structure (
    ATTRIBUTE-NAME: the symbol DOWNLOAD-STATUS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (
    ATTRIBUTE-NAME: the symbol TIMING,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol STEP-TIME,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol STEP-TIMESTAMP,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (
    ATTRIBUTE-NAME: the symbol MAX-TIMING,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0))
 */
public class Output extends IO {

	public Output() {
		addProperty(IlsProperty.OUTPUT_TYPE);
		addProperty(IlsProperty.WRITE_CONFIRM);
		addProperty(IlsProperty.WRITE_CONFIRMED);	// dynamic
		addProperty(IlsProperty.DOWNLOAD);
		addProperty(IlsProperty.DOWNLOAD_STATUS);   // dynamic
		addProperty(IlsProperty.TIMING);
		addProperty(IlsProperty.TYPE);              // clc - VALUE_TYPE?
		addProperty(IlsProperty.STEP_TIME);			// dynamic
		addProperty(IlsProperty.STEP_TIMESTAMP);    // dynamic
		addProperty(IlsProperty.MAX_TIMING);
	}
	
	public String toString() {
		return properties.toString();
	}
	
	public void setValuesFromDatabase(PyDictionary pyDict){
		super.setValuesFromDatabase(pyDict);
		log.info("Setting values from dictionary in Output");
		
		this.setValue(IlsProperty.OUTPUT_TYPE, (String) pyDict.get("OutputType"));
		this.setValue(IlsProperty.WRITE_CONFIRM, (Boolean) pyDict.get("WriteConfirm"));
		this.setValue(IlsProperty.WRITE_CONFIRMED, (Boolean) pyDict.get("WriteConfirmed"));
		this.setValue(IlsProperty.DOWNLOAD, (Boolean) pyDict.get("Download"));
		this.setValue(IlsProperty.DOWNLOAD_STATUS, (String) pyDict.get("DownloadStatus"));
		this.setValue(IlsProperty.TIMING, (Double) pyDict.get("Timing"));
		this.setValue(IlsProperty.TYPE, (String) pyDict.get("ValueType"));
		this.setValue(IlsProperty.STEP_TIME, (java.util.Date) pyDict.get("StepTime")); // We store the step time as a datetime in the DB, it comes through the pydictionary as a text
//		this.setValue(IlsProperty.STEP_TIMESTAMP, (String) pyDict.get("StepTime"));
		this.setValue(IlsProperty.MAX_TIMING, (Double) pyDict.get("MaxTiming"));
	}
}