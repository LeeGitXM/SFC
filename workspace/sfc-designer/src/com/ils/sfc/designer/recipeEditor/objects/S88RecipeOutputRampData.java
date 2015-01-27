package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-OUTPUT-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol RAMP-TIME,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 5.0),
  structure (
    ATTRIBUTE-NAME: the symbol UPDATE-FREQUENCY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 10.0))
 */
public class S88RecipeOutputRampData extends S88RecipeOutputData {
	public static final String RAMP_TIME = "rampTime";
	public static final String UPDATE_FREQUENCY = "updateFrequency";

	public S88RecipeOutputRampData() {
		addProperty(RAMP_TIME, Double.class, 5.0);
		addProperty(UPDATE_FREQUENCY, Double.class, 10.0);
	}
}
