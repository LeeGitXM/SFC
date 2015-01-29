package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

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
public class S88RecipeOutputData extends S88RecipeIOData {

	public S88RecipeOutputData() {
		// TODO: VAL-TYPE
		properties.add(IlsProperty.WRITE_CONFIRM);
		properties.add(IlsProperty.WRITE_CONFIRMED);
		properties.add(IlsProperty.DOWNLOAD);
		properties.add(IlsProperty.DOWNLOAD_STATUS);
		properties.add(IlsProperty.TIMING);
		properties.add(IlsProperty.STEP_TIME);
		properties.add(IlsProperty.STEP_TIMESTAMP);
		properties.add(IlsProperty.MAX_TIMING);
	}
	
}