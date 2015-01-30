package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-OUTPUT-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol RAMP-TIME,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 5.0),
  structure (
    ATTRIBUTE-NAME: the symbol UPDATE-FREQUENCY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 10.0))
 */
public class OutputRamp extends Output {

	public OutputRamp() {
		addProperty(IlsProperty.RAMP_TIME); // 5.0);
		addProperty(IlsProperty.UPDATE_FREQUENCY); // 10.0);
	}
}
