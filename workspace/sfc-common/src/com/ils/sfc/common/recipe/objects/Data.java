package com.ils.sfc.common.recipe.objects;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;

/**
superiorClass: sequence (the symbol S88-OBJECT)
attributes:sequence (structure (
    ATTRIBUTE-NAME: the symbol KEY,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol LABEL,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol DESCRIPTION,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol HELP,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol ADVICE,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol TEXT,
    ATTRIBUTE-INITIAL-VALUE: ""),
  structure (ATTRIBUTE-NAME: the symbol LAST-UPDATE-TIMESTAMP,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol FLOAT,
    ATTRIBUTE-INITIAL-VALUE: 0.0))
    
    Use cases:
       1. Translate G2 export xml to Ignition recipe data (JSON-serialized map)
       2a. Translate Ignition recipe data to map "model" for RecipeDataBrowser       
       2b. Translate map "model" for RecipeDataBrowser to Ignition recipe data
       3. For a single object, produce a map that can be added to the map "model" for RecipeDataBrowser       
       4a. For a single object in the map "model", produce a list of PropertyRow objects to feed the PropertyEditor
       4b. From a list of PropertyRow objects from the PropertyEditor, produce a map that can be added to the map "model" for RecipeDataBrowser
 */
public abstract class Data {
	protected BasicPropertySet properties = new BasicPropertySet();
	
	private static Map<String, Class<?>> concreteClassesByG2Name = new HashMap<String, Class<?>>();
	static {
		concreteClassesByG2Name.put("S88-RECIPE-DATA-GROUP", Group.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-ARRAY-DATA", QuantityArray.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-ARRAY-DATA", ValueArray.class);
		concreteClassesByG2Name.put("S88-RECIPE-INPUT-DATA", Input.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-DATA", Output.class);
		concreteClassesByG2Name.put("S88-RECIPE-OUTPUT-RAMP-DATA", OutputRamp.class);
		concreteClassesByG2Name.put("S88-RECIPE-MATRIX-DATA", Matrix.class);
		concreteClassesByG2Name.put("S88-RECIPE-QUANTITY-LIST-DATA", QuantityList.class);
		concreteClassesByG2Name.put("S88-RECIPE-SQC-DATA", SQC.class);
		concreteClassesByG2Name.put("S88-RECIPE-VALUE-DATA", Value.class);
		concreteClassesByG2Name.put("S88-RECIPE-TEXT-LIST-DATA", TextList.class);
		concreteClassesByG2Name.put("S88-RECIPE-SEQUENCE-DATA", Sequence.class);
		concreteClassesByG2Name.put("S88-RECIPE-STRUCTURE-DATA", Structure.class);
	}

	public static Collection<Class<?>> getConcreteClasses() {
		return concreteClassesByG2Name.values();
	}

	public Data() {
		addProperty(IlsProperty.CLASS);
		addProperty(IlsProperty.KEY);
		addProperty(IlsProperty.LABEL);
		addProperty(IlsProperty.DESCRIPTION);
		addProperty(IlsProperty.HELP);
		addProperty(IlsProperty.ADVICE);
		
		properties.set(IlsProperty.CLASS, getClass().getName());
	}

	public String getKey() {
		return (String) properties.get(IlsProperty.KEY);
	}
	
	public void setKey(String value) {
		properties.set(IlsProperty.KEY, value);
	}
	
	public boolean isGroup() {
		return this instanceof Group;
	}
	
	public BasicPropertySet getProperties() {
		return properties;
	}

	public void setProperties(BasicPropertySet propertyValues) {
		this.properties = propertyValues;
	}

	/** Add a property with the default value. */
	@SuppressWarnings("unchecked")
	protected void addProperty(IlsProperty property) {
		properties.set(property, property.getDefaultValue());
	}
	
	/** de-serialize the Ignition step property to a hierarchical map */
	@SuppressWarnings("unchecked")
	public static HashMap<String,Object> jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		HashMap<String,Object> ignitionMap = (HashMap<String,Object>) mapper.readValue(json, HashMap.class);
		return ignitionMap;
	}
	
	/** Serialize a hierarchical map to the Ignition step property */
	public static String mapToJson(Map<String,Object> ignitionMap) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String json = mapper.writeValueAsString(ignitionMap);
		return json;
	}
	
	public static Data fromMap(Map<String,Object> ignitionMap) {
		return null;
	}
		
	public Map<String,Object> toMap() {
		return null;
	}
	
	/** Translate from G2 export to ignition map. Example of G2 XML element:
	 * <recipe key="bar" label="bar" description="A barby piece of recipe data" help="More useless help" advice="More useless advice" units="DEGC" type="float" category="Simple Constant" val="37.567" high-limit="" low-limit=""  />
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, Object> fromG2(String g2Xml) {
		Map<String,String> g2Attributes = new HashMap<String,String>();
		int eqIndex = -1;
		while((eqIndex = g2Xml.indexOf('"', eqIndex)) != -1) {
			int keyIndex = eqIndex - 1;
			while(!Character.isSpace(g2Xml.charAt(keyIndex))) keyIndex--;
			String key = g2Xml.substring(keyIndex + 1, eqIndex);
			int valueIndex = eqIndex + 2;
			while(g2Xml.charAt(keyIndex) != '"') ++keyIndex;
			String value = g2Xml.substring(eqIndex + 2, valueIndex);
			g2Attributes.put(key, value);
		}
		return null;
	}
		
}
