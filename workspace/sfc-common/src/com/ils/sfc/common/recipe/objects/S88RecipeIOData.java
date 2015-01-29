package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

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

	public S88RecipeIOData() {
		properties.add(IlsProperty.TAG);
		properties.add(IlsProperty.VALUE);
		properties.add(IlsProperty.ERROR_CODE);
		properties.add(IlsProperty.ERROR_TEXT);
		properties.add(IlsProperty.PV_MONITOR_STATUS);
		properties.add(IlsProperty.PV_MONITOR_ACTIVE);
		properties.add(IlsProperty.PV_VALUE);
		properties.add(IlsProperty.TARGET_VALUE);
		properties.add(IlsProperty.GUI_UNITS);
		properties.add(IlsProperty.GUI_LABEL);
	}
}
