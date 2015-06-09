package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;

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
	
	public static JSONObject fromStepScope(PyChartScope stepScope) throws JSONException {
		return fromStepScopeRecursive(stepScope, 0);
	}
	
	private static JSONObject fromStepScopeRecursive(PyChartScope stepScope, int level) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		for(Object key: stepScope.keys()) {
			if(!(key instanceof String)) continue;
			// ignore the id and name step properties, and assume everything else
			// is recipe data
			if(level == 0 && ("id".equals(key) || "name".equals(key))) continue;
			Object value = stepScope.get(key);
			if(value instanceof PyChartScope) {
				jsonObject.put((String)key, fromStepScope((PyChartScope)value));
			}
			else {
				// null values show up in step scope as JSONObject.NULL, which is 
				// what we want to put in the JSON object, so no special logic:
				jsonObject.put((String)key, value);	
			}
		}
		return jsonObject;	
	}

	/** Convert this object (and any hierarchy) to a JSON object */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		for(PropertyValue<?> pvalue: properties) {
			String propName = pvalue.getProperty().getName();
			Object value = pvalue.getValue();
			if(pvalue.getProperty() == IlsProperty.JSON_LIST || pvalue.getProperty() == IlsProperty.JSON_MATRIX) {
				// we need to turn the list into a JSONArray first
				JSONArray jsonArray = value != null ? new JSONArray((String)value) : new JSONArray();
				value = jsonArray;
			}
			jsonObj.put(propName, value != null ? value : JSONObject.NULL);
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
		for(Data data: recipeData) {
			associatedDataJson.put(data.getKey(), data.toJSON());
		}
	}
	
	/** Create a recipe data hierarchy from a JSON Object that was
	 *  originally created from a recipe hierarchy (i.e. not
	 *  just some random JSONbject. */
	public static List<Data> fromAssociatedData(JSONObject associatedDataJson) throws Exception {
		List<Data> recipeData = new ArrayList<Data>();
		Iterator<String> keyIter = associatedDataJson.keys();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			try {
				JSONObject jsonData = associatedDataJson.getJSONObject(key);
				Data data = fromJson(jsonData);
				recipeData.add(data);
			}
			catch(Exception e) {
				// ?? what to do...we are blindly assuming that everything in the associated
				// data object is recipe data, which isn't necessarily true...
				logger.warn("non-recipe data in associated data for key " + key);
			}
		}		
		return recipeData;
	}

	public static Data fromJson(JSONObject jsonObject) throws Exception {
		String simpleClassName = jsonObject.getString(Constants.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = (Data)Class.forName(fullClassName).newInstance();
		data.setFromJson(jsonObject);
		return data;
	}
	
	/** The recursive part of fromJSON() */
	protected void setFromJson(JSONObject jsonObj) throws Exception {
		@SuppressWarnings("rawtypes")
		BasicProperty dummyProperty = new BasicProperty();
		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = jsonObj.keys();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			dummyProperty.setName(key);
			Object value = jsonObj.get(key);
			if(!(value instanceof JSONObject)) {
				setValue(dummyProperty, value.equals(JSONObject.NULL) ? null : value);
			}
			// else if the value is an object, the Group extension of this method will
			// handle it
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
