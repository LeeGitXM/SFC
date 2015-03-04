package com.ils.sfc.common.step;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONException;
import org.w3c.dom.Element;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.recipe.objects.Group;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
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
		IlsSfcCommonUtils.fromXML(dom, ui, properties);
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
		/*
		try {
			props.set(ChartStepProperties.AssociatedData, Group.getStepData());
			System.out.println("added initial step data to custom step");
		} catch (JSONException e) {
			log.error("error creating recipe data", e);
		}
		*/
		return props;
	}

}

