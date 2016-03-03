package com.ils.sfc.designer.propertyEditor;

import java.text.ParseException;
import java.util.Date;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class PropertyRow {
	private static final LoggerEx logger = LogUtil.getLogger(PropertyValue.class.getName());
	private PropertyValue<?> propertyValue;
	private String displayLabel;
	private String valueType; // for Object-valued properties, a hint as to the value type
	
	public PropertyRow(PropertyValue<?> propertyValue) {
		this.propertyValue = propertyValue;
		displayLabel = createDisplayLabel();
	}

	/** Create a user-friendly label for this property */
	private String createDisplayLabel() {
		if(propertyValue.getProperty() instanceof BasicProperty) {
			BasicProperty<?> prop = (BasicProperty<?>)propertyValue.getProperty();
			return IlsProperty.getLabel(prop);
		}
		else {
			return IlsProperty.labelize(propertyValue.getProperty().getName());
		}
	}

	public String getName() {
		return propertyValue.getProperty().getName();
	}
	
	public Object[] getChoices() {
		if(propertyValue.getProperty().equals(IlsProperty.MESSAGE_QUEUE)) {
			return getChoicesFromPythonCall(PythonCall.GET_QUEUE_NAMES);
		}
		else if(
			propertyValue.getProperty().equals(IlsProperty.ARRAY_KEY) ||
			propertyValue.getProperty().equals(IlsProperty.ROW_KEY) ||
			propertyValue.getProperty().equals(IlsProperty.COLUMN_KEY) ) {
			return getChoicesFromPythonCall(PythonCall.GET_INDEX_NAMES);
		}
		else if(propertyValue.getProperty() instanceof BasicProperty) {
			return IlsProperty.getChoices((BasicProperty<?>)propertyValue.getProperty());
		}
		else {
			return null;
		}
	}
		
	private Object[] getChoicesFromPythonCall(PythonCall pcall) {
		try {
			Object[] args = {null};
			return PythonCall.toArray(pcall.exec(args));
		} catch (JythonExecException e) {
			logger.error("Error getting choices from python call " + pcall.getMethodName(), e);
			return null;
		}
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public boolean isDateType() {
		return Constants.DATE_TIME.equals(valueType);
	}
	
	public Object getDefaultValue() {
		return getProperty() instanceof BasicProperty ? ((BasicProperty<?>)getProperty()).getDefaultValue() : null;
	}
	
	public Object getValue() {
		// showing default not a good idea--misrepresents the real value and doesn't set the default
		//return propertyValue.getValue() != null ? propertyValue.getValue() : getDefaultValue();
		return propertyValue.getValue();
	}

	public String getCategory() {
		return null;
	}
	
	public boolean isCategory() {
		return false;
	}

	public Property<?> getProperty() {
		return propertyValue.getProperty();
	}

	public PropertyValue<?> getPropertyValue() {
		return propertyValue;
	}

	/** Regardless of underlying type, set the value from a string representation. 
	 *  throws NumberFormatException for bad numbers 
	 * @throws ParseException */
	public void setValueFormatted(String stringValue) throws ParseException {		
		Object value = IlsProperty.parsePropertyValue(getProperty(), stringValue, valueType);
		setValue(value);
	}

	public String getValueFormatted() {
		Object value = getValue();
		if(value != null) {
			if(value instanceof Date) {
				return Constants.DATE_FORMAT.format((Date)value);
			}
			else {
				return value.toString();
			}
		}
		else {
			return "";
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(Object value) {
		propertyValue = new PropertyValue(getProperty(), value);		
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}

}
