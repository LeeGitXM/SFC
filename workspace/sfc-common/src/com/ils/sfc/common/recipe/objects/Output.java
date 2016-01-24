package com.ils.sfc.common.recipe.objects;

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
		addProperty(IlsProperty.TYPE);
		addProperty(IlsProperty.STEP_TIME);			// dynamic
		addProperty(IlsProperty.STEP_TIMESTAMP);    // dynamic
		addProperty(IlsProperty.MAX_TIMING);
	}
	
}