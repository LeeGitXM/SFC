package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-ARRAY-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-PERMANENT-VALUE-ARRAY,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-PERMANENT-VALUE-ARRAY))
 */
public class S88RecipeValueArrayData extends S88RecipeArrayData {
	
	public S88RecipeValueArrayData() {
		addProperty(VAL, String.class, "[]");
	}
}
