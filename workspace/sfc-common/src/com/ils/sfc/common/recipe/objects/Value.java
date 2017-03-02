package com.ils.sfc.common.recipe.objects;

import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA-WITH-UNITS)
attributes:sequence (
  structure (
    ATTRIBUTE-NAME: the symbol TYPE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "Quantity"),
  structure (
    ATTRIBUTE-NAME: the symbol CATEGORY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "Operator Input"),
  structure (ATTRIBUTE-NAME: the symbol VAL),
  structure (ATTRIBUTE-NAME: the symbol HIGH-LIMIT),
  structure (ATTRIBUTE-NAME: the symbol LOW-LIMIT))
 */
public class Value extends DataWithUnits {
	
	public Value() {
		addProperty(IlsProperty.TYPE); // , String.class, "Quantity"); // other possibilities: "float", ?
		addProperty(IlsProperty.CATEGORY); // "Operator Input");  // other possibilities: "Simple Constant"
		addProperty(IlsProperty.VALUE);
		addProperty(IlsProperty.VALUE_TYPE);
		addProperty(IlsProperty.HIGH_LIMIT);
		addProperty(IlsProperty.LOW_LIMIT);
	};
	
	public String toString() {
		return properties.toString();
	}
	
	public void setValuesFromDatabase(PyDictionary pyDict){
		super.setValuesFromDatabase(pyDict);
		log.info("Setting values from dictionary in Value");
		this.setValue(IlsProperty.VALUE_TYPE, (String) pyDict.get("ValueType"));
		this.setValue(IlsProperty.VALUE, (String) pyDict.get("Value"));
	}
}
