package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
A class that lumps together all the sequence/list types in G2
 */
public class RecipeList extends DataWithUnits {
	
	public RecipeList() {
		addProperty(IlsProperty.VALUE);
	}
}
