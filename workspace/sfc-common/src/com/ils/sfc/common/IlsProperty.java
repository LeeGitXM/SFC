package com.ils.sfc.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class IlsProperty {
	private static final String EMPTY_MONITOR_DOWNLOAD_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_CONFIRM_CONTROLLERS_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_MANUAL_DATA_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_PV_MONITOR_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_WRITE_OUTPUT_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_REVIEW_DATA_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_COLLECT_DATA_CONFIG = "{\"errorHandling\": \"" + Constants.DEFAULT_VALUE + "\", \"rows\":[]}";
	
	private static final Map<Integer, PropertyInfo> infoById = new HashMap<Integer, PropertyInfo>();
	
	public static class PropertyInfo {
		boolean isSerializedObject = false;
		String[] choices;
		String label;
		
		public PropertyInfo(boolean isSerializedObject, String[] choices,
				String label) {
			super();
			this.isSerializedObject = isSerializedObject;
			this.choices = choices;
			this.label = label;
		}
	}
	
	// properties to omit from the editor
	public static final Set<String> ignoreProperties = new HashSet<String>();
	static {
		// Ignition step properties:
		ignoreProperties.add("location");
		ignoreProperties.add("location-adjustment");
		ignoreProperties.add("id");
		ignoreProperties.add("type");
		ignoreProperties.add("factory-id");
		ignoreProperties.add(Constants.ASSOCIATED_DATA);
		// ILS recipe data type:
		ignoreProperties.add("class");
		ignoreProperties.add("execution-mode");  // hide this for ILS encapsulations like Procedure
		ignoreProperties.add("passed-parameters");  // hide this for ILS encapsulations like Procedure
		ignoreProperties.add("return-parameters");  // hide this for ILS encapsulations like Procedure
	}

    public static final BasicProperty<Boolean> ACK_REQUIRED = createProperty(Constants.ACK_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> ADVICE = createProperty(Constants.ADVICE, String.class, "");
    public static final BasicProperty<String> ARRAY_KEY = createProperty(Constants.ARRAY_KEY, String.class, Constants.NONE);
    public static final BasicProperty<JSONObject> ASSOCIATED_DATA = new BasicProperty<JSONObject>(Constants.ASSOCIATED_DATA, JSONObject.class, null);
    //public static final BasicProperty<String> AUDIT_LEVEL = createProperty(IlsSfcNames.AUDIT_LEVEL, String.class, IlsSfcNames.AUDIT_LEVEL_CHOICES[0], IlsSfcNames.AUDIT_LEVEL_CHOICES);
    public static final BasicProperty<String> AUTO_MODE = createProperty(Constants.AUTO_MODE, String.class, Constants.AUTO_MODE_CHOICES[0], Constants.AUTO_MODE_CHOICES);
    public static final BasicProperty<String> BUTTON_KEY = createProperty(Constants.BUTTON_KEY, String.class, "");
    public static final BasicProperty<String> BUTTON_KEY_LOCATION = createProperty(Constants.BUTTON_KEY_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<String> BUTTON_LABEL = createProperty(Constants.BUTTON_LABEL, String.class, "");
    public static final BasicProperty<String> CALLBACK = createProperty(Constants.CALLBACK, String.class, "");
    public static final BasicProperty<String> CATEGORY = createProperty(Constants.CATEGORY, String.class, "");
    public static final BasicProperty<String> CHOICES_RECIPE_LOCATION = createProperty(Constants.CHOICES_RECIPE_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<String> CHOICES_KEY = createProperty(Constants.CHOICES_KEY, String.class, "");
    public static final BasicProperty<String> CLASS = createProperty(Constants.CLASS, String.class, "");
    public static final BasicProperty<String> COLLECT_DATA_CONFIG = createProperty(Constants.COLLECT_DATA_CONFIG, String.class, EMPTY_COLLECT_DATA_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> CONFIRM_CONTROLLERS_CONFIG = createProperty(Constants.CONFIRM_CONTROLLERS_CONFIG, String.class, EMPTY_CONFIRM_CONTROLLERS_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<Integer> COLUMNS = createProperty(Constants.COLUMNS, Integer.class, 0);
    public static final BasicProperty<String> COLUMN_KEY = createProperty(Constants.COLUMN_KEY, String.class, Constants.NONE);
    public static final BasicProperty<Boolean> COLUMN_KEYED = createProperty(Constants.COLUMN_KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> COMPUTER = createProperty(Constants.COMPUTER, String.class, Constants.COMPUTER_CHOICES[0], Constants.COMPUTER_CHOICES);
    public static final BasicProperty<String> DATA_ID = createProperty(Constants.DATA_ID, String.class, "");
    public static final BasicProperty<String> DATA_LOCATION = createProperty(Constants.DATA_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<Double> DELAY = createProperty(Constants.DELAY, Double.class, 0.);
    public static final BasicProperty<String> DELAY_UNIT = createProperty(Constants.DELAY_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);  
    public static final BasicProperty<String> DESCRIPTION = createProperty(Constants.DESCRIPTION, String.class, "");
    public static final BasicProperty<String> DIALOG = createProperty(Constants.DIALOG, String.class, "");
    public static final BasicProperty<String> DIRECTORY = createProperty(Constants.DIRECTORY, String.class, "");
    public static final BasicProperty<Boolean> DOWNLOAD = createProperty(Constants.DOWNLOAD, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> DOWNLOAD_STATUS = createProperty(Constants.DOWNLOAD_STATUS, String.class, "");
    public static final BasicProperty<Integer> ELEMENTS = createProperty(Constants.ELEMENTS, Integer.class, 0);
    public static final BasicProperty<Boolean> ENABLE_PAUSE = createProperty(Constants.ENABLE_PAUSE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_RESUME = createProperty(Constants.ENABLE_RESUME, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_CANCEL = createProperty(Constants.ENABLE_CANCEL, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Double> ERROR_CODE = createProperty(Constants.ERROR_CODE, Double.class, 0.);
    public static final BasicProperty<String> ERROR_TEXT = createProperty(Constants.ERROR_TEXT, String.class, "");
    public static final BasicProperty<String> EXTENSION = createProperty(Constants.EXTENSION, String.class, ".txt");
    public static final BasicProperty<String> FACTORY_ID = createProperty("factory-id", String.class, "");
    public static final BasicProperty<String> FETCH_MODE = createProperty(Constants.FETCH_MODE, String.class, Constants.FETCH_MODE_CHOICES[0], Constants.FETCH_MODE_CHOICES);
    public static final BasicProperty<String> FILENAME = createProperty(Constants.FILENAME, String.class, "");
    public static final BasicProperty<String> HELP = createProperty(Constants.HELP, String.class, "");
    public static final BasicProperty<Double> HIGH_LIMIT = createProperty(Constants.HIGH_LIMIT, Double.class, null);
    public static final BasicProperty<String> ID = createProperty(Constants.ID, String.class, null);
    public static final BasicProperty<String> JSON_LIST = createProperty(Constants.VALUE, String.class, "[0., 0.]");
    public static final BasicProperty<String> JSON_MATRIX = createProperty(Constants.VALUE, String.class, "[0., 0.][0., 0.]");
    public static final BasicProperty<String> JSON_OBJECT = createProperty(Constants.VALUE, String.class, "{}", true);
    public static final BasicProperty<String> KEY = createProperty(Constants.KEY, String.class, "");
    public static final BasicProperty<Boolean> KEYED = createProperty(Constants.KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> KEY_MODE = createProperty(Constants.KEY_MODE, String.class, "", Constants.KEY_MODE_CHOICES);
    public static final BasicProperty<String> LABEL = createProperty(Constants.LABEL, String.class, "");
    public static final BasicProperty<Double> LOW_LIMIT = createProperty(Constants.LOW_LIMIT, Double.class, null);
    public static final BasicProperty<Double> MAX_TIMING = createProperty(Constants.MAX_TIMING, Double.class, 0.);
    public static final BasicProperty<Double> MAXIMUM_VALUE = createProperty(Constants.MAXIMUM_VALUE, Double.class, 0.);
    public static final BasicProperty<String> MESSAGE = createProperty(Constants.MESSAGE, String.class, "");
    public static final BasicProperty<String> MESSAGE_QUEUE = createProperty(Constants.MESSAGE_QUEUE, String.class, Constants.DEFAULT_MESSAGE_QUEUE);
    public static final BasicProperty<String> METHOD = createProperty(Constants.METHOD, String.class, "");
    public static final BasicProperty<Double> MINIMUM_VALUE = createProperty(Constants.MINIMUM_VALUE, Double.class, 0.);
    public static final BasicProperty<String> MANUAL_DATA_CONFIG = createProperty(Constants.MANUAL_DATA_CONFIG, String.class, EMPTY_MONITOR_DOWNLOAD_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> MANUAL_DATA_POSTING_METHOD = createProperty(Constants.POSTING_METHOD, String.class, "ils.sfc.client.windows.manualDataEntry.defaultPostingMethod");
    public static final BasicProperty<String> MANUAL_DATA_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_MANUAL_DATA_WINDOW, false);
    public static final BasicProperty<String> MONITOR_DOWNLOADS_CONFIG = createProperty(Constants.MONITOR_DOWNLOADS_CONFIG, String.class, EMPTY_MANUAL_DATA_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> MONITOR_DOWNLOADS_POSTING_METHOD = createProperty(Constants.POSTING_METHOD, String.class, "ils.sfc.client.windows.manualDataEntry.defaultPostingMethod");
    public static final BasicProperty<String> MONITOR_DOWNLOADS_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_MANUAL_DATA_WINDOW, false);
	public static final BasicProperty<String> NAME = new BasicProperty<String>(Constants.NAME, String.class);
    public static final BasicProperty<Object> NULLABLE_VALUE = createProperty(Constants.VALUE, Object.class, null);
    public static final BasicProperty<Object> OUTPUT_VALUE_TYPE = createProperty(Constants.VALUE_TYPE, Object.class, Constants.OUTPUT_VALUE_TYPE_CHOICES[0], Constants.OUTPUT_VALUE_TYPE_CHOICES);
    public static final BasicProperty<String> POSITION = createProperty(Constants.POSITION, String.class, Constants.POSITION_CHOICES[0], Constants.POSITION_CHOICES);
    public static final BasicProperty<Boolean> POST_NOTIFICATION = createProperty(Constants.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<Boolean> POST_TO_QUEUE = createProperty(Constants.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> PRIMARY_REVIEW_DATA = createProperty(Constants.PRIMARY_REVIEW_DATA, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "primary data");
    public static final BasicProperty<String> PRIMARY_REVIEW_DATA_WITH_ADVICE = createProperty(Constants.PRIMARY_REVIEW_DATA_WITH_ADVICE, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "primary data");
    public static final BasicProperty<String> PRIMARY_TAB_LABEL = createProperty(Constants.PRIMARY_TAB_LABEL, String.class, "Primary");
    public static final BasicProperty<String> PROMPT = createProperty(Constants.PROMPT, String.class, "");
    public static final BasicProperty<Boolean> PRINT_FILE = createProperty(Constants.PRINT_FILE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> PRIORITY = createProperty(Constants.PRIORITY, String.class, Constants.PRIORITY_CHOICES[0], Constants.PRIORITY_CHOICES);
    public static final BasicProperty<String> PV_MONITOR_STATUS = createProperty(Constants.PV_MONITOR_STATUS, String.class, "");
    public static final BasicProperty<String> PV_MONITOR_ACTIVE = createProperty(Constants.PV_MONITOR_ACTIVE, String.class, "");
    public static final BasicProperty<Double> PV_VALUE = createProperty(Constants.PV_VALUE, Double.class, 0.);
    public static final BasicProperty<String> PV_MONITOR_CONFIG = createProperty(Constants.PV_MONITOR_CONFIG, String.class, EMPTY_PV_MONITOR_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> QUEUE = createProperty(Constants.QUEUE, String.class, "");
    public static final BasicProperty<Double> RAMP_TIME = createProperty(Constants.RAMP_TIME, Double.class, 5.);
    public static final BasicProperty<String> RECIPE_LOCATION = createProperty(Constants.RECIPE_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<Boolean> REQUIRE_INPUTS = createProperty(Constants.REQUIRE_INPUTS, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> RESULTS_MODE = createProperty(Constants.RESULTS_MODE, String.class, Constants.RESULTS_MODE_CHOICES[0], Constants.RESULTS_MODE_CHOICES);
    public static final BasicProperty<String> REVIEW_DATA_POSTING_METHOD = createProperty(Constants.POSTING_METHOD, String.class, "ils.sfc.client.windows.reviewData.defaultPostingMethod");
    public static final BasicProperty<String> REVIEW_DATA_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_REVIEW_DATA_WINDOW, false);
    public static final BasicProperty<String> REVIEW_FLOWS_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_REVIEW_FLOWS_WINDOW, false);
    public static final BasicProperty<String> REVIEW_FLOWS_POSTING_METHOD = createProperty(Constants.POSTING_METHOD, String.class, "");
    public static final BasicProperty<Integer> ROWS = createProperty(Constants.ROWS, Integer.class, 0);
    public static final BasicProperty<String> ROW_KEY = createProperty(Constants.ROW_KEY, String.class, Constants.NONE);
    public static final BasicProperty<Boolean> ROW_KEYED = createProperty(Constants.ROW_KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> SECURITY = createProperty(Constants.SECURITY, String.class, Constants.SECURITY_CHOICES[0], Constants.SECURITY_CHOICES);
    public static final BasicProperty<String> SECONDARY_REVIEW_DATA = createProperty(Constants.SECONDARY_REVIEW_DATA, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final BasicProperty<String> SECONDARY_REVIEW_DATA_WITH_ADVICE = createProperty(Constants.SECONDARY_REVIEW_DATA_WITH_ADVICE, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final BasicProperty<String> SECONDARY_TAB_LABEL = createProperty(Constants.SECONDARY_TAB_LABEL, String.class, "Secondary");
    public static final BasicProperty<Boolean> SHOW_PRINT_DIALOG = createProperty(Constants.SHOW_PRINT_DIALOG, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> SQL = createProperty(Constants.SQL, String.class, "");
    public static final BasicProperty<Double> STEP_TIME = createProperty(Constants.STEP_TIME, Double.class, 0.);
    public static final BasicProperty<String> STEP_TIMESTAMP = createProperty(Constants.STEP_TIMESTAMP, String.class, "");
    public static final BasicProperty<String> TAG_PATH = createProperty(Constants.TAG_PATH, String.class, "");
    public static final BasicProperty<String> TIME_DELAY_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.TIME_DELAY_STRATEGY_CHOICES[0], Constants.TIME_DELAY_STRATEGY_CHOICES);
    public static final BasicProperty<String> TIME_LIMIT_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.TIME_LIMIT_STRATEGY_CHOICES[0], Constants.TIME_LIMIT_STRATEGY_CHOICES);
    public static final BasicProperty<Double> TIMING = createProperty(Constants.TIMING, Double.class, 0.);
    public static final BasicProperty<String> RECIPE_STATIC_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.RECIPE_STATIC_STRATEGY_CHOICES[0], Constants.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final BasicProperty<Double> SCALE = createProperty(Constants.SCALE, Double.class, .5);
    public static final BasicProperty<Double> TARGET_VALUE = createProperty(Constants.TARGET_VALUE, Double.class, 0.);
    public static final BasicProperty<Integer> TIMEOUT = createProperty(Constants.TIMEOUT, Integer.class, -1);
    public static final BasicProperty<String> TIMEOUT_UNIT = createProperty(Constants.TIMEOUT_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);
    public static final BasicProperty<Boolean> TIMER_CLEAR = createProperty(Constants.TIMER_CLEAR, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> TIMER_KEY = createProperty(Constants.TIMER_KEY, String.class, "");
    public static final BasicProperty<String> TIMER_LOCATION = createProperty(Constants.TIMER_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<Boolean> TIMER_SET = createProperty(Constants.TIMER_SET, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<Boolean> TIMESTAMP = createProperty(Constants.TIMESTAMP, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> TYPE = createProperty(Constants.TYPE, String.class, "");
    public static final BasicProperty<String> UNITS = createProperty(Constants.UNITS, String.class, "");
    public static final BasicProperty<Double> UPDATE_FREQUENCY = createProperty(Constants.UPDATE_FREQUENCY, Double.class, 10.);
    public static final BasicProperty<Object> NON_NULL_VALUE = createProperty(Constants.VALUE, Object.class, "");
    public static final BasicProperty<Boolean> VERBOSE = createProperty(Constants.VERBOSE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> VIEW_FILE = createProperty(Constants.VIEW_FILE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> WINDOW = createProperty(Constants.WINDOW, String.class, "");
    public static final BasicProperty<String> WINDOW_TITLE = createProperty(Constants.WINDOW_TITLE, String.class, "");
    public static final BasicProperty<Boolean> WRITE_CONFIRM = createProperty(Constants.WRITE_CONFIRM, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> WRITE_CONFIRMED = createProperty(Constants.WRITE_CONFIRMED, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> WRITE_OUTPUT_CONFIG = createProperty(Constants.WRITE_OUTPUT_CONFIG, String.class, EMPTY_WRITE_OUTPUT_CONFIG, true, Constants.CONFIG);
   
 // These are the names of toolkit properties that are to be stored in HSQLdb
 	public static final String TOOLKIT_PROPERTY_DATABASE            = "Database";           // Production database
 	public static final String TOOLKIT_PROPERTY_ISOLATION_DATABASE  = "SecondaryDatabase";  // Database when in isolation
 	public static final String TOOLKIT_PROPERTY_PROVIDER            = "Provider";           // Production tag provider
 	public static final String TOOLKIT_PROPERTY_ISOLATION_PROVIDER  = "SecondaryProvider";  // Tag provider when in isolation
 	public static final String TOOLKIT_PROPERTY_ISOLATION_TIME      = "SecondaryTimeFactor";// Time speedup when in isolation

	public static <C> BasicProperty<C> createProperty(String name, Class<C> clazz, C defaultValue) {
		return createProperty(name, clazz, defaultValue, false, null, null);
	}

	public static <C> BasicProperty<C> createProperty(String name, Class<C> clazz, C defaultValue, boolean isSerializedObject, String label) {
		return createProperty(name, clazz, defaultValue, isSerializedObject, null, label);
	}

	public static <C> BasicProperty<C> createProperty(String name, Class<C> clazz, C defaultValue, boolean isSerializedObject) {
		return createProperty(name, clazz, defaultValue, isSerializedObject, null, null);
	}

	public static <C> BasicProperty<C> createProperty(String name, Class<C> clazz, C defaultValue, String[] choices) {
		return createProperty(name, clazz, defaultValue, false, choices, null);
	}

	private static Integer getPropertyId(Property<?> prop) {
		return System.identityHashCode(prop);
	}
	
	private static <C> BasicProperty<C> createProperty(String name, Class<C> clazz, C defaultValue,
		boolean isSerializedObject, String[] choices, String label) {
		BasicProperty<C> prop = new BasicProperty<C>(name, clazz, defaultValue);
		PropertyInfo info = new PropertyInfo(isSerializedObject, null, null);		
		infoById.put(getPropertyId(prop), info);
		return prop;
	}

	public static List<String> getAllPropertyNames() throws Exception {
		List<String> allPropertyNames = new ArrayList<String>();
		Field[] fields = IlsProperty.class.getFields();
		for(Field field: fields) {
			int modifiers = field.getModifiers();
			if(field.getType() == IlsProperty.class && Modifier.isStatic(modifiers)) {
				BasicProperty<?> property = (BasicProperty<?>)field.get(null);
				allPropertyNames.add(property.getName());
			}
		}
		return allPropertyNames;
	}
		
	public static Object parsePropertyValue(Property<?> property, String stringValue) throws ParseException {
		if( IlsSfcCommonUtils.isEmpty(stringValue)) {
			return property.getDefaultValue();
		}
		if(property.getType() == Integer.class) {
			return parseInt(stringValue);
		}
		else if(property.getType() == Double.class) {
			return parseDouble(stringValue);
		}
		else if(property.getType() == Boolean.class) {
			return parseBoolean(stringValue);
		}
		else if(property == JSON_OBJECT) {
			// validate that the string is a valid JSON list
			try {
				new JSONObject(stringValue);
			}
			catch(JSONException e) {
				throw new ParseException("bad object format: " + stringValue + "; should be something like " + JSON_LIST.getDefaultValue(), 0);				
			}
			return stringValue;
		}
		else if(property == JSON_LIST) {
			// validate that the string is a valid JSON list
			try {
				new JSONArray(stringValue);
			}
			catch(JSONException e) {
				throw new ParseException("bad array format: " + stringValue + "; should be something like " + JSON_LIST.getDefaultValue(), 0);				
			}
			return stringValue;
		}
		else if(property == JSON_MATRIX) {
			// validate that the string is a valid JSON matrix			
			try {
				JSONArray rows = new JSONArray(stringValue);
				for(int i = 0; i < rows.length(); i++) {
					rows.getJSONArray(i); 
				}
			}
			catch(JSONException e) {
				throw new ParseException("bad matrix format: " + stringValue + "; should be something like " + JSON_MATRIX.getDefaultValue(), 0);				
			}
			return stringValue;
		}
		else if(property.getType() == String.class) {
			return stringValue;
		}
		else if(property.getType() == Object.class) {
			return parseObjectValue(stringValue, null);
		}
		else {
			return stringValue;
		}
	}

	public static Boolean parseBoolean(String stringValue) throws ParseException {
		if(IlsSfcCommonUtils.isEmpty(stringValue)) return null;
		try {
			return Boolean.valueOf(stringValue);
		}
		catch(NumberFormatException e) {
			throw new ParseException("bad boolean format: " + stringValue, 0);
		}
	}

	public static Double parseDouble(String stringValue) throws ParseException {
		if(IlsSfcCommonUtils.isEmpty(stringValue)) return null;
		try {
			return Double.parseDouble(stringValue);
		}
		catch(NumberFormatException e) {
			throw new ParseException("bad float format: " + stringValue, 0);
		}
	}

	public static Integer parseInt(String stringValue) throws ParseException {
		if(IlsSfcCommonUtils.isEmpty(stringValue)) return null;
		try {
			return Integer.parseInt(stringValue);
		}
		catch(NumberFormatException e) {
			throw new ParseException("bad integer format: " + stringValue, 0);
		}
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

	public static String[] getChoices(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		return info != null ? info.choices : null;
	}
	
	public static boolean isSerializedObject(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		return info != null ? info.isSerializedObject : false;
	}
	
	/** Get the user-visible display string. Usually this is a munged version of the name,
	 *  but sometimes a property has an explicit label. 
	 */
	public static String getLabel(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		if(info != null && info.label != null) {
			return info.label;
		}
		else {
			return labelize(property.getName());
		}
	}
	
	/** Create a human-friendly label from a camelcase name. */
	public static String labelize(String string) {
		StringBuilder buf = new StringBuilder();
		String camelCaseName = string;
		for(int i = 0; i < camelCaseName.length(); i++) {
			char c = camelCaseName.charAt(i);
			if(Character.isUpperCase(c)) {
				buf.append(' ');
			}
			buf.append(Character.toLowerCase(c));
		}
		return buf.toString();
	}
}
