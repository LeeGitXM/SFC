package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.List;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol RECIPE-DATA-V2,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-RECIPE-DATA-LIST,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-RECIPE-DATA-LIST))
 */
public class Group extends Data {
	private List<Data> children = new ArrayList<Data>();
	
	public Group() {
		// in G2, this has a S88-RECIPE-DATA-LIST
	}
	
	public List<Data> getChildren() {
		return children;
	}
}
