package com.ils.sfc.designer.recipeEditor.objects;

/**
superiorClass: sequence (the symbol S88-RECIPE-LIST-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-PERMANENT-TEXT-LIST,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-PERMANENT-TEXT-LIST))
 */
public class S88RecipeTextListData extends S88RecipeListData {

		public S88RecipeTextListData() {
			addProperty(VAL, String.class, "[]");
		}
}
