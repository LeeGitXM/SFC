package com.ils.sfc.common.recipe.objects;

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

	public static final String WRITE_CONFIRM = "writeConfirm";
	public static final String WRITE_CONFIRMED = "writeConfirmed";
	public static final String DOWNLOAD = "download";
	public static final String DOWNLOAD_STATUS = "downloadStatus";		
	public static final String TIMING = "timing";
	public static final String STEP_TIME = "stepTime";
	public static final String STEP_TIMESTAMP = "stepTimestamp";
	public static final String MAX_TIMING = "maxTiming";

	public S88RecipeOutputData() {
		// TODO: VAL-TYPE
		addProperty(WRITE_CONFIRM, Boolean.class, Boolean.TRUE);
		addProperty(WRITE_CONFIRMED, Boolean.class, Boolean.TRUE);
		addProperty(DOWNLOAD, Boolean.class, Boolean.TRUE);
		addProperty(DOWNLOAD_STATUS, String.class, "");
		addProperty(TIMING, Double.class, 0.);
		addProperty(STEP_TIME, Double.class, 0.);
		addProperty(STEP_TIMESTAMP, String.class, "");
		addProperty(MAX_TIMING, Double.class, 0.);
	}
	
}