package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.util.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol UNITS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "")) 
 */
public abstract class S88RecipeDataWithUnits extends S88RecipeData {

	public S88RecipeDataWithUnits() {
		properties.add(IlsProperty.UNITS);
	}
}
