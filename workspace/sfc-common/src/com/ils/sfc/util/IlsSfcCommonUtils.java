package com.ils.sfc.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ils.sfc.common.TestStepProperties;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** Misc. utilities that don't fit into any ILS class or superclass. */
public class IlsSfcCommonUtils {
	private static final Logger logger = LoggerFactory.getLogger(IlsSfcCommonUtils.class);

	/** Get the value of a string property from XML, using the default if necessary. */
	public static String getStringProperty(Property<String> property, Element dom) {
		String domValue = getPropertyAsString(property, dom);
		return domValue != null ? domValue : property.getDefaultValue();		
	}
	
	/** Get the value of an int property from XML, using the default if necessary. */
	public static Integer getIntProperty(Property<Integer> property, Element dom) {
		String domValue = getPropertyAsString(property, dom);
		if(domValue != null) {
			try {
				return Integer.parseInt(domValue);
			}
			catch(NumberFormatException e) {
				logger.error("bad format for int property " + property.getName() + ": " + domValue);;
				return (Integer) property.getDefaultValue();
			}
		}
		else {
			return (Integer) property.getDefaultValue();	
		}
		
	}
	
	/** Get the property's string value from the DOM, or return null if not found. */
	private static String getPropertyAsString(Property<?> property, Element dom) {
		NodeList list = dom.getElementsByTagName(property.getName());
		if(list.getLength() == 0) {
			return null;
		}
		else if(list.getLength() == 1) {
			Node node = list.item(0);
			return node.getTextContent();
		}
		else {
			logger.error("property " + property.getName() + " has > 1 element");
			return null;
		}
	}

	public static void toXML(XMLStreamWriter writer, ChartUIElement element) {
		// TODO: XMLStreamWriter's escaping of characters is incomplete; e.g.
		// it doesn't handle single quotes. Should we handle that?
		try {
			for(PropertyValue<?> pvalue: element) {
				writer.writeStartElement(pvalue.getProperty().getName());
				writer.writeCharacters(pvalue.getValue().toString());
				writer.writeEndElement();			
			}
		}
		catch(Exception e) {
			logger.error("Error serializing step properties", e);
		}
	}

	public static void fromXML(Element dom, ChartUIElement ui, Property<?>[] properties) {
		for(Property<?> property: properties) {
			String stringValue = getPropertyAsString(property, dom);
			Object value = parseProperty(property, stringValue);
			ui.setDirect(property, value);
		}
	}

	public static Object parseProperty(Property<?> property, String stringValue) {
		if(property.getType() == String.class) {
			return stringValue;
		}
		else if(property.getType() == Integer.class) {
			return UtilityFunctions.parseInteger(stringValue);
		}
		else if(property.getType() == Double.class) {
			return UtilityFunctions.parseDouble(stringValue);
		}
		else if(property.getType() == Boolean.class) {
			return Boolean.valueOf(stringValue);
		}
		else {
			return stringValue;
		}
	}

	public static Object getDefaultValue(Property<?> prop) {
		if(prop.getDefaultValue() != null) {
			return prop.getDefaultValue();
		}
		else {
			if(prop.getType() == String.class) {
				return "";
			}
			else if(prop.getType() == Double.class) {
				return Double.valueOf(0.);
			}
			else if(prop.getType() == Integer.class) {
				return Integer.valueOf(0);
			}
			else {
				return null;
			}
		}
	}

	public static PropertySet createPropertySet(Property<?>[] properties) {
       	Map<Property<?>,Object> pmap = new HashMap<Property<?>,Object>();
    	for(Property<?> prop: properties) {
    		pmap.put(prop, IlsSfcCommonUtils.getDefaultValue(prop));
    	}
    	BasicPropertySet propSet = new BasicPropertySet(pmap);
    	return propSet;
	}


}
