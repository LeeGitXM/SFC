package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

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

	public S88RecipeSQCData() {
		properties.add(IlsProperty.LOW_LIMIT);
		properties.add(IlsProperty.HIGH_LIMIT);
		properties.add(IlsProperty.TARGET_VALUE);
	}
}
