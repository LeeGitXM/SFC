package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (
  structure (
    ATTRIBUTE-NAME: the symbol TYPE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "Quantity"),
  structure (
    ATTRIBUTE-NAME: the symbol CATEGORY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "Operator Input"),
  structure (ATTRIBUTE-NAME: the symbol VAL),
  structure (ATTRIBUTE-NAME: the symbol HIGH-LIMIT),
  structure (ATTRIBUTE-NAME: the symbol LOW-LIMIT))
 */
public class Value extends DataWithUnits {
	
	public Value() {
		addProperty(IlsProperty.TYPE); // , String.class, "Quantity"); // other possibilities: "float", ?
		addProperty(IlsProperty.CATEGORY); // "Operator Input");  // other possibilities: "Simple Constant"
		addProperty(IlsProperty.NULLABLE_VALUE);
		addProperty(IlsProperty.HIGH_LIMIT);
		addProperty(IlsProperty.LOW_LIMIT);
	}
}
