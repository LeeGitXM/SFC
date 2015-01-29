package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

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

	public S88RecipeOutputRampData() {
		properties.add(IlsProperty.RAMP_TIME); // 5.0);
		properties.add(IlsProperty.UPDATE_FREQUENCY); // 10.0);
	}
}
