package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
	ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol SEQUENCE,
    ATTRIBUTE-INITIAL-VALUE: sequence ()))
 */
public class Sequence extends Data {
	
	public Sequence() {
		addProperty(IlsProperty.STRING_VALUE);
	}
	
}
