package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-MATRIX,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-MATRIX))
 */
public class S88RecipeMatrixData extends S88RecipeDataWithUnits {
	
	public S88RecipeMatrixData() {
		addProperty(VAL, String.class, "[]");
	}
}
