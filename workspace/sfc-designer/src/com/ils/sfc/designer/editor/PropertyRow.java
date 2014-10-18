package com.ils.sfc.designer.editor;

import java.text.ParseException;
import java.util.List;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.util.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;

public class PropertyRow {
	private PropertyValue<?> propertyValue;
	private PropertyValue<?> unitPropertyValue; // may be null
	private Object[] unitChoices;
	private String displayLabel;
	
	public PropertyRow(PropertyValue<?> propertyValue, PropertyValue<?> unitPropertyValue) {
		this.propertyValue = propertyValue;
		this.unitPropertyValue = unitPropertyValue;
		displayLabel = createDisplayLabel();
	}

	/** Create a user-friendly label for this property */
	private String createDisplayLabel() {
		// break up the camel-case, replacing caps by spaces
		StringBuilder buf = new StringBuilder();
		String camelCaseName = getName();
		for(int i = 0; i < camelCaseName.length(); i++) {
			char c = camelCaseName.charAt(i);
			if(Character.isUpperCase(c)) {
				buf.append(' ');
			}
			buf.append(Character.toLowerCase(c));
		}
		return buf.toString();
	}

	public String getName() {
		return propertyValue.getProperty().getName();
	}

	public String getUnitName() {
		return unitPropertyValue != null ? unitPropertyValue.getValue().toString() : "";
	}
	
	public Object getDefaultValue() {
		return getProperty() instanceof BasicProperty ? ((BasicProperty<?>)getProperty()).getDefaultValue() : null;
	}
	
	public Object getValue() {
		return propertyValue.getValue() != null ? propertyValue.getValue() : getDefaultValue();
	}
	
	public String getCategory() {
		return null;
	}
	
	public boolean isCategory() {
		return false;
	}

	public Object[] getChoices() {
		return getProperty() instanceof IlsProperty ? 
			((IlsProperty<?>)getProperty()).getChoices() : null;
	}

	public Object[] getUnitChoices() {
		return unitChoices;
	}

	public void setUnitChoices(Object[] unitChoices) {
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

	public String getDisplayLabel() {
		return displayLabel;
	}

}
