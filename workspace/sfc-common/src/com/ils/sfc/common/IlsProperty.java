package com.ils.sfc.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

@SuppressWarnings("serial")
public class IlsProperty<T> extends BasicProperty<T> implements java.io.Serializable {
	private static LoggerEx logger = LogUtil.getLogger(IlsProperty.class.getName());
	private int sortOrder;
	private boolean isSerializedObject;
	private String[] choices;
	private String label;
	private static final String EMPTY_MONITOR_DOWNLOAD_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_CONFIRM_CONTROLLERS_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_PV_MONITOR_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_WRITE_OUTPUT_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_REVIEW_DATA_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_COLLECT_DATA_CONFIG = "{\"errorHandling\": \"" + Constants.DEFAULT_VALUE + "\", \"rows\":[]}";
	
	// properties to omit from the editor
	public static final Set<String> ignoreProperties = new HashSet<String>();
	static {
		// Ignition step properties:
		ignoreProperties.add("location");
		ignoreProperties.add("location-adjustment");
		ignoreProperties.add("id");
		ignoreProperties.add("type");
		ignoreProperties.add("factory-id");
		ignoreProperties.add("associated-data");
		// ILS recipe data type:
		ignoreProperties.add("class");
		ignoreProperties.add("execution-mode");  // hide this for ILS encapsulations like Procedure
		ignoreProperties.add("passed-parameters");  // hide this for ILS encapsulations like Procedure
		ignoreProperties.add("return-parameters");  // hide this for ILS encapsulations like Procedure
	}

    public static final IlsProperty<Boolean> ACK_REQUIRED = new IlsProperty<Boolean>(Constants.ACK_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> ADVICE = new IlsProperty<String>(Constants.ADVICE, String.class, "");
    public static final IlsProperty<String> ARRAY_KEY = new IlsProperty<String>(Constants.ARRAY_KEY, String.class, Constants.NONE);
    //public static final IlsProperty<String> AUDIT_LEVEL = new IlsProperty<String>(IlsSfcNames.AUDIT_LEVEL, String.class, IlsSfcNames.AUDIT_LEVEL_CHOICES[0], IlsSfcNames.AUDIT_LEVEL_CHOICES);
    public static final IlsProperty<String> AUTO_MODE = new IlsProperty<String>(Constants.AUTO_MODE, String.class, Constants.AUTO_MODE_CHOICES[0], Constants.AUTO_MODE_CHOICES);
    public static final IlsProperty<String> BUTTON_KEY = new IlsProperty<String>(Constants.BUTTON_KEY, String.class, "");
    public static final IlsProperty<String> BUTTON_KEY_LOCATION = new IlsProperty<String>(Constants.BUTTON_KEY_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> BUTTON_LABEL = new IlsProperty<String>(Constants.BUTTON_LABEL, String.class, "");
    public static final IlsProperty<String> CALLBACK = new IlsProperty<String>(Constants.CALLBACK, String.class, "");
    public static final IlsProperty<String> CATEGORY = new IlsProperty<String>(Constants.CATEGORY, String.class, "");
    public static final IlsProperty<String> CHOICES_RECIPE_LOCATION = new IlsProperty<String>(Constants.CHOICES_RECIPE_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> CHOICES_KEY = new IlsProperty<String>(Constants.CHOICES_KEY, String.class, "");
    public static final IlsProperty<String> CLASS = new IlsProperty<String>(Constants.CLASS, String.class, "");
    public static final IlsProperty<String> COLLECT_DATA_CONFIG = new IlsProperty<String>(Constants.COLLECT_DATA_CONFIG, String.class, EMPTY_COLLECT_DATA_CONFIG, true);
    public static final IlsProperty<String> CONFIRM_CONTROLLERS_CONFIG = new IlsProperty<String>(Constants.CONFIRM_CONTROLLERS_CONFIG, String.class, EMPTY_CONFIRM_CONTROLLERS_CONFIG, true);
    public static final IlsProperty<Integer> COLUMNS = new IlsProperty<Integer>(Constants.COLUMNS, Integer.class, 0);
    public static final IlsProperty<String> COLUMN_KEY = new IlsProperty<String>(Constants.COLUMN_KEY, String.class, Constants.NONE);
    public static final IlsProperty<Boolean> COLUMN_KEYED = new IlsProperty<Boolean>(Constants.COLUMN_KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> COMPUTER = new IlsProperty<String>(Constants.COMPUTER, String.class, Constants.COMPUTER_CHOICES[0], Constants.COMPUTER_CHOICES);
    public static final IlsProperty<Double> DELAY = new IlsProperty<Double>(Constants.DELAY, Double.class, 0.);
    public static final IlsProperty<String> DELAY_UNIT = new IlsProperty<String>(Constants.DELAY_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);  
    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(Constants.DESCRIPTION, String.class, "");
    public static final IlsProperty<String> DIALOG = new IlsProperty<String>(Constants.DIALOG, String.class, "");
    public static final IlsProperty<String> DIRECTORY = new IlsProperty<String>(Constants.DIRECTORY, String.class, "");
    public static final IlsProperty<Boolean> DOWNLOAD = new IlsProperty<Boolean>(Constants.DOWNLOAD, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> DOWNLOAD_STATUS = new IlsProperty<String>(Constants.DOWNLOAD_STATUS, String.class, "");
    public static final IlsProperty<Integer> ELEMENTS = new IlsProperty<Integer>(Constants.ELEMENTS, Integer.class, 0);
    public static final IlsProperty<Boolean> ENABLE_PAUSE = new IlsProperty<Boolean>(Constants.ENABLE_PAUSE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> ENABLE_RESUME = new IlsProperty<Boolean>(Constants.ENABLE_RESUME, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> ENABLE_CANCEL = new IlsProperty<Boolean>(Constants.ENABLE_CANCEL, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Double> ERROR_CODE = new IlsProperty<Double>(Constants.ERROR_CODE, Double.class, 0.);
    public static final IlsProperty<String> ERROR_TEXT = new IlsProperty<String>(Constants.ERROR_TEXT, String.class, "");
    public static final IlsProperty<String> EXTENSION = new IlsProperty<String>(Constants.EXTENSION, String.class, ".txt");
    public static final IlsProperty<String> FACTORY_ID = new IlsProperty<String>("factory-id", String.class, "");
    public static final IlsProperty<String> FETCH_MODE = new IlsProperty<String>(Constants.FETCH_MODE, String.class, Constants.FETCH_MODE_CHOICES[0], Constants.FETCH_MODE_CHOICES);
    public static final IlsProperty<String> FILENAME = new IlsProperty<String>(Constants.FILENAME, String.class, "");
    public static final IlsProperty<String> GUI_UNITS = new IlsProperty<String>(Constants.FILENAME, String.class, "");
    public static final IlsProperty<String> GUI_LABEL = new IlsProperty<String>(Constants.GUI_LABEL, String.class, "");
    public static final IlsProperty<String> HELP = new IlsProperty<String>(Constants.HELP, String.class, "");
    public static final IlsProperty<Double> HIGH_LIMIT = new IlsProperty<Double>(Constants.HIGH_LIMIT, Double.class, null);
    public static final IlsProperty<String> JSON_LIST = new IlsProperty<String>(Constants.VALUE, String.class, "[0., 0.]");
    public static final IlsProperty<String> JSON_MATRIX = new IlsProperty<String>(Constants.VALUE, String.class, "[0., 0.][0., 0.]");
    public static final IlsProperty<String> JSON_OBJECT = new IlsProperty<String>(Constants.VALUE, String.class, "{}", true);
    public static final IlsProperty<String> KEY = new IlsProperty<String>(Constants.KEY, String.class, "");
    public static final IlsProperty<Boolean> KEYED = new IlsProperty<Boolean>(Constants.KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> KEY_MODE = new IlsProperty<String>(Constants.KEY_MODE, String.class, "", Constants.KEY_MODE_CHOICES);
    public static final IlsProperty<String> LABEL = new IlsProperty<String>(Constants.LABEL, String.class, "");
    public static final IlsProperty<Double> LOW_LIMIT = new IlsProperty<Double>(Constants.LOW_LIMIT, Double.class, null);
    public static final IlsProperty<Double> MAX_TIMING = new IlsProperty<Double>(Constants.MAX_TIMING, Double.class, 0.);
    public static final IlsProperty<Double> MAXIMUM_VALUE = new IlsProperty<Double>(Constants.MAXIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(Constants.MESSAGE, String.class, "");
    public static final IlsProperty<String> MESSAGE_QUEUE = new IlsProperty<String>(Constants.MESSAGE_QUEUE, String.class, Constants.DEFAULT_MESSAGE_QUEUE) {
    	@Override
    	public String[] getChoices() {
    		try {
    			Object[] args = {null};
				return PythonCall.toArray(PythonCall.GET_QUEUE_NAMES.exec(args));
			} catch (JythonExecException e) {
				logger.error("Error getting message queue names", e);
				return null;
			}
    	}
    };
    public static final IlsProperty<String> METHOD = new IlsProperty<String>(Constants.METHOD, String.class, "");
    public static final IlsProperty<Double> MINIMUM_VALUE = new IlsProperty<Double>(Constants.MINIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> MONITOR_DOWNLOADS_CONFIG = new IlsProperty<String>(Constants.MONITOR_DOWNLOADS_CONFIG, String.class, EMPTY_MONITOR_DOWNLOAD_CONFIG, true);
    public static final IlsProperty<String> MONITOR_DOWNLOADS_POSTING_METHOD = new IlsProperty<String>(Constants.POSTING_METHOD, String.class, "ils.sfc.client.windows.monitorDownload.defaultPostingMethod");
    public static final IlsProperty<String> MONITOR_DOWNLOADS_WINDOW = new IlsProperty<String>(Constants.WINDOW, String.class,  "SFC/MonitorDownload", false);
	public static final BasicProperty<String> NAME = new BasicProperty<String>(Constants.NAME, String.class);
    public static final IlsProperty<String> POSITION = new IlsProperty<String>(Constants.POSITION, String.class, Constants.POSITION_CHOICES[0], Constants.POSITION_CHOICES);
    public static final IlsProperty<Boolean> POST_NOTIFICATION = new IlsProperty<Boolean>(Constants.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Boolean> POST_TO_QUEUE = new IlsProperty<Boolean>(Constants.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> PRIMARY_REVIEW_DATA = new IlsProperty<String>(Constants.PRIMARY_REVIEW_DATA, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "primary data");
    public static final IlsProperty<String> PRIMARY_REVIEW_DATA_WITH_ADVICE = new IlsProperty<String>(Constants.PRIMARY_REVIEW_DATA_WITH_ADVICE, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "primary data");
    public static final IlsProperty<String> PRIMARY_TAB_LABEL = new IlsProperty<String>(Constants.PRIMARY_TAB_LABEL, String.class, "Primary");
    public static final IlsProperty<String> PROMPT = new IlsProperty<String>(Constants.PROMPT, String.class, "");
    public static final IlsProperty<Boolean> PRINT_FILE = new IlsProperty<Boolean>(Constants.PRINT_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> PRIORITY = new IlsProperty<String>(Constants.PRIORITY, String.class, Constants.PRIORITY_CHOICES[0], Constants.PRIORITY_CHOICES);
    public static final IlsProperty<String> PV_MONITOR_STATUS = new IlsProperty<String>(Constants.PV_MONITOR_STATUS, String.class, "");
    public static final IlsProperty<String> PV_MONITOR_ACTIVE = new IlsProperty<String>(Constants.PV_MONITOR_ACTIVE, String.class, "");
    public static final IlsProperty<Double> PV_VALUE = new IlsProperty<Double>(Constants.PV_VALUE, Double.class, 0.);
    public static final IlsProperty<String> PV_MONITOR_CONFIG = new IlsProperty<String>(Constants.PV_MONITOR_CONFIG, String.class, EMPTY_PV_MONITOR_CONFIG, true);
    public static final IlsProperty<String> QUEUE = new IlsProperty<String>(Constants.QUEUE, String.class, "");
    public static final IlsProperty<Double> RAMP_TIME = new IlsProperty<Double>(Constants.RAMP_TIME, Double.class, 5.);
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(Constants.RECIPE_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> RESULTS_MODE = new IlsProperty<String>(Constants.RESULTS_MODE, String.class, Constants.RESULTS_MODE_CHOICES[0], Constants.RESULTS_MODE_CHOICES);
    public static final IlsProperty<String> REVIEW_DATA_POSTING_METHOD = new IlsProperty<String>(Constants.POSTING_METHOD, String.class, "ils.sfc.client.windows.reviewData.defaultPostingMethod");
    public static final IlsProperty<String> REVIEW_DATA_WINDOW = new IlsProperty<String>(Constants.WINDOW, String.class,  "SFC/ReviewData", false);
    public static final IlsProperty<String> REVIEW_FLOWS_WINDOW = new IlsProperty<String>(Constants.WINDOW, String.class,  "SFC/ReviewFlows", false);
    public static final IlsProperty<String> REVIEW_FLOWS_POSTING_METHOD = new IlsProperty<String>(Constants.POSTING_METHOD, String.class, "");
    public static final IlsProperty<Integer> ROWS = new IlsProperty<Integer>(Constants.ROWS, Integer.class, 0);
    public static final IlsProperty<String> ROW_KEY = new IlsProperty<String>(Constants.ROW_KEY, String.class, Constants.NONE);
    public static final IlsProperty<Boolean> ROW_KEYED = new IlsProperty<Boolean>(Constants.ROW_KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> S88_LEVEL = new IlsProperty<String>(Constants.S88_LEVEL, String.class, Constants.S88_LEVEL_CHOICES[0], Constants.S88_LEVEL_CHOICES);
    public static final IlsProperty<String> SECURITY = new IlsProperty<String>(Constants.SECURITY, String.class, Constants.SECURITY_CHOICES[0], Constants.SECURITY_CHOICES);
    public static final IlsProperty<String> SECONDARY_REVIEW_DATA = new IlsProperty<String>(Constants.SECONDARY_REVIEW_DATA, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final IlsProperty<String> SECONDARY_REVIEW_DATA_WITH_ADVICE = new IlsProperty<String>(Constants.SECONDARY_REVIEW_DATA_WITH_ADVICE, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final IlsProperty<String> SECONDARY_TAB_LABEL = new IlsProperty<String>(Constants.SECONDARY_TAB_LABEL, String.class, "Secondary");
    public static final IlsProperty<Boolean> SHOW_PRINT_DIALOG = new IlsProperty<Boolean>(Constants.SHOW_PRINT_DIALOG, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> SQL = new IlsProperty<String>(Constants.SQL, String.class, "");
    public static final IlsProperty<Double> STEP_TIME = new IlsProperty<Double>(Constants.STEP_TIME, Double.class, 0.);
    public static final IlsProperty<String> STEP_TIMESTAMP = new IlsProperty<String>(Constants.STEP_TIMESTAMP, String.class, "");
    public static final IlsProperty<String> TAG_PATH = new IlsProperty<String>(Constants.TAG_PATH, String.class, "");
    public static final IlsProperty<String> TIME_DELAY_STRATEGY = new IlsProperty<String>(Constants.STRATEGY, String.class, Constants.TIME_DELAY_STRATEGY_CHOICES[0], Constants.TIME_DELAY_STRATEGY_CHOICES);
    public static final IlsProperty<String> TIME_LIMIT_STRATEGY = new IlsProperty<String>(Constants.STRATEGY, String.class, Constants.TIME_LIMIT_STRATEGY_CHOICES[0], Constants.TIME_LIMIT_STRATEGY_CHOICES);
    public static final IlsProperty<Double> TIMING = new IlsProperty<Double>(Constants.TIMING, Double.class, 0.);
    public static final IlsProperty<String> RECIPE_STATIC_STRATEGY = new IlsProperty<String>(Constants.STRATEGY, String.class, Constants.RECIPE_STATIC_STRATEGY_CHOICES[0], Constants.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final IlsProperty<Double> SCALE = new IlsProperty<Double>(Constants.SCALE, Double.class, .5);
    public static final IlsProperty<Double> TARGET_VALUE = new IlsProperty<Double>(Constants.TARGET_VALUE, Double.class, 0.);
    public static final IlsProperty<Integer> TIMEOUT = new IlsProperty<Integer>(Constants.TIMEOUT, Integer.class, 0);
    public static final IlsProperty<String> TIMEOUT_UNIT = new IlsProperty<String>(Constants.TIMEOUT_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);
    public static final IlsProperty<Boolean> TIMER_CLEAR = new IlsProperty<Boolean>(Constants.TIMER_CLEAR, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> TIMER_KEY = new IlsProperty<String>(Constants.TIMER_KEY, String.class, "");
    public static final IlsProperty<String> TIMER_LOCATION = new IlsProperty<String>(Constants.TIMER_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<Boolean> TIMER_SET = new IlsProperty<Boolean>(Constants.TIMER_SET, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Boolean> TIMESTAMP = new IlsProperty<Boolean>(Constants.TIMESTAMP, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> TYPE = new IlsProperty<String>(Constants.TYPE, String.class, "");
    public static final IlsProperty<String> UNITS = new IlsProperty<String>(Constants.UNITS, String.class, "");
    public static final IlsProperty<Double> UPDATE_FREQUENCY = new IlsProperty<Double>(Constants.UPDATE_FREQUENCY, Double.class, 10.);
    public static final IlsProperty<Object> VALUE = new IlsProperty<Object>(Constants.VALUE, Object.class, null);
    public static final IlsProperty<Boolean> VERBOSE = new IlsProperty<Boolean>(Constants.VERBOSE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> VIEW_FILE = new IlsProperty<Boolean>(Constants.VIEW_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> WINDOW = new IlsProperty<String>(Constants.WINDOW, String.class, "");
    public static final IlsProperty<String> WINDOW_TITLE = new IlsProperty<String>(Constants.WINDOW_TITLE, String.class, "");
    public static final IlsProperty<Boolean> WRITE_CONFIRM = new IlsProperty<Boolean>(Constants.WRITE_CONFIRM, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> WRITE_CONFIRMED = new IlsProperty<Boolean>(Constants.WRITE_CONFIRMED, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> WRITE_OUTPUT_CONFIG = new IlsProperty<String>(Constants.WRITE_OUTPUT_CONFIG, String.class, EMPTY_WRITE_OUTPUT_CONFIG, true);
   
 // These are the names of toolkit properties that are to be stored in HSQLdb
 	public static final String TOOLKIT_PROPERTY_DATABASE            = "Database";           // Production database
 	public static final String TOOLKIT_PROPERTY_ISOLATION_DATABASE  = "SecondaryDatabase";  // Database when in isolation
 	public static final String TOOLKIT_PROPERTY_PROVIDER            = "Provider";           // Production tag provider
 	public static final String TOOLKIT_PROPERTY_ISOLATION_PROVIDER  = "SecondaryProvider";  // Tag provider when in isolation
 	public static final String TOOLKIT_PROPERTY_ISOLATION_TIME      = "SecondaryTimeFactor";// Time speedup when in isolation
 	
    public IlsProperty() {}
    
	public IlsProperty(String name, Class<T> clazz, T defaultValue) {
		super(name, clazz, defaultValue);
		//if(defaultValue == null) {
		//	throw new IllegalArgumentException("Null not allowed as a default value for JSON");
		//}
	}


	public IlsProperty(String name, Class<T> clazz, T defaultValue, boolean isSerializedObject, String label) {
		this(name, clazz, defaultValue);
		this.isSerializedObject = isSerializedObject;
		this.label = label;
	}
	
	public IlsProperty(String name, Class<T> clazz, T defaultValue, boolean isSerializedObject) {
		this(name, clazz, defaultValue, isSerializedObject, null);
	}

	public IlsProperty(String name, Class<T> clazz, T defaultValue, String[] choices) {
		this(name, clazz, defaultValue);
		this.choices = choices;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean isSerializedObject() {
		return isSerializedObject;
	}

	public String[] getChoices() {
		return choices;
	}
	
	public static List<String> getAllPropertyNames() throws Exception {
		List<String> allPropertyNames = new ArrayList<String>();
		Field[] fields = IlsProperty.class.getFields();
		for(Field field: fields) {
			int modifiers = field.getModifiers();
			if(field.getType() == IlsProperty.class && Modifier.isStatic(modifiers)) {
				IlsProperty<?> property = (IlsProperty<?>)field.get(null);
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
		try {
			return Boolean.valueOf(stringValue);
		}
		catch(NumberFormatException e) {
			throw new ParseException("bad boolean format: " + stringValue, 0);
		}
	}

	public static Double parseDouble(String stringValue) throws ParseException {
		try {
			return Double.parseDouble(stringValue);
		}
		catch(NumberFormatException e) {
			throw new ParseException("bad float format: " + stringValue, 0);
		}
	}

	public static Integer parseInt(String stringValue) throws ParseException {
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

	public static boolean isSerializedObject(Property<?> property) {
		return property instanceof IlsProperty && ((IlsProperty<?>)property).isSerializedObject();
	}
	
	/** Get the user-visible display string. Usually this is a munged version of the name,
	 *  but sometimes a property has an explicit label. 
	 */
	public String getLabel() {
		if(label == null) {
			label = labelize(getName());
		}
		return label;
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
