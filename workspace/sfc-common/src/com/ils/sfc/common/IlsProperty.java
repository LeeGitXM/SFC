package com.ils.sfc.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import system.ils.sfc.common.Constants;

public class IlsProperty {
	private static final String EMPTY_MONITOR_DOWNLOAD_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_CONFIRM_CONTROLLERS_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_MANUAL_DATA_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_PV_MONITOR_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_WRITE_OUTPUT_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_REVIEW_DATA_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_REVIEW_FLOWS_CONFIG = "{\"rows\":[]}";
	private static final String EMPTY_COLLECT_DATA_CONFIG = "{\"errorHandling\": \"" + Constants.DEFAULT_VALUE + "\", \"rows\":[]}";
	
	private static final Map<Integer, PropertyInfo> infoById = new HashMap<Integer, PropertyInfo>();
	
	private static final Map<String,String> g2ValueTrans = new HashMap<String,String>();
	private static void addValueTranslation(String key, String value) {
		if(g2ValueTrans.get(key) != null) {
			//throw new Error("Translation already exists for " +  key);
		}
		else {
			g2ValueTrans.put(key, value);
		}
	}
	
	// /These are generic, non-step specific translations
	static {
		addValueTranslation("description", Constants.DESCRIPTION);
		
		// RECIPE_LOCATION_CHOICES = {LOCAL, PRIOR, SUPERIOR, GLOBAL, OPERATION, PHASE, TAG};
		addValueTranslation("GLOBAL", Constants.GLOBAL);
		addValueTranslation("OPERATION", Constants.OPERATION);
		addValueTranslation("PHASE", Constants.PHASE);
		addValueTranslation("SUPERIOR", Constants.SUPERIOR);
		addValueTranslation("PROCEDURE", Constants.GLOBAL);
		addValueTranslation("PREVIOUS", Constants.PREVIOUS);
		addValueTranslation("LOCAL", Constants.LOCAL);
		addValueTranslation("TAG", Constants.TAG);
		
		// Constants.POSITION_CHOICES
		addValueTranslation("CENTER", Constants.CENTER);
		addValueTranslation("TOP-LEFT", Constants.TOP_LEFT);
		addValueTranslation("TOP-CENTER", Constants.TOP_CENTER);
		addValueTranslation("TOP-RIGHT", Constants.TOP_RIGHT);
		addValueTranslation("BOTTOM-LEFT", Constants.BOTTOM_LEFT);
		addValueTranslation("BOTTOM-CENTER", Constants.BOTTOM_CENTER);
		addValueTranslation("BOTTOM-RIGHT", Constants.BOTTOM_RIGHT);
		
		// Constants.TIME_DELAY_STRATEGY_CHOICES = {LOCAL, RECIPE, CALLBACK,TAG}
		addValueTranslation("RECIPE-DATA", Constants.RECIPE);
		addValueTranslation("RECIPE-BLOCK", Constants.RECIPE); // ?
		addValueTranslation("CALLBACK", Constants.CALLBACK);
		addValueTranslation("NAMED-PARAM-OR-VAR", Constants.TAG);

		// AUTO_MODE_CHOICES = {AUTOMATIC, SEMI_AUTOMATIC};
		addValueTranslation("SEMI-AUTOMATIC", Constants.SEMI_AUTOMATIC);
		addValueTranslation("AUTOMATIC", Constants.AUTOMATIC);
		
		// PRIORITY_CHOICES = {INFO, WARNING, ERROR};
		addValueTranslation("INFORMATION", Constants.INFO);
		addValueTranslation("WARNING", Constants.WARNING);
		addValueTranslation("ERROR", Constants.ERROR);
		
		// TIME_DELAY_UNIT_CHOICES = {DELAY_UNIT_SECOND, DELAY_UNIT_MINUTE, DELAY_UNIT_HOUR};
		addValueTranslation("SEC", Constants.DELAY_UNIT_SECOND);
		addValueTranslation("MIN", Constants.DELAY_UNIT_MINUTE);
		addValueTranslation("HR", Constants.DELAY_UNIT_HOUR);
		
		// VALUE_TYPE_CHOICES = {BOOLEAN, DATE_TIME,FLOAT,INT, STRING};
		addValueTranslation("FLOAT", Constants.FLOAT);
		addValueTranslation("INTEGER", Constants.INT);
		addValueTranslation("STRING", Constants.STRING);
		addValueTranslation("SYMBOL", Constants.STRING);
		addValueTranslation("TRUTH-VALUE", Constants.BOOLEAN);
}
	
	public static class PropertyInfo {
		boolean isSerializedObject = false;
		boolean isReadOnly = false;
		String[] choices;
		String label;
		Map<String,String> g2ValueTranslation;
		
		public PropertyInfo(boolean isSerializedObject, String[] choices,String label) {
			super();
			this.isSerializedObject = isSerializedObject;
			this.choices = choices;
			this.label = label;
		}
	}
	
	// Ignition properties that we wish to omit from the editor
	public static final Set<String> ignitionPropertiesToHide = new HashSet<String>();
	static {
		// Ignition step properties:
		ignitionPropertiesToHide.add("location");
		ignitionPropertiesToHide.add("location-adjustment");
		ignitionPropertiesToHide.add("type");
		ignitionPropertiesToHide.add("factory-id");
		ignitionPropertiesToHide.add(Constants.ASSOCIATED_DATA);
		// ILS recipe data type:
		ignitionPropertiesToHide.add("class");
		ignitionPropertiesToHide.add("execution-mode");  // hide this for ILS encapsulations like Procedure
		ignitionPropertiesToHide.add("passed-parameters");
		ignitionPropertiesToHide.add("return-parameters"); 
	}
	public static boolean isHiddenProperty(String name) { return ignitionPropertiesToHide.contains(name); }


    public static final BasicProperty<Boolean> ACK_REQUIRED = createProperty(Constants.ACK_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> ACTIVATION_CALLBACK = createProperty(Constants.ACTIVATION_CALLBACK, String.class, "");
    public static final BasicProperty<String> ADVICE = createProperty(Constants.ADVICE, String.class, "");
    public static final BasicProperty<String> ARRAY_KEY = createProperty(Constants.ARRAY_KEY, String.class, null);
    public static final BasicProperty<JSONObject> ASSOCIATED_DATA = new BasicProperty<JSONObject>(Constants.ASSOCIATED_DATA, JSONObject.class, null);
    //public static final BasicProperty<String> AUDIT_LEVEL = createProperty(IlsSfcNames.AUDIT_LEVEL, String.class, IlsSfcNames.AUDIT_LEVEL_CHOICES[0], IlsSfcNames.AUDIT_LEVEL_CHOICES);
    public static final BasicProperty<String> AUTO_MODE = createProperty(Constants.AUTO_MODE, String.class, Constants.AUTO_MODE_CHOICES[0], Constants.AUTO_MODE_CHOICES);
    public static final BasicProperty<String> BOTTOM_MESSAGE = createProperty(Constants.BOTTOM_MESSAGE, String.class, "");
    public static final BasicProperty<String> BUTTON_KEY = createProperty(Constants.BUTTON_KEY, String.class, "");
    public static final BasicProperty<String> BUTTON_KEY_LOCATION = createProperty(Constants.BUTTON_KEY_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<String> BUTTON_LABEL = createProperty(Constants.BUTTON_LABEL, String.class, "");
    public static final BasicProperty<String> BUTTON_CALLBACK = createProperty(Constants.BUTTON_CALLBACK, String.class, "");
    public static final BasicProperty<String> CALLBACK = createProperty(Constants.CALLBACK, String.class, "");
    public static final BasicProperty<String> CATEGORY = createProperty(Constants.CATEGORY, String.class, "");
    public static final BasicProperty<String> CHART_PATH = createProperty("chart-path", String.class, "");
    public static final BasicProperty<String> CHOICES_RECIPE_LOCATION = createProperty(Constants.CHOICES_RECIPE_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<String> CHOICES_RECIPE_CHART_STEP_LOCATION = createProperty(Constants.CHOICES_RECIPE_CHART_STEP_LOCATION, String.class, Constants.RECIPE_PLUS_CHART_STEP_CHOICES[0], Constants.RECIPE_PLUS_CHART_STEP_CHOICES);
    public static final BasicProperty<String> CHOICES_KEY = createProperty(Constants.CHOICES_KEY, String.class, "");
    public static final BasicProperty<String> CLASS = createProperty(Constants.CLASS, String.class, "");
    public static final BasicProperty<String> CLASS_TO_CREATE = createProperty(Constants.CLASS_TO_CREATE, String.class, Constants.SIMPLE_VALUE, Constants.RECIPE_DATA_CLASS_CHOICES);
    public static final BasicProperty<String> COLLECT_DATA_CONFIG = createProperty(Constants.COLLECT_DATA_CONFIG, String.class, EMPTY_COLLECT_DATA_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> CONFIRM_CONTROLLERS_CONFIG = createProperty(Constants.CONFIRM_CONTROLLERS_CONFIG, String.class, EMPTY_CONFIRM_CONTROLLERS_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<Integer> COLUMNS = createProperty(Constants.COLUMNS, Integer.class, 0);
    public static final BasicProperty<String> COLUMN_KEY = createProperty(Constants.COLUMN_KEY, String.class, null);
    //public static final BasicProperty<Boolean> COLUMN_KEYED = createProperty(Constants.COLUMN_KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> COMPUTER = createProperty(Constants.COMPUTER, String.class, Constants.COMPUTER_CHOICES[0], Constants.COMPUTER_CHOICES);
    public static final BasicProperty<String> CUSTOM_WINDOW_PATH = createProperty(Constants.CUSTOM_WINDOW_PATH, String.class, "");
    public static final BasicProperty<String> DATA_ID = createProperty(Constants.DATA_ID, String.class, "");

    public static final BasicProperty<Double> DEADTIME = createProperty(Constants.DEADTIME, Double.class, 0.);
    public static final BasicProperty<String> DEFAULT_VALUE = createProperty(Constants.DEFAULT_VALUE, String.class, "");
    public static final BasicProperty<Double> DELAY = createProperty(Constants.DELAY, Double.class, 0.);
    public static final BasicProperty<String> DELAY_UNIT = createProperty(Constants.DELAY_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);  
    public static final BasicProperty<String> DESCRIPTION = createProperty(Constants.DESCRIPTION, String.class, "");
    public static final BasicProperty<String> DIRECTORY = createProperty(Constants.DIRECTORY, String.class, "");
    public static final BasicProperty<Date> DRIVER = createProperty(Constants.DRIVER, Date.class, new Date());
    public static final BasicProperty<Boolean> DOWNLOAD = createProperty(Constants.DOWNLOAD, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> DOWNLOAD_STATUS = createProperty(Constants.DOWNLOAD_STATUS, String.class, "");
    public static final BasicProperty<Boolean> ENABLE_PAUSE = createProperty(Constants.ENABLE_PAUSE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_RESUME = createProperty(Constants.ENABLE_RESUME, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_CANCEL = createProperty(Constants.ENABLE_CANCEL, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_START = createProperty(Constants.ENABLE_START, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> ENABLE_RESET = createProperty(Constants.ENABLE_RESET, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Double> ERROR_CODE = createProperty(Constants.ERROR_CODE, Double.class, 0.);
    
    public static final BasicProperty<String> ERROR_COUNT_SCOPE = createProperty(Constants.ERROR_COUNT_SCOPE, String.class, Constants.RECIPE_PLUS_CHART_STEP_CHOICES[7], Constants.RECIPE_PLUS_CHART_STEP_CHOICES);
    public static final BasicProperty<String> ERROR_COUNT_KEY = createProperty(Constants.ERROR_COUNT_KEY, String.class, "errorCount");
    public static final BasicProperty<String> ERROR_COUNT_MODE = createProperty(Constants.ERROR_COUNT_MODE, String.class, Constants.COUNT_MODE_CHOICES[0], Constants.COUNT_MODE_CHOICES);
    
    public static final BasicProperty<String> ERROR_TEXT = createProperty(Constants.ERROR_TEXT, String.class, "");
    public static final BasicProperty<String> EXTENSION = createProperty(Constants.EXTENSION, String.class, ".txt");
    public static final BasicProperty<String> FACTORY_ID = createProperty("factory-id", String.class, "");
    public static final BasicProperty<String> FETCH_MODE = createProperty(Constants.FETCH_MODE, String.class, Constants.FETCH_MODE_CHOICES[0], Constants.FETCH_MODE_CHOICES);
    public static final BasicProperty<String> FILENAME = createProperty(Constants.FILENAME, String.class, "");
    public static final BasicProperty<String> FILE_LOCATION = createProperty(Constants.FILE_LOCATION, String.class, Constants.FILE_LOCATION_CHOICES[0], Constants.FILE_LOCATION_CHOICES);
    public static final BasicProperty<String> GLOBAL_ERROR_COUNT_KEY = createProperty(Constants.GLOBAL_ERROR_COUNT_KEY, String.class, "");
    public static final BasicProperty<String> GLOBAL_ERROR_COUNT_LOCATION = createProperty(Constants.GLOBAL_ERROR_COUNT_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<String> HEADING1 = createProperty(Constants.HEADING1, String.class, "");
    public static final BasicProperty<String> HEADING2 = createProperty(Constants.HEADING2, String.class, "");
    public static final BasicProperty<String> HEADING3 = createProperty(Constants.HEADING3, String.class, "");
    public static final BasicProperty<String> HELP = createProperty(Constants.HELP, String.class, "");
    public static final BasicProperty<Double> HIGH_LIMIT = createProperty(Constants.HIGH_LIMIT, Double.class, null);
    public static final BasicProperty<String> ID = createProperty(Constants.ID, String.class, null);
    public static final BasicProperty<Boolean> IS_SFC_WINDOW = createProperty(Constants.IS_SFC_WINDOW, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> JSON_LIST = createProperty(Constants.VALUE, String.class, "[]");
    public static final BasicProperty<String> JSON_MATRIX = createProperty(Constants.VALUE, String.class, "[[],[]]");
    public static final BasicProperty<String> JSON_OBJECT = createProperty(Constants.VALUE, String.class, "{}", true);
    public static final BasicProperty<String> KEY = createProperty(Constants.KEY, String.class, "");
    public static final BasicProperty<String> KEY_AND_ATTRIBUTE = createProperty(Constants.KEY_AND_ATTRIBUTE, String.class, "");
    //public static final BasicProperty<Boolean> KEYED = createProperty(Constants.KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> KEY_MODE = createProperty(Constants.KEY_MODE, String.class, "", Constants.KEY_MODE_CHOICES);
    public static final BasicProperty<String> LABEL = createProperty(Constants.LABEL, String.class, "");
    public static final BasicProperty<Integer> LENGTH = createProperty(Constants.LENGTH, Integer.class, 0,false);
    public static final BasicProperty<Double> LOW_LIMIT = createProperty(Constants.LOW_LIMIT, Double.class, null);
    public static final BasicProperty<Double> MAX_TIMING = createProperty(Constants.MAX_TIMING, Double.class, 0.);
    public static final BasicProperty<Double> MAXIMUM_VALUE = createProperty(Constants.MAXIMUM_VALUE, Double.class, 0.);
    public static final BasicProperty<String> MESSAGE = createProperty(Constants.MESSAGE, String.class, "");
    public static final BasicProperty<String> MESSAGE_QUEUE = createProperty(Constants.MESSAGE_QUEUE, String.class, Constants.DEFAULT_MESSAGE_QUEUE);
    public static final BasicProperty<Double> MINIMUM_VALUE = createProperty(Constants.MINIMUM_VALUE, Double.class, 0.);
    public static final BasicProperty<String> MAIN_MESSAGE = createProperty(Constants.MAIN_MESSAGE, String.class, "");
    public static final BasicProperty<String> MANUAL_DATA_CONFIG = createProperty(Constants.MANUAL_DATA_CONFIG, String.class, EMPTY_MONITOR_DOWNLOAD_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> MANUAL_DATA_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_MANUAL_DATA_WINDOW, false);
    public static final BasicProperty<String> MONITOR_DOWNLOADS_CONFIG = createProperty(Constants.MONITOR_DOWNLOADS_CONFIG, String.class, EMPTY_MANUAL_DATA_CONFIG, true, Constants.CONFIG);
    public static final BasicProperty<String> MONITOR_DOWNLOADS_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_MONITOR_DOWNLOADS_WINDOW, false);
	public static final BasicProperty<String> NAME = new BasicProperty<String>(Constants.NAME, String.class);
	public static final BasicProperty<String> OC_ALERT_WINDOW = createProperty(Constants.OC_ALERT_WINDOW, String.class, "");
	public static final BasicProperty<String> OC_ALERT_WINDOW_TYPE = createProperty(Constants.OC_ALERT_WINDOW_TYPE, String.class, Constants.OC_ALERT_WINDOW_TYPE_CHOICES[0], Constants.OC_ALERT_WINDOW_TYPE_CHOICES);
	public static final BasicProperty<Object> OUTPUT_TYPE = createProperty(Constants.OUTPUT_TYPE, Object.class, Constants.OUTPUT_VALUE_TYPE_CHOICES[0], Constants.OUTPUT_VALUE_TYPE_CHOICES);
    public static final BasicProperty<String> PARALLEL_CHILDREN = createProperty("parallel-children", String.class, "");
    public static final BasicProperty<String> POSITION = createProperty(Constants.POSITION, String.class, Constants.POSITION_CHOICES[0], Constants.POSITION_CHOICES);
    public static final BasicProperty<String> POST = createProperty(Constants.POST, String.class, "");
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
    public static final BasicProperty<String> RECIPE_CHART_STEP_LOCATION = createProperty(Constants.RECIPE_LOCATION, String.class, Constants.RECIPE_PLUS_CHART_STEP_CHOICES[0], Constants.RECIPE_PLUS_CHART_STEP_CHOICES);
    public static final BasicProperty<Boolean> REQUIRE_ALL_INPUTS = createProperty(Constants.REQUIRE_ALL_INPUTS, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> RESPONSE_KEY_AND_ATTRIBUTE = createProperty(Constants.RESPONSE_KEY_AND_ATTRIBUTE, String.class, "");
    public static final BasicProperty<String> RESULTS_MODE = createProperty(Constants.RESULTS_MODE, String.class, Constants.RESULTS_MODE_CHOICES[0], Constants.RESULTS_MODE_CHOICES);
    public static final BasicProperty<String> REVIEW_DATA_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_REVIEW_DATA_WINDOW, false);
    public static final BasicProperty<String> REVIEW_FLOWS = createProperty(Constants.REVIEW_FLOWS, String.class, EMPTY_REVIEW_FLOWS_CONFIG, true);
    public static final BasicProperty<String> REVIEW_FLOWS_WINDOW = createProperty(Constants.WINDOW, String.class, Constants.SFC_REVIEW_FLOWS_WINDOW, false);
    public static final BasicProperty<Integer> ROWS = createProperty(Constants.ROWS, Integer.class, 0);
    public static final BasicProperty<String> ROW_KEY = createProperty(Constants.ROW_KEY, String.class, null);
    public static final BasicProperty<Double> RUNTIME = createProperty(Constants.RUNTIME, Double.class, 0.);
    //public static final BasicProperty<Boolean> ROW_KEYED = createProperty(Constants.ROW_KEYED, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<String> SECURITY = createProperty(Constants.SECURITY, String.class, Constants.SECURITY_CHOICES[1], Constants.SECURITY_CHOICES);
    public static final BasicProperty<String> SECONDARY_REVIEW_DATA = createProperty(Constants.SECONDARY_REVIEW_DATA, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final BasicProperty<String> SECONDARY_REVIEW_DATA_WITH_ADVICE = createProperty(Constants.SECONDARY_REVIEW_DATA_WITH_ADVICE, String.class, EMPTY_REVIEW_DATA_CONFIG, true, "secondary data");
    public static final BasicProperty<String> SECONDARY_SORT_KEY = createProperty(Constants.SECONDARY_SORT_KEY, String.class, Constants.SECONDARY_SORT_KEY_CHOICES[0], Constants.SECONDARY_SORT_KEY_CHOICES);
    public static final BasicProperty<String> SECONDARY_TAB_LABEL = createProperty(Constants.SECONDARY_TAB_LABEL, String.class, "Secondary");
    public static final BasicProperty<String> SETPOINT_STATUS = createProperty(Constants.SETPOINT_STATUS, String.class, "");
    public static final BasicProperty<Boolean> SHOW_PRINT_DIALOG = createProperty(Constants.SHOW_PRINT_DIALOG, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> SQL = createProperty(Constants.SQL, String.class, "");
    public static final BasicProperty<Date> START_TIME = createProperty(Constants.START_TIME, Date.class, new Date());
    public static final BasicProperty<String> STATE = createProperty(Constants.STATE, String.class, "");
    public static final BasicProperty<Date> STEP_TIME = createProperty(Constants.STEP_TIME, Date.class, new Date());
    public static final BasicProperty<String> STEP_TIMESTAMP = createProperty(Constants.STEP_TIMESTAMP, String.class, "");
    public static final BasicProperty<String> TAG_PATH = createProperty(Constants.TAG_PATH, String.class, "");
    public static final BasicProperty<String> TIME_LIMIT_RECIPE_KEY = createProperty(Constants.KEY, String.class, "", false, "time limit recipe key");
    public static final BasicProperty<String> TIME_DELAY_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.TIME_DELAY_STRATEGY_CHOICES[0], Constants.TIME_DELAY_STRATEGY_CHOICES);
    public static final BasicProperty<String> TIME_LIMIT_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.TIME_LIMIT_STRATEGY_CHOICES[0], false, Constants.TIME_LIMIT_STRATEGY_CHOICES, "time limit strategy");
    public static final BasicProperty<String> TIME_LIMIT_RECIPE_LOCATION = createProperty(Constants.DATA_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], false, Constants.RECIPE_LOCATION_CHOICES, "time limit recipe location");
    public static final BasicProperty<Double> TIMING = createProperty(Constants.TIMING, Double.class, 0.);
    public static final BasicProperty<String> TOP_MESSAGE = createProperty(Constants.TOP_MESSAGE, String.class, "");
    public static final BasicProperty<String> RECIPE_STATIC_STRATEGY = createProperty(Constants.STRATEGY, String.class, Constants.RECIPE_STATIC_STRATEGY_CHOICES[0], Constants.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final BasicProperty<Double> SCALE = createProperty(Constants.SCALE, Double.class, 1.0);
    public static final BasicProperty<Double> TARGET_VALUE = createProperty(Constants.TARGET_VALUE, Double.class, 0.);
    public static final BasicProperty<Double> TIME_LIMIT_STATIC_VALUE = createProperty(Constants.VALUE, Double.class, 0.0, false, "time limit static value (min)");
    public static final BasicProperty<Double> TIMEOUT = createProperty(Constants.TIMEOUT, Double.class, -1.);
    public static final BasicProperty<String> TIMEOUT_BEHAVIOR = createProperty(Constants.TIMEOUT_BEHAVIOR, String.class, Constants.TIMEOUT_BEHAVIOR_CHOICES[0], Constants.TIMEOUT_BEHAVIOR_CHOICES);
    public static final BasicProperty<String> TIMEOUT_UNIT = createProperty(Constants.TIMEOUT_UNIT, String.class, Constants.TIME_DELAY_UNIT_CHOICES[0], Constants.TIME_DELAY_UNIT_CHOICES);
    public static final BasicProperty<Boolean> TIMER_CLEAR = createProperty(Constants.TIMER_CLEAR, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> TIMER_KEY = createProperty(Constants.TIMER_KEY, String.class, "");
    public static final BasicProperty<String> TIMER_LOCATION = createProperty(Constants.TIMER_LOCATION, String.class, Constants.RECIPE_LOCATION_CHOICES[0], Constants.RECIPE_LOCATION_CHOICES);
    public static final BasicProperty<Boolean> TIMER_SET = createProperty(Constants.TIMER_SET, Boolean.class, Boolean.FALSE);
    public static final BasicProperty<Boolean> TIMESTAMP = createProperty(Constants.TIMESTAMP, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> TYPE = createProperty(Constants.TYPE, String.class, "");
    public static final BasicProperty<String> UNITS = createProperty(Constants.UNITS, String.class, Constants.UNIT_CHOICES[0], Constants.UNIT_CHOICES );
    public static final BasicProperty<Double> UPDATE_FREQUENCY = createProperty(Constants.UPDATE_FREQUENCY, Double.class, 10.);
    public static final BasicProperty<Object> VALUE = createProperty(Constants.VALUE, Object.class, null);
    public static final BasicProperty<String> VALUE_TYPE = createProperty(Constants.VALUE_TYPE, String.class, Constants.VALUE_TYPE_CHOICES[2],Constants.VALUE_TYPE_CHOICES);
    public static final BasicProperty<Boolean> VERBOSE = createProperty(Constants.VERBOSE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> VIEW_FILE = createProperty(Constants.VIEW_FILE, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> WINDOW = createProperty(Constants.WINDOW, String.class, "");
    public static final BasicProperty<String> WINDOW_HEADER = createProperty(Constants.WINDOW_HEADER, String.class, "");
    public static final BasicProperty<String> WINDOW_TITLE = createProperty(Constants.WINDOW_TITLE, String.class, "");
    public static final BasicProperty<Boolean> WRITE_CONFIRM = createProperty(Constants.WRITE_CONFIRM, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<Boolean> WRITE_CONFIRMED = createProperty(Constants.WRITE_CONFIRMED, Boolean.class, Boolean.TRUE);
    public static final BasicProperty<String> WRITE_OUTPUT_CONFIG = createProperty(Constants.WRITE_OUTPUT_CONFIG, String.class, EMPTY_WRITE_OUTPUT_CONFIG, true, Constants.CONFIG);

    // Properties for EM-RECIPE-DATA
    public static final BasicProperty<String> CHG_LEV = createProperty(Constants.CHG_LEV, String.class, "");
    public static final BasicProperty<String> CTAG = createProperty(Constants.CTAG, String.class, "");
    public static final BasicProperty<String> DSCR = createProperty(Constants.DSCR, String.class, "");
    public static final BasicProperty<Double> HILIM = createProperty(Constants.HILIM, Double.class, 0.);
    public static final BasicProperty<Double> LOLIM = createProperty(Constants.LOLIM, Double.class, 0.);
    public static final BasicProperty<String> MODATTR =    createProperty(Constants.MODATTR, String.class, "");;
    public static final BasicProperty<String> MODATTR_VAL =createProperty(Constants.MODATTR_VAL, String.class, "");
    public static final BasicProperty<Double> PRES = createProperty(Constants.PRES, Double.class, 0.);
    public static final BasicProperty<Double> RECC = createProperty(Constants.RECC, Double.class, 0.);
    public static final BasicProperty<String> STAG = createProperty(Constants.STAG, String.class, "");
    
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
		PropertyInfo info = new PropertyInfo(isSerializedObject, choices, label);		
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
		
	public static Object parsePropertyValue(Property<?> property, 
		String stringValue, String objectValueType) throws ParseException {
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
				Integer rowLength = null;
				for(int i = 0; i < rows.length(); i++) {
					JSONArray row = rows.getJSONArray(i); 
					if(rowLength == null) {
						rowLength = row.length();
					}
					else if(rowLength != row.length()) {
						throw new ParseException("bad matrix format: " + stringValue + "; inconsistent row length", 0);										
					}
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
			if(Constants.DATE_TIME.equals(objectValueType)) {
				return parseDate(stringValue);
			}
			else {
				return parseObjectValue(stringValue, null);								
			}
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
		// try as integer
		if(hintClass != Double.class) {
			try { return Integer.parseInt(strValue); }
			catch(NumberFormatException e) { /* didn't work */ }
		}
		
		// try as double
		try { return Double.parseDouble(strValue); }
		catch(NumberFormatException e) { /* didn't work */ }

		// try as Boolean
		if(strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(strValue); 		
		}
		
		// else just a string...
		return strValue;  // nothing else worked, just make it a string
	}

	/** Try to parse a string as a date according to our strict format. If the parse
	 *  fails, return null. Caution: a null or empty string will return null...
	 */
	public static Date parseDate(String string) throws ParseException {
		if(IlsSfcCommonUtils.isEmpty(string)) {
			return null;
		}
		else {
			return Constants.DATE_FORMAT.parse(string);
		}
	}
	
	public static String[] getChoices(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		return info != null ? info.choices : null;
	}
	
	public static void setChoices(Property<?> property,String[] newChoices) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		if( info!=null ) info.choices = newChoices;
	}
	
	public static boolean isSerializedObject(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		return info != null ? info.isSerializedObject : false;
	}
	
	public static void setReadOnly(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		info.isReadOnly = true;
	}

	public static boolean isReadOnly(Property<?> property) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		return info != null ? info.isReadOnly : false;
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

	// define read-only properties:
	static {
		setReadOnly(DOWNLOAD_STATUS);
		setReadOnly(DATA_ID);
		setReadOnly(ERROR_CODE);
		setReadOnly(ERROR_TEXT);
		setReadOnly(PV_MONITOR_ACTIVE);
		setReadOnly(PV_MONITOR_STATUS);
		setReadOnly(SETPOINT_STATUS);
		setReadOnly(PV_VALUE);
		setReadOnly(STEP_TIME);
		setReadOnly(STEP_TIMESTAMP);
		// ?? used in SQC, IO. Needs to be writable in SQC
		//setReadOnly(TARGET_VALUE);
		//setReadOnly(VALUE_TYPE);
		setReadOnly(WRITE_CONFIRMED);
	}
	
	private static Map<String, BasicProperty<?>> g2ToIgProperty = new HashMap<String, BasicProperty<?>>();
	private static Map<String,Map<String, BasicProperty<?>>> g2ToIgPropertyByFactoryId = new HashMap<String,Map<String, BasicProperty<?>>>();
	static {
		g2ToIgProperty.put("acknowledgementRequired", ACK_REQUIRED);
		g2ToIgProperty.put("appendTimestamp", TIMESTAMP);
		g2ToIgProperty.put("callback", CALLBACK);
		g2ToIgProperty.put("clearTimer", TIMER_CLEAR);
		g2ToIgProperty.put("dataLocation", TIME_LIMIT_RECIPE_LOCATION);
		g2ToIgProperty.put("deadTime", DEADTIME);
		g2ToIgProperty.put("description", DESCRIPTION);
		g2ToIgProperty.put("delay-time", DELAY);
		g2ToIgProperty.put("delay-units", DELAY_UNIT);
		g2ToIgProperty.put("dialogId", WINDOW);
		g2ToIgProperty.put("directory", DIRECTORY);
		g2ToIgProperty.put("displayMode", AUTO_MODE);
		g2ToIgProperty.put("extension", EXTENSION);
		g2ToIgProperty.put("fetchMode", FETCH_MODE);
		g2ToIgProperty.put("filename", FILENAME);
		g2ToIgProperty.put("fullFilename", FILENAME);
		g2ToIgProperty.put("header", WINDOW_TITLE);
		g2ToIgProperty.put("identifier-or-name", KEY);
		g2ToIgProperty.put("key", KEY);
		g2ToIgProperty.put("label", LABEL);
		g2ToIgProperty.put("keyMode", KEY_MODE);
		g2ToIgProperty.put("location", RECIPE_LOCATION);		
		g2ToIgProperty.put("manualEntryTimeoutInSeconds", TIMEOUT);
		g2ToIgProperty.put("message-queue-name", MESSAGE_QUEUE);
		g2ToIgProperty.put("messageQueueName", MESSAGE_QUEUE);
		g2ToIgProperty.put("messageText", MESSAGE);
		//g2ToIgProperty.put("mode", MODE);
		g2ToIgProperty.put("monitorItemName", KEY);
		g2ToIgProperty.put("monitorKey", TIME_LIMIT_RECIPE_KEY);
		g2ToIgProperty.put("monitorLocalValue", TIME_LIMIT_STATIC_VALUE);
		g2ToIgProperty.put("monitorRecipeLocation", TIME_LIMIT_RECIPE_LOCATION);
		g2ToIgProperty.put("monitorStrategy", TIME_LIMIT_STRATEGY);
		g2ToIgProperty.put("name", NAME);
		g2ToIgProperty.put("post-notification", POST_NOTIFICATION);		
		g2ToIgProperty.put("postMessageToQueue", POST_TO_QUEUE);
		g2ToIgProperty.put("printFile", PRINT_FILE);
		g2ToIgProperty.put("priority", PRIORITY);
		g2ToIgProperty.put("prompt", PROMPT);
		g2ToIgProperty.put("recipe-data-location", RECIPE_LOCATION);
		g2ToIgProperty.put("recipe-location", RECIPE_LOCATION);
		g2ToIgProperty.put("requireAllInputs", REQUIRE_ALL_INPUTS);
		g2ToIgProperty.put("selectedButtonKey", BUTTON_KEY);
		g2ToIgProperty.put("setTimer", TIMER_SET);		
		g2ToIgProperty.put("strategy", TIME_DELAY_STRATEGY); // ?? multiple??		
		g2ToIgProperty.put("timerKey", TIMER_KEY);
		g2ToIgProperty.put("timerSource", TIMER_LOCATION);
		g2ToIgProperty.put("timeoutBehavior", TIMEOUT_BEHAVIOR);
		g2ToIgProperty.put("value", VALUE);
		g2ToIgProperty.put("window", WINDOW);
		g2ToIgProperty.put("windowTitle", WINDOW_TITLE);
		g2ToIgProperty.put("workspace-location", POSITION);
		g2ToIgProperty.put("workspace-scale", SCALE);
		g2ToIgProperty.put("workspaceLocation", POSITION);
		g2ToIgProperty.put("workspaceScale", SCALE);
		
		addSpecificTranslation("com.ils.pvMonitorStep", "recipeLocation", TIME_LIMIT_RECIPE_LOCATION);			
	}
	
	private static void addSpecificTranslation(String factoryId, String name, BasicProperty<?> property) {
		Map<String, BasicProperty<?>> specificTranslations = g2ToIgPropertyByFactoryId.get(factoryId);
		if(specificTranslations == null) {
			specificTranslations = new HashMap<String, BasicProperty<?>>();
			g2ToIgPropertyByFactoryId.put(factoryId, specificTranslations);
		}
		specificTranslations.put(name,  property);
	}
	
	public static BasicProperty<?> getTranslationForG2Property(String factoryId, String name) {
		Map<String, BasicProperty<?>> specificTranslations = g2ToIgPropertyByFactoryId.get(factoryId);
		if(specificTranslations != null && specificTranslations.get(name) != null) {
			return specificTranslations.get(name);
		}
		else {
			return g2ToIgProperty.get(name);
		}
	}

	public static String getTranslationForG2Value(String factoryId,BasicProperty<?> property, String g2Value, LoggerEx logger) {
		PropertyInfo info = infoById.get(getPropertyId(property));
		if(info != null && info.choices != null) {
			String transValue = null;
			// values are constrained
			if(info.g2ValueTranslation != null && info.g2ValueTranslation.get(g2Value) != null)  {
				transValue = info.g2ValueTranslation.get(g2Value.toUpperCase());
			}
			else {
				transValue = g2ValueTrans.get(g2Value.toUpperCase());
			}
			
			// special cases:
			if(TimedDelayStepProperties.FACTORY_ID.equals(factoryId) && 
				"local".equals(g2Value.toLowerCase())) {
				transValue = Constants.STATIC;
			}
			
			//logger.infof("factoryId %s prop %s g2 value %s -> enum %s", factoryId, property.getName(), g2Value, transValue);
			if(transValue == null) {
				transValue = g2Value;
				//logger.errorf("no translation for g2 value %s in property %s step %s factoryId %s", g2Value, property.getName(), stepName, factoryId);
				logger.errorf("no translation for g2 value %s in property %s factoryId %s", g2Value, property.getName(), factoryId);
			}
			else {
				logger.debugf("translated g2 value %s -> %s in property %s factoryId %s", g2Value, transValue, property.getName(), factoryId);
			}
			return transValue;
		}
		else {
			// no constraint on value
			if(g2Value.endsWith(".val")) {
				g2Value += "ue"; // change to .value
			}
			//logger.infof("factoryId %s prop %s g2 value %s -> free %s", factoryId, property.getName(), g2Value, g2Value);
			return g2Value;
		}
	}
	
	/** If the given property is a recipe data key, return the corresponding scope
	 *  property--otherwise return null.
	 */
	public static Property<?> getScopeProperty(Property<?> prop) {
		if(prop.equals(KEY)) {
			return RECIPE_LOCATION;
		}
		else if(prop.equals(BUTTON_KEY)) {
			return BUTTON_KEY_LOCATION;
		}
		else if(prop.equals(TIMER_KEY)) {
			return TIMER_LOCATION;
		}
		else if(prop.equals(TIME_LIMIT_RECIPE_KEY)) {
			return TIME_LIMIT_RECIPE_LOCATION;
		}
		else if(prop.equals(CHOICES_KEY)) {
			return CHOICES_RECIPE_LOCATION;
		}
		else {
			return null;
		}
	}
	
	/** Split apart the scope and key in a <scope>#<key> references. Return
	 *  the scope and key in an array. If there is no separator, just return
	 *  the key with a null scope.
	 */
	public static String[] parseRecipeScopeValue(String sval) {	
		if(sval == null) {
			return new String[] {null, null};
		}
		else if(sval.indexOf("#") != -1) {
			int separatorIndex = sval.indexOf("#");
			String scope = sval.substring(0, separatorIndex);
			String key = sval.substring(separatorIndex+1, sval.length());
			return new String[] {scope, key};
		}
		else {
			return new String[] {null, sval};
		}
	}
}
