package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

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
	private static LoggerEx logger = LogUtil.getLogger(Data.class.getName());
	protected BasicPropertySet properties = new BasicPropertySet();
	private Map<String, IlsProperty<?>> propertiesByName = new HashMap<String, IlsProperty<?>>();
	protected String s88Level;
	// id and parentId come from the G2 export and are used to re-compose a hierarchy:
	protected String id;
	protected String parentId;
	

	public Data() {
		addProperty(IlsProperty.CLASS);
		addProperty(IlsProperty.KEY);
		addProperty(IlsProperty.LABEL);
		addProperty(IlsProperty.DESCRIPTION);
		addProperty(IlsProperty.HELP);
		addProperty(IlsProperty.ADVICE);
		
		properties.set(IlsProperty.CLASS, getClass().getSimpleName());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}
	
	public Object getValue(Property<?> property) {
		return properties.get(property);
	}

	public boolean hasProperty(String propName) {
		return getProperty(propName) != null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(Property<?> property, Object value) {
		if(value == null) {
			throw new IllegalArgumentException("null values are not allowed");
		}
		IlsProperty myProperty = getProperty(property.getName());
		if(myProperty != null) {
			properties.set(myProperty, value);
		}
		else {
			logger.error("Property " + property.getName() + " not found in " + this.getClass().getSimpleName());
		}
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getS88Level() {
		return s88Level;
	}

	public void setS88Level(String s88Level) {
		this.s88Level = s88Level;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addProperty(IlsProperty property) {
		propertiesByName.put(property.getName(), property);
		properties.set(property, property.getDefaultValue());
	}	

	/** Get the property with the given name, or null if none. */
	IlsProperty<?> getProperty(String propertyName) {
		return propertiesByName.get(propertyName);
	}
	

	/** Convert this object (and any hierarchy) to a JSON object */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		for(PropertyValue<?> pvalue: properties) {
			String propName = pvalue.getProperty().getName();
			Object valueOrDefault = pvalue.getValue() != null ? 
				pvalue.getValue() : pvalue.getProperty().getDefaultValue();
			if(valueOrDefault != null) {
				if(pvalue.getProperty() == IlsProperty.JSON_LIST || pvalue.getProperty() == IlsProperty.JSON_MATRIX) {
					// we need to turn the list into a JSONArray first
					JSONArray jsonArray = new JSONArray((String)valueOrDefault);
					valueOrDefault = jsonArray;
				}
				jsonObj.put(propName, valueOrDefault);
			}
			else {
				logger.error("property " + propName + " is null; cannot add to JSON");
			}
		}
		if(s88Level != null) {
			jsonObj.put(IlsSfcNames.S88_LEVEL, s88Level);
		}
		return jsonObj;
	}

	/** Create an associated data object containing only the recipe data 
	 * @throws JSONException */
	public static JSONObject toAssociatedData(List<Data> recipeData) throws JSONException {
		JSONObject associatedDataJson = new JSONObject();
		setAssociatedData(associatedDataJson, recipeData);
		return associatedDataJson;
	}
	
	/** Set recipe data in the given associated data object */
	public static void setAssociatedData(JSONObject associatedDataJson, List<Data> recipeData) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		for(Data data: recipeData) {
			jsonObject.put(data.getKey(), data.toJSON());
		}
		associatedDataJson.put(IlsSfcNames.RECIPE_DATA, jsonObject);
	}
	
	/** Create a recipe data hierarchy from a JSON Object that was
	 *  originally created from a recipe hierarchy (i.e. not
	 *  just some random JSONbject. */
	public static List<Data> fromAssociatedData(JSONObject associatedDataJson) throws Exception {
		List<Data> recipeData = new ArrayList<Data>();
		if(!associatedDataJson.has(IlsSfcNames.RECIPE_DATA)) return recipeData;
		JSONObject jsonObject = associatedDataJson.getJSONObject(IlsSfcNames.RECIPE_DATA);
		Iterator<String> keyIter = jsonObject.keys();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			JSONObject jsonData = jsonObject.getJSONObject(key);
			Data data = fromJson(jsonData);
			recipeData.add(data);
		}		
		return recipeData;
	}

	public static Data fromJson(JSONObject jsonObject) throws Exception {
		String simpleClassName = jsonObject.getString(IlsSfcNames.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = (Data)Class.forName(fullClassName).newInstance();
		data.setFromJson(jsonObject);
		return data;
	}
	
	/** The recursive part of fromJSON() */
	protected void setFromJson(JSONObject jsonObj) throws Exception {
		BasicProperty dummyProperty = new BasicProperty();
		Iterator<String> keyIter = jsonObj.keys();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			dummyProperty.setName(key);
			Object value = jsonObj.get(key);
			if(!(value instanceof JSONObject)) {
				setValue(dummyProperty, value);
			}
			// else if the value is an object, the Group extension of this method will
			// handle it
		}
		if(jsonObj.has(IlsSfcNames.S88_LEVEL)) {
			s88Level = (String)jsonObj.get(IlsSfcNames.S88_LEVEL);
		}	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addDynamicProperty(String name, Object value) {
		IlsProperty<?> newProperty = new IlsProperty(name, value.getClass(), value);
		addProperty(newProperty);	
	}
	
	protected void printSpace(int count) {
		for(int i = 0; i < count; i++) {
			System.out.print("   ");
		}
	}
	
	protected void print(int level) {
		for(PropertyValue<?> pvalue: properties) {
			printSpace(level);
			System.out.println(pvalue.getProperty().getName() + ": " + pvalue.getValue());
		}
	}

}
