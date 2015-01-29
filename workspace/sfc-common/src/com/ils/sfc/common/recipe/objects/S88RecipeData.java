package com.ils.sfc.common.recipe.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.ils.sfc.util.IlsProperty;
import com.ils.sfc.util.IlsSfcCommonUtils;

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
public abstract class S88RecipeData {
	protected List<IlsProperty<?>> properties = new ArrayList<IlsProperty<?>>();
	private Map<IlsProperty<?>,Object> valuesByProperty = new HashMap<IlsProperty<?>,Object>();
	public static final String CLASS = "class";	// Not in S88 spec; the name of the Java class
	// KEY attribute not needed (?), is held separately in dictionary
	
	public static final String VAL = "val";  // added by leaf classes
	
	private static Class<?>[] concreteClasses = {
		S88RecipeDataGroup.class,
		S88RecipeQuantityArrayData.class,
		S88RecipeValueArrayData.class,
		S88RecipeInputData.class,
		S88RecipeOutputData.class,
		S88RecipeOutputRampData.class,
		S88RecipeMatrixData.class,
		S88RecipeQuantityListData.class,
		S88RecipeSQCData.class,
		S88RecipeValueData.class,
		S88RecipeTextListData.class,
		S88RecipeSequenceData.class,
		S88RecipeStructureData.class
	};
	
	public S88RecipeData() {
		properties.add(IlsProperty.CLASS);
		properties.add(IlsProperty.LABEL);
		properties.add(IlsProperty.DESCRIPTION);
		properties.add(IlsProperty.HELP);
		properties.add(IlsProperty.ADVICE);
	}
	
	/** de-serialize the Ignition step property to a map. The map may have deep structure */
	@SuppressWarnings("unchecked")
	public static HashMap<String,Object> ignitionToMap(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		HashMap<String,Object> map = (HashMap<String,Object>) mapper.readValue(json, HashMap.class);
		return map;
	}
	
	/** Serialize a map to the Ignition step property */
	public static String mapToIgnition(Map<String,Object> map) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		String json = mapper.writeValueAsString(map);
		return json;
	}
	
	/** Export objects to a map format */
	public Map<String,String> toMap() {
		Map<String,String> map = new HashMap<String,String>();
		for(IlsProperty<?> property: properties) {
			Object value = valuesByProperty.get(property);
			map.put(property.getName(), value != null ? value.toString() : null);
		}
		return map;
	}
	
	/** Translate from G2 export to Ignition step property. Example of G2 XML element:
	 * <recipe key="bar" label="bar" description="A barby piece of recipe data" help="More useless help" advice="More useless advice" units="DEGC" type="float" category="Simple Constant" val="37.567" high-limit="" low-limit=""  />
	 */
	@SuppressWarnings("deprecation")
	public static String fromG2(String g2Xml) {
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

	/** */
	private static S88RecipeData createFromMap(Map<String, String> map) {
		String className = (String) map.get(CLASS);
		S88RecipeData data;
		try {
			data = (S88RecipeData) Class.forName(className).newInstance();
			data.initFromMap(map);
			return data;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	

	/** Initialize this object from a map. */
	private void initFromMap(Map<String,String> map) {
		for(IlsProperty<?> property: properties) {
			String svalue = map.get(property.getName());
			Object value = IlsSfcCommonUtils.parseProperty(property, svalue);
			valuesByProperty.put(property, value);
		}		
	}

	public static BasicPropertySet mapToProperties(Map<String,Object> map) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Class<?>[] getTypes() {
		return concreteClasses;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BasicPropertySet getPropertiesForType(String key, Class aClass) throws Exception {
		S88RecipeData recipeData = (S88RecipeData) aClass.newInstance();
		BasicPropertySet propertyValues = new BasicPropertySet();
		for(IlsProperty property: recipeData.properties) {
			propertyValues.set(property, property.getDefaultValue());
		}
		return propertyValues;
	}

	public static Map<String, Object> propertiesToMap(List<PropertyValue> propertyValues) {
		Map<String, Object> map = new HashMap<String, Object>();
		for(PropertyValue value: propertyValues) {
			map.put(value.getProperty().getName(), value.getValue());
		}
		return map;
	}
	
	
		
}
