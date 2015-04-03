package com.ils.sfc.common.step;

import java.text.ParseException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepDelegate implements StepDelegate {
	private static LoggerEx log = LogUtil.getLogger(AbstractIlsStepDelegate.class.getName());
	private static final IlsProperty<?>[] commonProperties = {
		com.ils.sfc.common.IlsProperty.DESCRIPTION, 
		com.ils.sfc.common.IlsProperty.AUDIT_LEVEL
	};
	private Property<?>[] properties;
	
    public static final Property<?>[] FOUNDATION_STEP_PROPERTIES = {
    	EnclosingStepProperties.CHART_PATH,
    	EnclosingStepProperties.EXECUTION_MODE,
    	EnclosingStepProperties.PASSED_PARAMS,
    	EnclosingStepProperties.RETURN_PARAMS,
    };
 
	protected AbstractIlsStepDelegate(Property<?>[] uncommonProperties) {
		// initialize sort order
		int numProperties = uncommonProperties.length + commonProperties.length;
		properties = new Property<?>[numProperties];
		int p = 0;
		for(; p < commonProperties.length; p++) {
			properties[p] = commonProperties[p];
		}
		for(int i=0; i < uncommonProperties.length; i++, p++) {
			properties[p] = uncommonProperties[i];
		}
		// Set the sort order--common properties first, then as given
		for(int i = 0; i < properties.length; i++) {
			Property<?> property = properties[i];
			if(property instanceof IlsProperty) {
				((IlsProperty<?>)property).setSortOrder(i);
			}
		}
	}
	
	@Override
	public void toXML(XMLStreamWriter writer, ChartUIElement element, String arg2)
			throws XMLStreamException {
		IlsSfcCommonUtils.toXML(writer, element);
	}

	@Override
	public void fromXML(Element dom, ChartUIElement ui)
		throws XMLParseException {
		for(Property<?> property: properties) {
			String stringValue = IlsSfcCommonUtils.getPropertyAsString(property, dom);
			Object value = null;
			if(stringValue!=null) {
				try {
					value = IlsProperty.parsePropertyValue(property, stringValue);
				}
				catch(ParseException e) {
					log.warn("Error deserializing step property "+property+" from "+stringValue, e);
					value = stringValue;
				}
			}
			else {
				if(property.equals(EnclosingStepProperties.EXECUTION_MODE)) {
					value = ExecutionMode.RunUntilCompletion;
				}
				else if(property.equals(EnclosingStepProperties.PASSED_PARAMS)) {
					value = new ExpressionParamCollection();
				}
				else if(property.equals(EnclosingStepProperties.RETURN_PARAMS)) {
					value = new ReturnParamCollection();				
				}
				else if(property.getDefaultValue() != null) {
					value = property.getDefaultValue();
				}
				if(value == null) {
					log.warn("Step property "+property+" missing in supplied DOM for " + ui.getType() + "; will use default");
				}
			}
			if(value != null) {
				ui.setDirect(property, value);
			}
			else {
				log.warn("Unable to get value for Step property " + property + "; will be missing");				
			}
		}
	}

	@Override
	public List<Property<?>> getCompilationAlteringProperties() {
		return null;
	}

	@Override
	public void validate(ChartUIElement element,
            ChartCompilationResults compilationResults) {
	}
	
	public PropertySet getPropertySet() {
		// add the ILS properties
		PropertySet props = IlsSfcCommonUtils.createPropertySet(properties);
		return props;
	}

}

