package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

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
public class S88RecipeValueData extends S88RecipeDataWithUnits {
	
	public S88RecipeValueData() {
		properties.add(IlsProperty.TYPE); // , String.class, "Quantity"); // other possibilities: "float", ?
		properties.add(IlsProperty.CATEGORY); // "Operator Input");  // other possibilities: "Simple Constant"
		properties.add(IlsProperty.VALUE);
		properties.add(IlsProperty.HIGH_LIMIT);
		properties.add(IlsProperty.LOW_LIMIT);
	}
}
