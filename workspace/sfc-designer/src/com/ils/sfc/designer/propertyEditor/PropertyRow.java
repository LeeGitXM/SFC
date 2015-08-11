package com.ils.sfc.designer.propertyEditor;

import java.text.ParseException;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
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
			try {
    			Object[] args = {null};
				return PythonCall.toArray(PythonCall.GET_QUEUE_NAMES.exec(args));
			} catch (JythonExecException e) {
				logger.error("Error getting message queue names", e);
				return null;
			}
		}
		else if(propertyValue.getProperty() instanceof BasicProperty) {
			return IlsProperty.getChoices((BasicProperty<?>)propertyValue.getProperty());
		}
		else {
			return null;
		}
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
		Object value = IlsProperty.parsePropertyValue(getProperty(), stringValue);
		setValue(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(Object value) {
		propertyValue = new PropertyValue(getProperty(), value);		
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}

}
