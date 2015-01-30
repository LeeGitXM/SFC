package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-LIST-DATA,
  the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-PERMANENT-QUANTITY-LIST,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-PERMANENT-QUANTITY-LIST))
 */
public class QuantityList extends DataWithUnits {
	
	public QuantityList() {
		addProperty(IlsProperty.STRING_VALUE);
	}
}
