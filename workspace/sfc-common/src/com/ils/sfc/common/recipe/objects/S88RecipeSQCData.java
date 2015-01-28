package com.ils.sfc.common.recipe.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (
	ATTRIBUTE-NAME: the symbol LOW_LIMIT,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol HIGH_LIMIT,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol TARGET,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0))
 */
public class S88RecipeSQCData extends S88RecipeDataWithUnits {
	public static final String LOW_LIMIT = "lowLimit";
	public static final String HIGH_LIMIT = "highLimit";
	public static final String TARGET = "target";

	public S88RecipeSQCData() {
		addProperty(LOW_LIMIT, Double.class, 0.);
		addProperty(HIGH_LIMIT, Double.class, 0.);
		addProperty(TARGET, Double.class, 0.);
	}
}
