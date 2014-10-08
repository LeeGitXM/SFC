package com.ils.sfc.designer.editor;

import java.text.ParseException;
import java.util.List;

import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;

public class PropertyRow {
	private PropertyValue<?> propertyValue;
	private PropertyValue<?> unitPropertyValue; // may be null
	private List<?> choices;
	
	public PropertyRow(PropertyValue<?> propertyValue, PropertyValue<?> unitPropertyValue) {
		this.propertyValue = propertyValue;
		this.unitPropertyValue = unitPropertyValue;
	}

	public String getName() {
		return propertyValue.getProperty().getName();
	}

	public String getUnitName() {
		return unitPropertyValue != null ? unitPropertyValue.getValue().toString() : "";
	}
	
	public Object getValue() {
		return propertyValue.getValue();
	}
	
	public String getCategory() {
		return null;
	}
	
	public boolean isCategory() {
		return false;
	}

	public List<?> getChoices() {
		return choices;
	}

	public void setChoices(List<?> choices) {
		this.choices = choices;
	}

	public Property<?> getProperty() {
		return propertyValue.getProperty();
	}

	public PropertyValue<?> getPropertyValue() {
		return propertyValue;
	}

	public PropertyValue<?> getUnitPropertyValue() {
		return unitPropertyValue;
	}

	/** Regardless of underlying type, set the value from a string representation. */
	public void setValueFormatted(String stringValue) throws ParseException {
		Object value = IlsSfcCommonUtils.parseProperty(getProperty(), stringValue);
		setValue(value);
	}
	
	public void setValue(Object value) {
		propertyValue = new PropertyValue(getProperty(), value);		
	}
}
