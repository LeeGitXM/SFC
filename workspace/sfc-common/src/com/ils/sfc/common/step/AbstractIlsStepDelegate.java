package com.ils.sfc.common.step;

import java.text.ParseException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepDelegate;
import com.inductiveautomation.sfc.api.XmlParseException;
import com.inductiveautomation.sfc.elements.steps.ExpressionParamCollection;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.elements.steps.enclosing.ExecutionMode;
import com.inductiveautomation.sfc.elements.steps.enclosing.ReturnParamCollection;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepDelegate implements StepDelegate {
	private static LoggerEx log = LogUtil.getLogger(AbstractIlsStepDelegate.class.getName());
	// Note that NAME is handled separately. 
	public static final BasicProperty<?>[] commonProperties = {
		IlsProperty.NAME, 
		IlsProperty.DESCRIPTION
	};
	private Property<?>[] orderedProperties;
	private static ChartStructureManager structureManager = null;
	
    public static final Property<?>[] FOUNDATION_STEP_PROPERTIES = {
    	EnclosingStepProperties.CHART_PATH,
    	EnclosingStepProperties.EXECUTION_MODE,
    	EnclosingStepProperties.PASSED_PARAMS,
    	EnclosingStepProperties.RETURN_PARAMS
    };

    public Property<?>[] getOrderedProperties() {
		return orderedProperties;
	}

	public static final Property<?>[] FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE; 
    static {
    	FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE = new Property<?>[FOUNDATION_STEP_PROPERTIES.length + 1];
    	for(int i = 0; i < FOUNDATION_STEP_PROPERTIES.length; i++) {
    		FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE[i] = FOUNDATION_STEP_PROPERTIES[i];
    	}
    	FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE[FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE.length - 1] = IlsProperty.MESSAGE_QUEUE;
    };
 
	protected AbstractIlsStepDelegate(Property<?>[] ilsProperties) {
		// combine the ILS properties with whichever common or
		// Ignition properties we wish to expose:
		int numProperties = ilsProperties.length + commonProperties.length;
		orderedProperties = new Property<?>[numProperties];
		int p = 0;
		for(; p < commonProperties.length; p++) {
			orderedProperties[p] = commonProperties[p];
		}
		for(int i=0; i < ilsProperties.length; i++, p++) {
			orderedProperties[p] = ilsProperties[i];
		}
	}
	/**
	 * The structure manager provides the information we need to validate
	 * @param mgr
	 */
	public static void setStructureManager(ChartStructureManager mgr) { structureManager = mgr; }
	
	@Override
	public void toXml(XMLStreamWriter writer, ChartUIElement element, String arg2)
			throws XMLStreamException {
		IlsSfcCommonUtils.toXML(writer, element);
	}
	
	@Override
	public void fromXml(Element dom, ChartUIElement ui)
		throws XmlParseException {
		for(Property<?> property: orderedProperties) {
			String stringValue = IlsSfcCommonUtils.getPropertyAsString(property, dom);
			String stepName = IlsSfcCommonUtils.getPropertyAsString(IlsProperty.NAME, dom);
			Object value = null;
			if(stringValue!=null) {
				try {
					value = IlsProperty.parsePropertyValue(property, stringValue, null);
					checkValueChoices(property, stepName, value);
				}
				catch(ParseException e) {
					log.warn("fromXml: Error deserializing step property "+property+" from "+stringValue, e);
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
					log.warn("fromXml:Step property "+property+" missing in supplied DOM for " + ui.getType() + "; will use default");
				}
			}
			if(value != null) {
				ui.setDirect(property, value);
			}
			else if(!IlsProperty.isHiddenProperty(property.getName())) {
				log.warn("fromXml: Unable to get value for Step property " + property + "; will be missing");				
			}
		}
	}

	private void checkValueChoices(Property<?> property, String stepName,
			Object value) {
		if(property instanceof BasicProperty) {
			BasicProperty<?> iprop = (BasicProperty<?>)property;
			// If the value is an enumeration, check that the assigned
			// value is a member of the enumeration:
			String[] choices = IlsProperty.getChoices(iprop);
			if(choices != null) {
				boolean valueIsChoice = false;
				for(Object o: choices) {
					if(o.equals(value)) {
						valueIsChoice = true;
						break;
					}
				}
				if(!valueIsChoice) {
					log.error("checkValueChoices: Bad value for " + stepName + "." + property.getName() + ": " + value);
				}
			}
		}
	}

	@Override
	public List<Property<?>> getCompilationAlteringProperties() {
		return null;
	}

	/**
	 * Use the structure manager to see if the step has compiled, and to traverse the call hierarchy 
	 * so as to check whether or not referenced recipe data exist at the proper level.
	 */
	@Override
	public void validate(ChartUIElement element,ChartCompilationResults compilationResults) {
		if( getStructureManager()!=null ) {   // Will be null in client scope
			//String factoryId = element.get(SimpleHierarchyAnalyzer.factoryIdProperty);
			//JSONObject associatedDataJson = element.get(SimpleHierarchyAnalyzer.associatedDataProperty);
			//List<Data> recipeData = Data.fromAssociatedData(associatedDataJson);
			// See com.ils.sfc.designer.browser.validation.StepErrorsPanel.java for current validator techniques
			// and also com.ils.common.stepPropertyValidator
			//compilationResults.addError(new ChartCompilationResults.CompilationError("I'm an invalid",element.getLocation()));
		}
	} 
	
	public PropertySet getPropertySet() {
		// add the ILS properties
		PropertySet props = IlsSfcCommonUtils.createPropertySet(orderedProperties);
		return props;
	}

	public void validate(ChartInfo chart, ChartUIElement element, StepPropertyValidator validator) {
	}
	
	public static ChartStructureManager getStructureManager() {
		return structureManager;
	}
	
}

