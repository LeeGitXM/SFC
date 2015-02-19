package com.ils.sfc.common.recipe.objects;

import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;

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
	@SuppressWarnings("unchecked")
	protected void addProperty(IlsProperty property) {
		properties.set(property, property.getDefaultValue());
	}	

	/** Get the value object associated with the given property name, or null if not found. */
	PropertyValue<?> findPropertyValue(String propertyName) {
		for(PropertyValue<?> pvalue: properties) {
			if(pvalue.getProperty().getName().equals(propertyName)) {
				return pvalue;
			}
		}
		return null;
	}
	

	/** Convert a recipe data hierarchy to a JSON Object */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		for(PropertyValue<?> pvalue: properties) {
			if(pvalue.getValue() != null) {
				jsonObj.put(pvalue.getProperty().getName(), pvalue.getValue());
			}
		}
		if(s88Level != null) {
			jsonObj.put(IlsSfcNames.S88_LEVEL, s88Level);
		}
		return jsonObj;
	}

	/** Create a recipe data hierarchy from a JSON Object that was
	 *  originally created from a recipe hierarchy (i.e. not
	 *  just some random JSONbject. */
	public static Data fromJson(JSONObject jsonObj) throws Exception {
		String simpleClassName = jsonObj.getString(IlsSfcNames.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = (Data)Class.forName(fullClassName).newInstance();
		data.setFromJson(jsonObj);
		return data;
	}

	/** The recursive part of fromJSON() */
	protected void setFromJson(JSONObject jsonObj) throws Exception {
		Map<Property<?>,java.lang.Object> rawValueMap = properties.getRawValueMap();
		for(Property<?> prop: rawValueMap.keySet()) {
			if(jsonObj.has(prop.getName())) {
				rawValueMap.put(prop, jsonObj.get(prop.getName()));
			}
		}
		if(jsonObj.has(IlsSfcNames.S88_LEVEL)) {
			s88Level = (String)jsonObj.get(IlsSfcNames.S88_LEVEL);
		}	
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
