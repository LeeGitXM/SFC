package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (structure (
	ATTRIBUTE-NAME: the symbol LOW_LIMIT,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol HIGH_LIMIT,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0),
  structure (
    ATTRIBUTE-NAME: the symbol TARGET,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol QUANTITY,
    ATTRIBUTE-INITIAL-VALUE: 0.0))
 */
public class SQC extends DataWithUnits {

	public SQC() {
		addProperty(IlsProperty.LOW_LIMIT);
		addProperty(IlsProperty.HIGH_LIMIT);
		addProperty(IlsProperty.TARGET_VALUE);
	}
	
	@Override
	public String getLabel() {
		StringBuffer buf = new StringBuffer();
		addLabelValue(IlsProperty.KEY, buf);		
		addLabelValue(IlsProperty.CLASS, buf);	
		buf.append("Target: ");
		addLabelValue(IlsProperty.TARGET_VALUE, buf);		
		addLabelValue(IlsProperty.UNITS, buf);
		buf.append("(HL: ");
		addLabelValue(IlsProperty.HIGH_LIMIT, buf);		
		buf.append("LL: ");
		addLabelValue(IlsProperty.LOW_LIMIT, buf);		
		buf.append(")");
		return buf.toString();
	}
}
