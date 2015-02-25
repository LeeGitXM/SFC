package com.ils.sfc.common.recipe.objects;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol VAL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol STRUCTURE,
    ATTRIBUTE-INITIAL-VALUE: structure ()))
 */
public class Structure extends Data {

	public Structure() {
		// value is a pseudo-property, as coming from G2 it contains
		// a serialized form of all the dynamic property/values
		addProperty(IlsProperty.JSON_OBJECT);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addDynamicProperty(String name, Class<?> aClass, Object defaultValue) {
		BasicProperty property = new BasicProperty(name, aClass, defaultValue);
		properties.set(property, defaultValue);
	}

	public void removeDynamicProperty(Property<?> property) {
		properties.remove(property);
	}
}
