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

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;

@SuppressWarnings("serial")
public class IlsProperty<T> extends BasicProperty<T> implements java.io.Serializable {
	private int sortOrder;
	private String[] choices;
	
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
	}

    public static final IlsProperty<Boolean> ACK_REQUIRED = new IlsProperty<Boolean>(IlsSfcNames.ACK_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> ADVICE = new IlsProperty<String>(IlsSfcNames.ADVICE, String.class, "");
    public static final IlsProperty<String> ARRAY_KEY = new IlsProperty<String>(IlsSfcNames.ARRAY_KEY, String.class, IlsSfcNames.NONE);
    public static final IlsProperty<String> AUDIT_LEVEL = new IlsProperty<String>(IlsSfcNames.AUDIT_LEVEL, String.class, IlsSfcNames.AUDIT_LEVEL_CHOICES[0], IlsSfcNames.AUDIT_LEVEL_CHOICES);
    public static final IlsProperty<String> AUTO_MODE = new IlsProperty<String>(IlsSfcNames.AUTO_MODE, String.class, IlsSfcNames.AUTO_MODE_CHOICES[0], IlsSfcNames.AUTO_MODE_CHOICES);
    public static final IlsProperty<String> BUTTON_KEY = new IlsProperty<String>(IlsSfcNames.BUTTON_KEY, String.class, "");
    public static final IlsProperty<String> BUTTON_KEY_LOCATION = new IlsProperty<String>(IlsSfcNames.BUTTON_KEY_LOCATION, String.class, IlsSfcNames.RECIPE_LOCATION_CHOICES[0], IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> BUTTON_LABEL = new IlsProperty<String>(IlsSfcNames.BUTTON_LABEL, String.class, "");
    public static final IlsProperty<String> CALLBACK = new IlsProperty<String>(IlsSfcNames.CALLBACK, String.class, "");
    public static final IlsProperty<String> CATEGORY = new IlsProperty<String>(IlsSfcNames.CATEGORY, String.class, "");
    public static final IlsProperty<String> CHOICES_RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.CHOICES_RECIPE_LOCATION, String.class, IlsSfcNames.RECIPE_LOCATION_CHOICES[0], IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> CHOICES_KEY = new IlsProperty<String>(IlsSfcNames.CHOICES_KEY, String.class, "");
    public static final IlsProperty<String> CLASS = new IlsProperty<String>(IlsSfcNames.CLASS, String.class, "");
    public static final IlsProperty<Integer> COLUMNS = new IlsProperty<Integer>(IlsSfcNames.COLUMNS, Integer.class, null);
    public static final IlsProperty<String> COLUMN_KEY = new IlsProperty<String>(IlsSfcNames.COLUMN_KEY, String.class, IlsSfcNames.NONE);
    public static final IlsProperty<Boolean> COLUMN_KEYED = new IlsProperty<Boolean>(IlsSfcNames.COLUMN_KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> COMPUTER = new IlsProperty<String>(IlsSfcNames.COMPUTER, String.class, IlsSfcNames.COMPUTER_CHOICES[0], IlsSfcNames.COMPUTER_CHOICES);
    public static final IlsProperty<String> DATABASE = new IlsProperty<String>(IlsSfcNames.DATABASE, String.class, "");
    public static final IlsProperty<Double> DELAY = new IlsProperty<Double>(IlsSfcNames.DELAY,Double.class, 0.);
    public static final IlsProperty<String> DELAY_UNIT = new IlsProperty<String>(IlsSfcNames.DELAY_UNIT, String.class, IlsSfcNames.MINUTE);  
    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
    public static final IlsProperty<String> DIALOG = new IlsProperty<String>(IlsSfcNames.DIALOG, String.class, "");
    public static final IlsProperty<String> DIALOG_TEMPLATE = new IlsProperty<String>(IlsSfcNames.DIALOG_TEMPLATE, String.class, "ReviewData");
    public static final IlsProperty<String> DIRECTORY = new IlsProperty<String>(IlsSfcNames.DIRECTORY, String.class, "");
    public static final IlsProperty<String> DISPLAY_MODE = new IlsProperty<String>(IlsSfcNames.DISPLAY_MODE, String.class, IlsSfcNames.DISPLAY_MODE_CHOICES[0], IlsSfcNames.DISPLAY_MODE_CHOICES);
    public static final IlsProperty<Boolean> DOWNLOAD = new IlsProperty<Boolean>(IlsSfcNames.DOWNLOAD, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> DOWNLOAD_STATUS = new IlsProperty<String>(IlsSfcNames.DOWNLOAD_STATUS, String.class, "");
    public static final IlsProperty<Integer> ELEMENTS = new IlsProperty<Integer>(IlsSfcNames.ELEMENTS, Integer.class, null);
    public static final IlsProperty<Boolean> ENABLE_PAUSE = new IlsProperty<Boolean>(IlsSfcNames.ENABLE_PAUSE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> ENABLE_RESUME = new IlsProperty<Boolean>(IlsSfcNames.ENABLE_RESUME, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> ENABLE_CANCEL = new IlsProperty<Boolean>(IlsSfcNames.ENABLE_CANCEL, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Double> ERROR_CODE = new IlsProperty<Double>(IlsSfcNames.ERROR_CODE, Double.class, null);
    public static final IlsProperty<String> ERROR_TEXT = new IlsProperty<String>(IlsSfcNames.ERROR_TEXT, String.class, "");
    public static final IlsProperty<String> EXTENSION = new IlsProperty<String>(IlsSfcNames.EXTENSION, String.class, ".txt");
    public static final IlsProperty<String> FETCH_MODE = new IlsProperty<String>(IlsSfcNames.FETCH_MODE, String.class, "", IlsSfcNames.FETCH_MODE_CHOICES);
    public static final IlsProperty<String> FILENAME = new IlsProperty<String>(IlsSfcNames.FILENAME, String.class, "");
    public static final IlsProperty<String> GUI_UNITS = new IlsProperty<String>(IlsSfcNames.FILENAME, String.class, "");
    public static final IlsProperty<String> GUI_LABEL = new IlsProperty<String>(IlsSfcNames.GUI_LABEL, String.class, "");
    public static final IlsProperty<String> HELP = new IlsProperty<String>(IlsSfcNames.HELP, String.class, "");
    public static final IlsProperty<Double> HIGH_LIMIT = new IlsProperty<Double>(IlsSfcNames.HIGH_LIMIT, Double.class, 0.);
    public static final IlsProperty<String> JSON_LIST = new IlsProperty<String>(IlsSfcNames.VALUE, String.class, "[0., 0.]");
    public static final IlsProperty<String> JSON_MATRIX = new IlsProperty<String>(IlsSfcNames.VALUE, String.class, "[0., 0.][0., 0.]");
    public static final IlsProperty<String> JSON_OBJECT = new IlsProperty<String>(IlsSfcNames.VALUE, String.class, "{}");
    public static final IlsProperty<String> KEY = new IlsProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final IlsProperty<Boolean> KEYED = new IlsProperty<Boolean>(IlsSfcNames.KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> KEY_MODE = new IlsProperty<String>(IlsSfcNames.KEY_MODE, String.class, "", IlsSfcNames.KEY_MODE_CHOICES);
    public static final IlsProperty<String> LABEL = new IlsProperty<String>(IlsSfcNames.LABEL, String.class, "");
    public static final IlsProperty<Double> LOW_LIMIT = new IlsProperty<Double>(IlsSfcNames.LOW_LIMIT, Double.class, 0.);
    public static final IlsProperty<Double> MAX_TIMING = new IlsProperty<Double>(IlsSfcNames.MAX_TIMING, Double.class, 0.);
    public static final IlsProperty<Double> MAXIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MAXIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
    public static final IlsProperty<String> METHOD = new IlsProperty<String>(IlsSfcNames.METHOD, String.class, "");
    public static final IlsProperty<Double> MINIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MINIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> POSITION = new IlsProperty<String>(IlsSfcNames.POSITION, String.class, IlsSfcNames.POSITION_CHOICES[0], IlsSfcNames.POSITION_CHOICES);
    public static final IlsProperty<Boolean> POST_NOTIFICATION = new IlsProperty<Boolean>(IlsSfcNames.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Boolean> POST_TO_QUEUE = new IlsProperty<Boolean>(IlsSfcNames.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> POSTING_METHOD = new IlsProperty<String>(IlsSfcNames.POSTING_METHOD, String.class, "");
    public static final IlsProperty<String> PROMPT = new IlsProperty<String>(IlsSfcNames.PROMPT, String.class, "");
    public static final IlsProperty<Boolean> PRINT_FILE = new IlsProperty<Boolean>(IlsSfcNames.PRINT_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> PRIORITY = new IlsProperty<String>(IlsSfcNames.PRIORITY, String.class, IlsSfcNames.PRIORITY_CHOICES[0], IlsSfcNames.PRIORITY_CHOICES);
    public static final IlsProperty<String> PV_MONITOR_STATUS = new IlsProperty<String>(IlsSfcNames.PV_MONITOR_STATUS, String.class, "");
    public static final IlsProperty<String> PV_MONITOR_ACTIVE = new IlsProperty<String>(IlsSfcNames.PV_MONITOR_ACTIVE, String.class, "");
    public static final IlsProperty<Double> PV_VALUE = new IlsProperty<Double>(IlsSfcNames.PV_VALUE, Double.class, 0.);
    public static final IlsProperty<String> QUEUE = new IlsProperty<String>(IlsSfcNames.QUEUE, String.class, "");
    public static final IlsProperty<Double> RAMP_TIME = new IlsProperty<Double>(IlsSfcNames.RAMP_TIME, Double.class, 5.);
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, IlsSfcNames.RECIPE_LOCATION_CHOICES[0], IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> RESULTS_MODE = new IlsProperty<String>(IlsSfcNames.RESULTS_MODE, String.class, "", IlsSfcNames.RESULTS_MODE_CHOICES);
    public static final IlsProperty<String> REVIEW_FLOWS = new IlsProperty<String>(IlsSfcNames.REVIEW_FLOWS, String.class, null);
    public static final IlsProperty<String> REVIEW_DATA = new IlsProperty<String>(IlsSfcNames.REVIEW_DATA, String.class, null);
    public static final IlsProperty<String> REVIEW_DATA_WITH_ADVICE = new IlsProperty<String>(IlsSfcNames.REVIEW_DATA_WITH_ADVICE, String.class, null);
    public static final IlsProperty<Integer> ROWS = new IlsProperty<Integer>(IlsSfcNames.ROWS, Integer.class, null);
    public static final IlsProperty<String> ROW_KEY = new IlsProperty<String>(IlsSfcNames.ROW_KEY, String.class, IlsSfcNames.NONE);
    public static final IlsProperty<Boolean> ROW_KEYED = new IlsProperty<Boolean>(IlsSfcNames.ROW_KEYED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> S88_LEVEL = new IlsProperty<String>(IlsSfcNames.S88_LEVEL, String.class, IlsSfcNames.S88_LEVEL_CHOICES[0], IlsSfcNames.S88_LEVEL_CHOICES);
    public static final IlsProperty<String> SCREEN_HEADER = new IlsProperty<String>(IlsSfcNames.SCREEN_HEADER, String.class, "");
    public static final IlsProperty<String> SECURITY = new IlsProperty<String>(IlsSfcNames.SECURITY, String.class, IlsSfcNames.SECURITY_CHOICES[0], IlsSfcNames.SECURITY_CHOICES);
    public static final IlsProperty<Boolean> SHOW_PRINT_DIALOG = new IlsProperty<Boolean>(IlsSfcNames.SHOW_PRINT_DIALOG, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> SQL = new IlsProperty<String>(IlsSfcNames.SQL, String.class, "");
    public static final IlsProperty<Double> STEP_TIME = new IlsProperty<Double>(IlsSfcNames.STEP_TIME, Double.class, 0.);
    public static final IlsProperty<String> STEP_TIMESTAMP = new IlsProperty<String>(IlsSfcNames.STEP_TIMESTAMP, String.class, "");
    public static final IlsProperty<String> TAG_PATH = new IlsProperty<String>(IlsSfcNames.TAG_PATH, String.class, "");
    public static final IlsProperty<String> TIME_DELAY_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, IlsSfcNames.TIME_DELAY_STRATEGY_CHOICES[0], IlsSfcNames.TIME_DELAY_STRATEGY_CHOICES);
    public static final IlsProperty<Double> TIMING = new IlsProperty<Double>(IlsSfcNames.TIMING, Double.class, 0.);
    public static final IlsProperty<String> RECIPE_STATIC_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, IlsSfcNames.RECIPE_STATIC_STRATEGY_CHOICES[0], IlsSfcNames.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final IlsProperty<Double> SCALE = new IlsProperty<Double>(IlsSfcNames.SCALE, Double.class, .5);
    public static final IlsProperty<Double> TARGET_VALUE = new IlsProperty<Double>(IlsSfcNames.TARGET_VALUE, Double.class, 0.);
    public static final IlsProperty<Integer> TIMEOUT = new IlsProperty<Integer>(IlsSfcNames.TIMEOUT, Integer.class, 0);
    public static final IlsProperty<String> TIMEOUT_UNIT = new IlsProperty<String>(IlsSfcNames.TIMEOUT_UNIT, String.class, IlsSfcNames.MINUTE);
    public static final IlsProperty<Boolean> TIMESTAMP = new IlsProperty<Boolean>(IlsSfcNames.TIMESTAMP, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> TYPE = new IlsProperty<String>(IlsSfcNames.TYPE, String.class, "");
    public static final IlsProperty<String> UNITS = new IlsProperty<String>(IlsSfcNames.UNITS, String.class, "");
    public static final IlsProperty<Double> UPDATE_FREQUENCY = new IlsProperty<Double>(IlsSfcNames.UPDATE_FREQUENCY, Double.class, 10.);
    public static final IlsProperty<Object> VALUE = new IlsProperty<Object>(IlsSfcNames.VALUE, Object.class, null);
    public static final IlsProperty<Boolean> VIEW_FILE = new IlsProperty<Boolean>(IlsSfcNames.VIEW_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> WINDOW = new IlsProperty<String>(IlsSfcNames.WINDOW, String.class, "");
    public static final IlsProperty<Boolean> WRITE_CONFIRM = new IlsProperty<Boolean>(IlsSfcNames.WRITE_CONFIRM, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> WRITE_CONFIRMED = new IlsProperty<Boolean>(IlsSfcNames.WRITE_CONFIRMED, Boolean.class, Boolean.TRUE);

    public IlsProperty() {}
    
	public IlsProperty(String name, Class<T> clazz, T defaultValue) {
		super(name, clazz, defaultValue);
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
		if(property.getType() == Integer.class) {
			try {
				return Integer.parseInt(stringValue);
			}
			catch(NumberFormatException e) {
				throw new ParseException("bad integer format: " + stringValue, 0);
			}
		}
		else if(property.getType() == Double.class) {
			try {
				return Double.parseDouble(stringValue);
			}
			catch(NumberFormatException e) {
				throw new ParseException("bad float format: " + stringValue, 0);
			}
		}
		else if(property.getType() == Boolean.class) {
			try {
				return Boolean.valueOf(stringValue);
			}
			catch(NumberFormatException e) {
				throw new ParseException("bad boolean format: " + stringValue, 0);
			}
		}
		else if(property.equals(JSON_LIST)) {
			// validate that the string is a valid JSON list
			try {
				new JSONArray(stringValue);
			}
			catch(JSONException e) {
				throw new ParseException("bad array format: " + stringValue + "; should be something like " + JSON_LIST.getDefaultValue(), 0);				
			}
			return stringValue;
		}
		else if(property.equals(JSON_MATRIX)) {
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
		return 
			property.equals(IlsProperty.REVIEW_DATA_WITH_ADVICE) ||
			property.equals(IlsProperty.JSON_OBJECT);
	}
}
