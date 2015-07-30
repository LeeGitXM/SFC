package com.ils.sfc.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.sfc.api.PyChartScope;
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
	public static String getPropertyAsString(Property<?> property, Element dom) {
		String propertyName = property.getName();
		NodeList list = dom.getElementsByTagName(propertyName);
		if(list.getLength() == 0) {
			// not an element--maybe it is an attribute; if not this will
			// return an empty string
			String value = dom.getAttribute(propertyName);
			return value.length() > 0 ? value : null;
		}
		else if(list.getLength() == 1) {
			Node node = list.item(0);
			return node.getTextContent();
		}
		else {
			logger.error("property " + propertyName + " has > 1 element");
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
	
	/** A null-tolerant check for strings with no real content. */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	/** Get the highest level scope (by looking at parent scopes) */
	public static PyChartScope getTopScope(PyChartScope scope) {
		while(scope.get("parent") != null) {
			scope = (PyChartScope)scope.get("parent");
		}
		return scope;
	}
	
	public static void main(String[] args) {
		/*
		JDBC driver = new JDBC(); // Force driver to be loaded
		String path = "c:/root/repo/git/sfc/migration/mdb/conversion.db";
		String connectPath = "jdbc:sqlite:"+path;
		try {
			Connection connection = DriverManager.getConnection(connectPath);
			System.out.println("connection OK");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
		Paths.get("/","temp");
	}
}
