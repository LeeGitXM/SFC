package com.ils.sfc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.inductiveautomation.ignition.common.config.Property;

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



}
