package com.ils.sfc.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	/** A debug utility that prints out a gzipped text resource--e.g. the XML for a stored chart. */
	public static void printResource(byte[] data) throws IOException {
		java.io.BufferedReader test = new java.io.BufferedReader(new java.io.InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(data))));
		StringBuilder bldr = new StringBuilder();
		String line = null;
		while((line = test.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println(bldr.toString());
	}

	public static void toXML(XMLStreamWriter writer, ChartUIElement element) {
		// TODO: XMLStreamWriter's escaping of characters is incomplete; e.g.
		// it doesn't handle single quotes. Should we handle that?
		try {
			for(PropertyValue<?> pvalue: element) {
				if(IlsProperty.ignoreProperties.contains(pvalue.getProperty().getName())) continue;
				writer.writeStartElement(pvalue.getProperty().getName());
				if(pvalue.getValue() != null) {
					String saveValue = pvalue.getValue().toString();
					writer.writeCharacters(saveValue);
				}
				else {
					logger.warn("null value for property " + pvalue.getProperty().getName());
				}
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
			Object value = null;
			try {
				value = parseProperty(property, stringValue);
			}
			catch(NumberFormatException nfe) {
				logger.warn("Error deserializing step property "+property+" from "+stringValue, nfe);
				value = stringValue;
			}
			
			ui.setDirect(property, value);
		}
	}

	public static Object parseProperty(Property<?> property, String stringValue) {
		if(property.getType() == String.class) {
			return stringValue;
		}
		else if(property.getType() == Integer.class) {
			return Integer.parseInt(stringValue);
		}
		else if(property.getType() == Double.class) {
			return Double.parseDouble(stringValue);
		}
		else if(property.getType() == Boolean.class) {
			return Boolean.valueOf(stringValue);
		}
		else if(property.getType() == Object.class) {
			return parseObjectValue(stringValue, null);
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

	public static Object getStepPropertyValue(ChartUIElement element, String name) {
		for(PropertyValue<?> value: element.getValues()) {
			if(value.getProperty().getName().equals(name)) {
				return value.getValue();
			}
		}
		return null;
	}

	public static Object getStepPropertyValue(PropertySet properties, String name) {
		for(PropertyValue<?> value: properties.getValues()) {
			if(value.getProperty().getName().equals(name)) {
				return value.getValue();
			}
		}
		return null;
	}

	public static void printSfcStepProperties(ChartUIElement element) {
		for(PropertyValue<?> pv: element.getValues()) {
			System.out.println(pv.getProperty().getName() + ": " + pv.getValue());
		}
	}			

	/** null-tolerant equality check */
	public static boolean equal(Object o1, Object o2) {
		if(o1 == null && o2 == null) {
			return true;
		}
		else if(o1 != null) {
			return o1.equals(o2);
		}
		else {
			return o2.equals(o1);
		}
	}
	
	/** A null-tolerant check for empty strings. */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	/** For a string that may represent a number, string, or boolean, parse it
	 *  with a best guess as to type
	 */
	public static Object parseObjectValue(String strValue, Class<?> hintClass) {
		if(hintClass != Double.class) {
			try { return Integer.parseInt(strValue); }
			catch(NumberFormatException e) { /* didn't work */ }
		}
		try { return Double.parseDouble(strValue); }
		catch(NumberFormatException e) { /* didn't work */ }
		if(strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(strValue); 		
		}
		return strValue;  // nothing else worked, just make it a string
	}
}
