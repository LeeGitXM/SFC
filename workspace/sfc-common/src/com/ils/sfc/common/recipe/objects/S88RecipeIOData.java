package com.ils.sfc.common.recipe.objects;

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
public abstract class S88RecipeIOData extends S88RecipeDataWithUnits {
	public static final String TAG = "tag";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_TEXT = "errorText";
	public static final String PV_MONITOR_STATUS = "pvMonitorStatus";
	public static final String PV_MONITOR_ACTIVE = "pvMonitorActive";
	public static final String PV_VALUE = "pvValue";
	public static final String TARGET_VALUE = "targetValue";
	public static final String GUI_UNITS = "guiUnits";
	public static final String GUI_LABEL = "guiLabel";

	public S88RecipeIOData() {
		addProperty(TAG, String.class, "G2");
		addProperty(VAL, Double.class, 0.);
		addProperty(ERROR_CODE, Double.class, 0.);
		addProperty(ERROR_TEXT, String.class, "");
		addProperty(PV_MONITOR_STATUS, String.class, "");
		addProperty(PV_MONITOR_ACTIVE, String.class, "");
		addProperty(PV_VALUE, Double.class, 0.);
		addProperty(TARGET_VALUE, Double.class, 0.);
		addProperty(GUI_UNITS, String.class, "");
		addProperty(GUI_LABEL, String.class, "");
	}
}
