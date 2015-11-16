package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
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
	private Map<String, BasicProperty<?>> propertiesByName = new HashMap<String, BasicProperty<?>>();
	// id and parentId come from the G2 export and are used to re-compose a hierarchy:
	protected String g2Id;
	protected String parentG2Id;
	protected String stepPath;
	protected String provider;

	public Data() {
		addProperty(IlsProperty.CLASS);
		addProperty(IlsProperty.KEY);
		addProperty(IlsProperty.LABEL);
		addProperty(IlsProperty.DESCRIPTION);
		addProperty(IlsProperty.HELP);
		addProperty(IlsProperty.ADVICE);
		addProperty(IlsProperty.DATA_ID);
		
		properties.set(IlsProperty.CLASS, getClass().getSimpleName());
	}

	public String getG2Id() {
		return g2Id;
	}

	public void setG2Id(String id) {
		this.g2Id = id;
	}

	public String getParentG2Id() {
		return parentG2Id;
	}
	
	public Object getValue(Property<?> property) {
		return properties.getOrDefault(property);
	}

	public boolean hasProperty(String propName) {
		return getProperty(propName) != null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(Property<?> property, Object value) {
		BasicProperty myProperty = getProperty(property.getName());
		if(myProperty != null) {
			properties.set(myProperty, value);
		}
		else {
			logger.error("Property " + property.getName() + " not found in " + this.getClass().getSimpleName());
		}
	}
	
	public void setParentG2Id(String parentId) {
		this.parentG2Id = parentId;
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
	protected void addProperty(BasicProperty property) {
		propertiesByName.put(property.getName(), property);
		properties.set(property, property.getDefaultValue());
	}	

	/** Get the property with the given name, or null if none. */
	BasicProperty<?> getProperty(String propertyName) {
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
			if(level == 0 && (Constants.ID.equals(key) || Constants.NAME.equals(key))) continue;
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
		boolean hasDateValue = hasDateValueType();  // CAUTION: must call OUTSIDE property iteration to avoid concurrent mod
		for(PropertyValue<?> pvalue: properties) {
			String propName = pvalue.getProperty().getName();
			Object value = pvalue.getValue();
			if(pvalue.getProperty() == IlsProperty.JSON_LIST || pvalue.getProperty() == IlsProperty.JSON_MATRIX) {
				// we need to turn the list into a JSONArray first
				JSONArray jsonArray = value != null ? new JSONArray((String)value) : new JSONArray();
				value = jsonArray;
			}
			else if(value != null && hasDateValue && 
					pvalue.getProperty().equals(IlsProperty.VALUE)) {
				// JSON doesn't understand Dates, so if we have a Date value
				// we need to store it as a String
				if(value instanceof Date) {
					if(value != null) {
						Date dateValue = (Date)value;
						value = Constants.DATE_FORMAT.format(dateValue);
					}
				}
				else {
					logger.error("Expecting Date for value); found " + value);
				}
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
			if(key.equals(Constants.S88_LEVEL) || key.equals("runningTime")) {
				continue;
			}
			try {
				JSONObject jsonData = associatedDataJson.getJSONObject(key);
				Data data = fromJson(jsonData);
				recipeData.add(data);
			}
			catch(Exception e) {
				// ?? what to do...we are blindly assuming that everything in the associated
				// data object is recipe data, which isn't necessarily true...
				logger.debug("Error creating recipe data", e);
			}
		}		
		return recipeData;
	}

	/** Restore an object from JSON */
	public static Data fromJson(JSONObject jsonObject) throws Exception {
		String simpleClassName = jsonObject.getString(Constants.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = createForRestore(Class.forName(fullClassName));
		data.setFromJson(jsonObject);
		
		// if we're restoring an old instance from before we assigned UUIDs, give it one:
		if(IlsSfcCommonUtils.isEmpty((String)data.getValue(IlsProperty.DATA_ID))) {
			assignUniqueId(data);
		}
			 		
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
		// JSON doesn't understand Dates, so if we have a Date value
		// we need to re-create it
		if(hasDateValueType()) {
			String strValue = (String)getValue(IlsProperty.VALUE);
			if(!IlsSfcCommonUtils.isEmpty(strValue)) {
				Date dateValue = Constants.DATE_FORMAT.parse(strValue);
				setValue(IlsProperty.VALUE, dateValue);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addDynamicProperty(String name, Object value) {
		BasicProperty<?> newProperty = IlsProperty.createProperty(name, Object.class, value);
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
	
	/** Create an instance of the given class to be used to restore
	 *  an existing instance. */
	public static Data createForRestore(Class<?> aClass) {
		return basicCreate(aClass);
	}
	
	/** Create a brand new instance of the given class. This includes
	 *  recipe data converted from G2. */
	public static Data createNewInstance(Class<?> aClass) {
		Data newInstance = basicCreate(aClass);
		assignUniqueId(newInstance);
		return newInstance;
	}

	private static void assignUniqueId(Data newInstance) {
		UUID uniqueId = UUID.randomUUID();
		newInstance.setValue(IlsProperty.DATA_ID, uniqueId);
	}
	
	/** Instantiate the given Data subclass. */
	private static Data basicCreate(Class<?> aClass) {
		try {
			return (Data)aClass.newInstance();
		} catch (Exception e) {
			logger.error("error creating recipe data", e);
			return null;
		}
	}

	public String getStepPath() {
		return stepPath;
	}
	
	public void setStepPath(String chartPath) {
		this.stepPath = chartPath;
	}
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/** Create the UDT tag if it doesn't already exist, 
	 *  and initialize the tag values with the defaults. */
	public void createTag(String valueTypeOrNull) {
		if(tagExists()) return;
		String myType = isGroup() ? "Data" : (String) getValue(IlsProperty.CLASS);
		
		// if value type not given, use the value as a cue:
		if(valueTypeOrNull == null) {
			valueTypeOrNull = inferValueType();
			if(valueTypeOrNull != null) {
				logger.debugf("Inferred value type %s for tag %s.%s", valueTypeOrNull, stepPath, getKey());
			}
		}
		setValueType(valueTypeOrNull);
		Object[] args = {provider, stepPath, getKey(), myType, valueTypeOrNull};
		try {
			PythonCall.CREATE_RECIPE_DATA.exec(args);
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag creation failed", e);
		}
		basicWriteToTags();
	}

	/** Return true if this object has the VALUE property. */
	public boolean hasValueType() {
		return properties.contains(IlsProperty.VALUE_TYPE);
	}
	
	public boolean hasDateValueType() {
		return hasValueType() && Constants.DATE_TIME.equals(getValue(IlsProperty.VALUE_TYPE));
	}
	
	private void setValueType(String valueTypeOrNull) {
		if(hasValueType()) {
			setValue(IlsProperty.VALUE_TYPE, valueTypeOrNull);
		}		
	}

	/** For Value instances, infer the type from the actual value. 
	 *  returns null if value can't be inferred
	 */
	private String inferValueType() {
		if(properties.contains(IlsProperty.TYPE)) {
			String g2Type = ((String)getValue(IlsProperty.TYPE)).toLowerCase();
			if(g2Type.equals("float") || g2Type.equals("quantity") ) {
				return Constants.FLOAT;
			}
			else if(g2Type.equals("integer")) {
				return Constants.INT;
			}
			else if(g2Type.equals("symbol")) {
				return Constants.STRING;
			}
		}
		
		Object value = getValue(IlsProperty.VALUE);
		if(value != null) {
			if(value instanceof Double || value instanceof Float) {
				return Constants.FLOAT;
			}
			else if(value instanceof Integer) {
				return Constants.INT;
			}
			else if(value instanceof Boolean) {
				return Constants.BOOLEAN;
			}
			else if(value instanceof String) {
				return Constants.STRING;
			}
			else {
				logger.error("Could not infer type from value class " + value.getClass().getSimpleName());
				return null;
			}
		}
		else {
			return null;
		}
	}

	/** Write to tags, creating them if they don't exist. */
	public void writeToTags()  {
		createTag(null);
		basicWriteToTags();
	}

	/** Write all the attribute values out to the tags. */
	private void basicWriteToTags() {
		try {
			for(PropertyValue<?> pval: getProperties()) {
			String attributePath = getTagAttributePath(pval.getProperty());
			Object value = getValue(pval.getProperty());
			// note: Ignition will not allow a synchronous tag write from
			// a UI thread, so writes from Designer UI must be async
			Object[] setArgs = {provider, attributePath, value, false};
			PythonCall.SET_RECIPE_DATA.exec(setArgs);
			}
		} catch (JythonExecException e) {
			logger.error("Recipe Data write to tags failed", e);
		}
	}

	/** If the UDT tag exists and has a different value from the
	 *  data, use the value from the tag. */
	public void readFromTags() {
		if(isGroup() || !tagExists()) return;
		for(PropertyValue<?> pv: properties) {
			Object pvalue = pv.getValue();
			Object tagValue = getTagValue(pv.getProperty());
			if(tagValue instanceof Date) {
				tagValue = Constants.DATE_FORMAT.format((Date)tagValue);
			}
			if(!IlsSfcCommonUtils.equal(pvalue, tagValue)) {
				setValue(pv.getProperty(), tagValue);
			}
		}
	}

	/** Remove the UDT tag corresponding to this object. */
	public void deleteTag() {
		Object[] args = {provider, getTagPath()};
		try {
			PythonCall.DELETE_RECIPE_DATA.exec(args);
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag deletion failed", e);
		}		
	}
	
	/** Get the tag path of the UDT instance corresponding to this object. */
	public String getTagPath() {
		String path = getStepPath() + "/" + getKey();
		return path;
	}
	
	/** Get the tag path for a particular property of this object. */
	public String getTagAttributePath(Property<?> property) {
		return getTagPath() + "/" + property.getName();
	}
	
	/** Get the value of the given property from the tag. The tag must exist,
	 *  or an error results. */
	public Object getTagValue(Property<?> property) {
		String valuePath = getTagAttributePath(property);
		Object[] args = {provider, valuePath};
		try {
			Object value = PythonCall.GET_RECIPE_DATA.exec(args);
			return value;
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag read failed", e);
			return null;
		}		
	}
	
	/** Check if the UDT tag corresponding to this object exists. */
	public boolean tagExists() {
		String tagPath = getTagPath();
		Object[] args = {provider, tagPath};
		try {
			Boolean value = (Boolean)PythonCall.RECIPE_DATA_EXISTS.exec(args);
			return value;
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag existence check failed", e);
			return false;
		}				
	}

	public void readTreeFromTags() {
		if(isGroup()) {
			for(Data data: ((Group)this).getChildren()) {
				data.readTreeFromTags();
			}
		}
		else {
			readFromTags();
		}
	}
	
}
