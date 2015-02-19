package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-MATRIX,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-MATRIX))
 */
public class Matrix extends DataWithUnits {
	
	public Matrix() {
		addProperty(IlsProperty.VALUE);
	}
}
