package com.ils.sfc.common.recipe.objects;

import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol UNITS,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: "")) 
 */
public abstract class DataWithUnits extends Data {

	public DataWithUnits() {
		addProperty(IlsProperty.UNITS);
	}
	
	public void setValuesFromDatabase(PyDictionary pyDict){
		super.setValuesFromDatabase(pyDict);
		log.info("Setting values from dictionary in DataWithUnits");
		this.setValue(IlsProperty.UNITS, (String) pyDict.get("Units"));
	}
}
