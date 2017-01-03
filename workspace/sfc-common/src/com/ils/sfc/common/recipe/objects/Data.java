package com.ils.sfc.common.recipe.objects;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
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
import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;

import system.ils.sfc.common.Constants;

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
	private static final String CLSS = "Data";
	private static LoggerEx log = LogUtil.getLogger(Data.class.getName());
	protected static SimpleDateFormat parser = new SimpleDateFormat("YYYY/mm/dd HH:mm:ss");
	protected BasicPropertySet properties = new BasicPropertySet();
	private Map<String, BasicProperty<?>> propertiesByName = new HashMap<String, BasicProperty<?>>();
	// id and parentId come from the G2 export and are used to re-compose a hierarchy:
	protected String g2Id;
	protected String parentG2Id;
	protected String stepPath;
	protected String provider;
	private Data parent;
	public static final String[] valueTypeChoices = {Constants.FLOAT, Constants.INT, Constants.BOOLEAN, 
		Constants.STRING, Constants.DATE_TIME};
	
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

	// ======================================== Static Initializers ==============================
	
	/** Create a brand new instance of the given class. This includes
	 *  recipe data converted from G2. */
	public static Data createNewInstance(String className) {
		Data data = null;
		try {
			Class<?> clazz = Class.forName(className);
			if( clazz==null) throw new IllegalArgumentException(String.format("the class %s is not loaded", className));
			data = Data.createNewInstance(clazz);
		} 
		catch (Exception e) {
			log.error(String.format("%s.createNewInstance:ERROR creating class instance (%s)",CLSS,className), e);   // Prints stack trace
			return null;
		}
		
		return data;
	}
	
	/** Create a brand new instance of the given class. This includes
	 *  recipe data converted from G2. */
	public static Data createNewInstance(Class<?> clss) {
		Data data = null;
		try {
			Constructor<?> ctor = clss.getConstructor();
			data = (Data)ctor.newInstance();
			data.assignUniqueId();
		} 
		catch (Exception e) {
			log.error(String.format("%s.createNewInstance:ERROR creating recipe data (%s)",CLSS,clss.getCanonicalName()), e);   // Prints stack trace
			return null;
		}
		
		return data;
	}

	public static Data createRecipeData(String className, String chartPath, 
		String key,  String valueType, String provider, Group parentGroup) {
		Data newObject = Data.createNewInstance(className);
		newObject.setKey(key);
		newObject.setProvider(provider);
		newObject.setStepPath(chartPath);
		newObject.setValueType(valueType);
		newObject.setParent(parentGroup);
		newObject.createTag();
		return newObject;
	}
	
	
	public static JSONObject fromStepScope(PyChartScope stepScope) throws JSONException {
		log.info("In Data.fromStepScope()");
		return Data.fromStepScopeRecursive(stepScope, 0);
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
	/** Create an associated data object containing only the recipe data 
	 * @throws JSONException */
	public static JSONObject toAssociatedData(List<Data> recipeData) throws JSONException {
		log.info("in Data.toAssociatedData");
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
	public static List<Data> fromDatabase(String stepUUID) throws Exception {
		List<Data> recipeData = new ArrayList<Data>();
		
		try {
			List<PyDictionary> pyDictList = (List<PyDictionary>) PythonCall.GET_RECIPE_DATA_LIST.exec(stepUUID);
			log.infof("Back in Java land, received: %s", pyDictList);
			for (PyDictionary pyDict:pyDictList){
				log.infof("%s", pyDict);
				
				//Get the class name out of my Python dictionary
				
				String fullClassName = "ffoo";
				Data data = Data.createNewInstance(fullClassName);
				
				// set the attribute values of the new instance from the dictionary
				
				recipeData.add(data);
			}
			
		}
		catch(Exception e) {
			// ?? what to do...we are blindly assuming that everything in the associated
			// data object is recipe data, which isn't necessarily true...
			log.debug("Error creating recipe data", e);
		}
		return recipeData;
	}
	
	/** Create a recipe data hierarchy from a JSON Object that was
	 *  originally created from a recipe hierarchy (i.e. not
	 *  just some random JSONbject. */
	public static List<Data> fromAssociatedData(JSONObject associatedDataJson) throws Exception {
		log.info("In Data.fromAssociatedData()");
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
				log.debug("Error creating recipe data", e);
			}
		}		
		return recipeData;
	}

	/** Restore an object from JSON */
	public static Data fromJson(JSONObject jsonObject) throws Exception {
		log.info("In Data.fromJSON()");
		String simpleClassName = jsonObject.getString(Constants.CLASS);
		String packageName = Data.class.getPackage().getName();
		String fullClassName = packageName + "." + simpleClassName;
		Data data = Data.createNewInstance(fullClassName);
		if( data!=null ) {
			data.setFromJson(jsonObject);

			// if we're restoring an old instance from before we assigned UUIDs, give it one:
			if(IlsSfcCommonUtils.isEmpty((String)data.getValue(IlsProperty.DATA_ID))) {
				data.assignUniqueId();
			}
		}
		return data;
	}
	
	
	public String getG2Id() {return g2Id;}
	public String getParentG2Id() {return parentG2Id;}
	public Object getValue(Property<?> property) {return properties.getOrDefault(property);	}

	public boolean hasProperty(String propName) {return getProperty(propName) != null;}
	
	public void setG2Id(String id) {this.g2Id = id;}
	public void setParent(Data parent) {this.parent = parent;}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(Property<?> property, Object value) {
		BasicProperty myProperty = getProperty(property.getName());
		if(myProperty != null) {
			properties.set(myProperty, value);
		}
		else {
			log.error("Property " + property.getName() + " not found in " + this.getClass().getSimpleName());
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
	public BasicProperty<?> getProperty(String propertyName) {
		return propertiesByName.get(propertyName);
	}
	


	/** 
	 * Convert this object (and any hierarchy) to a JSON object. 
	 * NOTE: JSON_LIST and MATRIX objects are simply stored as Strings.
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		boolean hasDateValue = hasDateValueType();  // CAUTION: must call OUTSIDE property iteration to avoid concurrent mod
		for(PropertyValue<?> pvalue: properties) {
			String propName = pvalue.getProperty().getName();
			Object value = pvalue.getValue();
			if(value != null && hasDateValue && 
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
					log.error("Expecting Date for value); found " + value);
				}
			}
			jsonObj.put(propName, value != null ? value : JSONObject.NULL);
		}
		return jsonObj;
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


	private void assignUniqueId() {
		UUID uniqueId = UUID.randomUUID();
		setValue(IlsProperty.DATA_ID, uniqueId);
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

	/** Create a string-type tag underneath the group folder */
	private void createGroupPropertyTag(String name) {
		String folderPath = stepPath + "/" + getParentPath() + getKey();
		Object[] args = {provider, folderPath, name};
		try {
			PythonCall.CREATE_GROUP_PROPERTY_TAG.exec(args);
		} catch (JythonExecException e) {
			log.error("Recipe Group tag creation failed", e);
		}		
	}
	
	/** Create the UDT tag if it doesn't already exist, 
	 *  and initialize the tag values with the defaults. */
	public void createTag() {
		if(tagExists()) return;
		String myType = (String) getValue(IlsProperty.CLASS);
		
		Object[] args = {provider, stepPath + "/" + getParentPath(), getKey(), myType, getValueType()};
		try {
			PythonCall.CREATE_RECIPE_DATA.exec(args);
		} catch (JythonExecException e) {
			log.error("Recipe Data tag creation failed", e);
		}
		
		// If this is a group, the basic type is a folder rather than a UDT. As far as I know,
		// you can't have a UDT folder, so we have to create all the child property tags 
		// explicitly
		if(this instanceof Group) {
			for(PropertyValue<?> prop: getProperties()) {
				createGroupPropertyTag(prop.getProperty().getName());
			}
		}

		basicWriteToTags();
	}

	private String getParentPath() {
		StringBuilder buf = new StringBuilder();
		if(parent != null) {
			parent.addParentKey(buf);
		}
		return buf.toString();
	}

	/** Recursive method to add parent keys to a key path. */
	private void addParentKey(StringBuilder buf) {
		if(parent != null) {
			parent.addParentKey(buf);
		}
		buf.append(getKey());
		buf.append("/");
	}
	
	private String getKeyPath() {
		return getParentPath() + getKey();
	}

	/** Return true if this object has the VALUE property. */
	public boolean hasValueType() {
		return properties.contains(IlsProperty.VALUE_TYPE);
	}
	
	public boolean hasDateValueType() {
		return hasValueType() && Constants.DATE_TIME.equals(getValue(IlsProperty.VALUE_TYPE));
	}
	
	public void setValueType(String valueType) {
		if(hasValueType()) {
			setValue(IlsProperty.VALUE_TYPE, valueType);
		}		
	}

	private String getValueType() {
		if(hasValueType()) {
			return (String)getValue(IlsProperty.VALUE_TYPE);
		}		
		else {
			return null;
		}
	}

	/** For Value instances, infer the type from the actual value. 
	 *  returns null if value can't be inferred
	 */
/*
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
*/
	/** Write to tags, creating them if they don't exist. */
	public void writeToTags()  {
		createTag();
		basicWriteToTags();
	}

	/** Write all the attribute values out to the tags. */
	private void basicWriteToTags() {
		try {
			for(PropertyValue<?> pval: getProperties()) {
				String attributePath = getTagAttributePath(pval.getProperty());
				Object value = getValue(pval.getProperty());
				if(value != null && pval.getProperty() == IlsProperty.JSON_MATRIX) {
					value = createDataset((String)value);
				}
				else if(value != null && pval.getProperty() == IlsProperty.JSON_LIST) {
					String type = getValueType();
					if( type==null ) type = Constants.STRING;  // Default
					String json = (String)value;
					value = null;
					try {
						if( type.equals(Constants.BOOLEAN)) {
							value = createBooleanArray(json);
						}
						else if( type.equals(Constants.DATE_TIME)) {
							value = createDateArray(json);

						}
						else if( type.equals(Constants.FLOAT)) {
							value = createDoubleArray(json);
						}
						else if( type.equals(Constants.INT)) {
							value = createIntegerArray(json);
						}
						else {    // Default to String
							value = createStringArray(json);
						}
					}
					catch(Exception e) {
						value = "Not a valid JSON array of "+type;
					}
				}
				// NOTE: Ignition will not allow a synchronous tag write from
				// a UI thread (final argument is false) 
				Object[] args = {provider, attributePath, value, false};
				PythonCall.SET_RECIPE_DATA.exec(args);
			}
		}
		catch (JythonExecException e) {
			log.error("Recipe Data write to tags failed", e);
		}
	}

	/** Create a boolean array from a JSON string */
	protected Boolean[] createBooleanArray(String value) {
		try {
			JSONArray jarray = new JSONArray((String)value);
			Boolean[] array = new Boolean[jarray.length()];
			for(int i = 0; i < jarray.length(); i++) {
				array[i] = jarray.getBoolean(i);
			}
			return array;
		}
		catch(Exception e) {
			log.error("createBooleanArray: Error converting JSON ("+value+") to array of bools", e);
		}
		return null;
	}
	/** Create a date array from a JSON string */
	protected Date[] createDateArray(String value) {
		try {
			JSONArray jarray = new JSONArray((String)value);
			Date[] array = new Date[jarray.length()];
			for(int i = 0; i < jarray.length(); i++) {
				array[i] = parser.parse(jarray.getString(i));
			}
			return array;
		}
		catch(Exception e) {
			log.error("createDateArray: Error converting JSON ("+value+") to array of dates", e);
		}
		return null;
	}
	/** Create a double array from a JSON string */
	protected Double[] createDoubleArray(String value) {
		try {
			JSONArray jarray = new JSONArray((String)value);
			Double[] array = new Double[jarray.length()];
			for(int i = 0; i < jarray.length(); i++) {
				array[i] = jarray.getDouble(i);
			}
			return array;
		}
		catch(Exception e) {
			log.error("createDoubleArray: Error converting JSON ("+value+") to array of doubles", e);
		}
		return null;
	}
	/** Create a integer array from a JSON string */
	protected Integer[] createIntegerArray(String value) {
		try {
			JSONArray jarray = new JSONArray((String)value);
			Integer[] array = new Integer[jarray.length()];
			for(int i = 0; i < jarray.length(); i++) {
				array[i] = jarray.getInt(i);
			}
			return array;
		}
		catch(Exception e) {
			log.error("createIntegerArray: Error converting JSON ("+value+") to array of integers", e);
		}
		return null;
	}
	/** Create a string array from a JSON string */
	protected String[] createStringArray(String value) {
		try {
			JSONArray jarray = new JSONArray((String)value);
			String[] array = new String[jarray.length()];
			for(int i = 0; i < jarray.length(); i++) {
				array[i] = jarray.getString(i);
			}
			return array;
		}
		catch(Exception e) {
			log.error("createStringArray: Error converting JSON ("+value+") to array of integers", e);
		}
		return null;
	}
	protected int getKeySize(String keyName) {
		Object[] args = new Object[] {keyName};
		try {
			Integer count = (Integer)PythonCall.GET_KEY_SIZE.exec(args);
			return count.intValue();
		}
		catch(Exception e) {
			log.error("Error getting key size", e);
			return 0;
		}
	}
	
	protected Dataset createDataset(String value) {
		try {
			JSONArray rows = new JSONArray((String)value);
			int rowCount = rows.length();
			if(rowCount > 0) {
				JSONArray firstRow = rows.getJSONArray(0);
				int colCount = firstRow.length();
				Class<?>[] colTypes = new Class<?>[colCount];
				String[] colNames = new String[colCount];
				for(int i = 0; i < colCount; i++) {
					colTypes[i] = Double.class;
					colNames[i] = "";
				}
				DatasetBuilder builder = DatasetBuilder.newBuilder();
				builder.colTypes(colTypes);
				builder.colNames(colNames);
				for(int i = 0; i < rows.length(); i++) {
					Object[] values = new Object[colCount];
					JSONArray row = rows.getJSONArray(i); 
					for(int j = 0; j < colCount; j++) {
						values[j] = row.getDouble(j);
					}
					builder.addRow(values);
				}
				return builder.build();
			}
		}
		catch(Exception e) {
			log.error("Error converting JSON matrix to dataset", e);
		}
		return null;
	}

	/** Remove the UDT tag corresponding to this object. */
	public void deleteTag() {
		Object[] args = {provider, getTagPath()};
		try {
			PythonCall.DELETE_RECIPE_DATA.exec(args);
		} catch (JythonExecException e) {
			log.error("Recipe Data tag deletion failed", e);
		}		
	}
	
	/** Get the tag path of the UDT instance corresponding to this object. */
	public String getTagPath() {
		String path = getStepPath() + "/" + getKeyPath();
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
			if(value instanceof Dataset) {
				value = fromDataset((Dataset)value);
			}
			else if(value instanceof Boolean[]) {
				value = fromBooleanArray((Boolean[])value);
			}
			else if(value instanceof Date[]) {
				value = fromDateArray((Date[])value);
			}
			else if(value instanceof Double[]) {
				value = fromDoubleArray((Double[])value);
			}
			else if(value instanceof Integer[]) {
				value = fromIntegerArray((Integer[])value);
			}
			else if(value instanceof String[]) {
				value = fromStringArray((String[])value);
			}
			return value;
		} catch (JythonExecException e) {
			log.error("Recipe Data tag read failed", e);
			return null;
		}		
	}
	
	/** Convert a dataset to a JSON array-of-arrays */
	private String fromDataset(Dataset dataset) {
		try {
			JSONArray rows = new JSONArray();
			for(int i = 0; i < dataset.getRowCount(); i++) {
				JSONArray row = new JSONArray();
				for(int j = 0; j < dataset.getColumnCount(); j++) {
					Double value = (Double)dataset.getValueAt(i, j);
					row.put(value.doubleValue());
				}
				rows.put(row);
			}
			String json = rows.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting dataset to json", e);
			return null;
		}
	}
	/** Convert a boolean array to a JSON array */
	private String fromBooleanArray(Boolean[] array) {
		try {
			JSONArray cells = new JSONArray();
			for(int i = 0; i < array.length; i++) {
				Boolean flag = array[i];
				cells.put(flag.booleanValue());
			}
			String json = cells.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting boolean array to json", e);
			return null;
		}
	}
	/** Convert a date array to a JSON array */
	private String fromDateArray(Date[] array) {
		try {
			JSONArray cells = new JSONArray();
			for(int i = 0; i < array.length; i++) {
				Date date = array[i];
				cells.put(date.getTime());   // A long
			}
			String json = cells.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting date array to json", e);
			return null;
		}
	}
	/** Convert a double array to a JSON array */
	private String fromDoubleArray(Double[] array) {
		try {
			JSONArray cells = new JSONArray();
			for(int i = 0; i < array.length; i++) {
				Double dbl = array[i];
				cells.put(dbl.doubleValue());
			}
			String json = cells.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting double array to json", e);
			return null;
		}
	}
	/** Convert a integer array to a JSON array */
	private String fromIntegerArray(Integer[] array) {
		try {
			JSONArray cells = new JSONArray();
			for(int i = 0; i < array.length; i++) {
				Integer integer = array[i];
				cells.put(integer.intValue());
			}
			String json = cells.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting integer array to json", e);
			return null;
		}
	}
	/** Convert a string array to a JSON array */
	private String fromStringArray(String[] array) {
		try {
			JSONArray cells = new JSONArray();
			for(int i = 0; i < array.length; i++) {
				String val = array[i];
				cells.put(val);
			}
			String json = cells.toString();
			return json;
		}
		catch(Exception e) {
			log.error("Error converting string array to json", e);
			return null;
		}
	}

	/** Check if the UDT tag corresponding to this object exists. */
	public boolean tagExists() {
		String tagPath = getTagPath();
		Object[] args = {provider, tagPath};
		try {
			Boolean value = (Boolean)PythonCall.RECIPE_DATA_EXISTS.exec(args);
			if(value==null) return false;
			return value.booleanValue();
		} 
		catch (JythonExecException e) {
			log.error("Recipe Data tag existence check failed", e);
			return false;
		}				
	}
	
	/** Get string representation of the value, using empty string for null. */
	protected String getStringValue(BasicProperty<?> property) {
		Object valueOrNull = getValue(property);
		return valueOrNull != null ? valueOrNull.toString() : "";
	}
		
	/** Append the value of the given property, if it is part of this data. */
	protected void addLabelValue(BasicProperty<?> property, StringBuffer buf) {
		if(properties.contains(property)) {
			String strValue = getStringValue(property);
			buf.append(strValue);
			buf.append(" ");
		}
	}

	/** Get a label for this data, typically its name and some values of interest. */
	public String getLabel() {
		StringBuffer buf = new StringBuffer();
		addLabelValue(IlsProperty.KEY, buf);		
		addLabelValue(IlsProperty.CLASS, buf);		
		addLabelValue(IlsProperty.TAG_PATH, buf);		
		addLabelValue(IlsProperty.VALUE, buf);
		addLabelValue(IlsProperty.UNITS, buf);
		return buf.toString();
	}

	public static List<Data> fromStepProperties(PropertySet stepProperties) throws Exception {
		JSONObject assDataJson = stepProperties.get(IlsProperty.ASSOCIATED_DATA);
		log.info("In Data.fromStepProperties");
		if(assDataJson != null) {
			return fromAssociatedData(assDataJson);
		}
		else {
			return null;
		}
	}
	
	/** Check if the given access path, e.g. "myValue.value", is valid
	 *  for the given collection of recipe data. */
	public static boolean hasPath(List<Data> datas, String path) {
		boolean result = false;
		if( path!=null ) {
			String[] keys = path.split("\\.");
			for(Data data: datas) {
				if(keys.length > 0 && data.getKey().equals(keys[0])) {
					if(data.isGroup() ) {
						return hasPath(((Group)data).getChildren(), 
								path.substring(path.indexOf(".")+1, path.length()));
					}
					else {
						if(keys.length>1 && data.hasProperty(keys[1])) {
							return true;
						}
					}
				}
			}
		}
		return result;
	}

	public String validate() {
		return null;
	}
	
}
