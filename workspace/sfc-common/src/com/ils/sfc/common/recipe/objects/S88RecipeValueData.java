package com.ils.sfc.common.recipe.objects;

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
	public static final String TYPE = "type";
	public static final String CATEGORY = "category";
	public static final String HIGH_LIMIT = "highLimit";
	public static final String LOW_LIMIT = "lowLimit";
	
	public S88RecipeValueData() {
		addProperty(TYPE, String.class, "Quantity"); // other possibilities: "float", ?
		addProperty(CATEGORY, String.class, "Operator Input");  // other possibilities: "Simple Constant"
		addProperty(VAL, Double.class, 0.);
		addProperty(HIGH_LIMIT, Double.class, 0.);
		addProperty(LOW_LIMIT, Double.class, 0.);
	}
}
