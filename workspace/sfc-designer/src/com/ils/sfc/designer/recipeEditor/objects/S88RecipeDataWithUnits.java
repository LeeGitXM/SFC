package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol UNITS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "")) 
 */
public abstract class S88RecipeDataWithUnits extends S88RecipeData {
	public static final String UNITS = "units";

	public S88RecipeDataWithUnits() {
		addProperty(UNITS, String.class, "");
	}
}
