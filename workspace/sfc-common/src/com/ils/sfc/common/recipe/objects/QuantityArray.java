package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-ARRAY-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-PERMANENT-QUANTITY-ARRAY,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-PERMANENT-QUANTITY-ARRAY))
 */
public class QuantityArray extends Array {

	public QuantityArray() {
		addProperty(IlsProperty.STRING_VALUE);
	}
}
