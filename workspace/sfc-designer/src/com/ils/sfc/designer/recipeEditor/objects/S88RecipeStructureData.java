package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol STRUCTURE,
    ATTRIBUTE-INITIAL-VALUE: structure ()))
 */
public class S88RecipeStructureData extends S88RecipeData {

	public S88RecipeStructureData() {
		addProperty(VAL, String.class, "");
	}
}
