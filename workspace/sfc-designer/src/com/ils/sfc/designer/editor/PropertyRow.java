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
	private List<?> unitChoices;
	
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

	public List<?> getUnitChoices() {
		return unitChoices;
	}

	public void setUnitChoices(List<?> unitChoices) {
		this.unitChoices = unitChoices;
	}

	public Property<?> getUnitProperty() {
		return unitPropertyValue.getProperty();
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

	/** Regardless of underlying type, set the value from a string representation. */
	public void setUnitValueFormatted(String stringValue) throws ParseException {
		Object value = IlsSfcCommonUtils.parseProperty(getUnitProperty(), stringValue);
		setUnitValue(value);
	}

	public void setValue(Object value) {
		propertyValue = new PropertyValue(getProperty(), value);		
	}
	
	public void setUnitValue(Object value) {
		unitPropertyValue = new PropertyValue(getUnitProperty(), value);		
	}

}
