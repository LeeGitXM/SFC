package com.ils.sfc.designer.propertyEditor;

import java.text.ParseException;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;

public class PropertyRow {
	private PropertyValue<?> propertyValue;
	private Object[] choices;
	private String displayLabel;
	
	public PropertyRow(PropertyValue<?> propertyValue) {
		this.propertyValue = propertyValue;
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
	
	public boolean isEditableString() {
		return getProperty().getType() == String.class && getChoices() == null;
	}

	public Object getDefaultValue() {
		return getProperty() instanceof BasicProperty ? ((BasicProperty<?>)getProperty()).getDefaultValue() : null;
	}
	
	public Object getValue() {
		return propertyValue.getValue() != null ? propertyValue.getValue() : getDefaultValue();
	}
	
	public Object[] getChoices() {
		return choices;
	}

	public String getCategory() {
		return null;
	}
	
	public boolean isCategory() {
		return false;
	}

	public void setChoices(Object[] choices) {
		this.choices = choices;
	}

	public Property<?> getProperty() {
		return propertyValue.getProperty();
	}

	public PropertyValue<?> getPropertyValue() {
		return propertyValue;
	}

	/** Regardless of underlying type, set the value from a string representation. 
	 *  throws NumberFormatException for bad numbers */
	public void setValueFormatted(String stringValue) {
		Object value = IlsSfcCommonUtils.parseProperty(getProperty(), stringValue);
		setValue(value);
	}

	@SuppressWarnings("rawtypes")
	public void setValue(Object value) {
		propertyValue = new PropertyValue(getProperty(), value);		
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}

}
