package system.ils.sfc.common;

import java.text.SimpleDateFormat;


/** A single place to define names that need to be shared between Java and Jython. */
public class Constants {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd h:mm:ss aa"); 
	static {DATE_FORMAT.setLenient(false);}
	
	// states
	public static final String DEACTIVATED = "deactivated";
	public static final String ACTIVATED = "activated";
	public static final String PAUSED = "paused";
	public static final String CANCELLED = "cancelled";
	public static final String RESUMED = "resumed";
	
	// step attributes
	public static final String FACTORY_ID = "factory-id";
	public static final String CHART_PATH = "chart-path";
	
	// step properties
	public static final String ABORT = "abort";
	public static final String ABS = "Abs";
	public static final String ACK_REQUIRED = "ackRequired";
	public static final String ACTIVATION_CALLBACK = "activationCallback";
	public static final String ADVICE = "advice";
	public static final String ARRAY_KEY = "arrayKey";
	public static final String ASSOCIATED_DATA = "associated-data";
	public static final String AUDIT_LEVEL = "auditLevel";
	public static final String AUTO_MODE = "autoMode";
	public static final String AUTOMATIC = "automatic";
	public static final String AVERAGE = "average";
	public static final String BLANK = "";
	public static final String BOTTOM_CENTER = "bottomCenter";
	public static final String BOTTOM_LEFT = "bottomLeft";
	public static final String BOTTOM_MESSAGE = "bottomMessage";
	public static final String BOTTOM_RIGHT = "bottomRight";
	public static final String BUTTON_CALLBACK = "buttonCallback";
	public static final String BUTTON_LABEL = "buttonLabel";
	public static final String BUTTON_KEY = "buttonKey";
	public static final String BUTTON_KEY_LOCATION = "buttonKeyLocation";
	public static final String CALLBACK = "callback";
	public static final String CATEGORY = "category";
	public static final String CENTER = "center";
	public static final String CHILDREN = "children";
	public static final String CLASS = "class";
	public static final String CLASS_TO_CREATE = "classToCreate";
	public static final String CLIENT = "client";
	public static final String COMMAND = "callback";
	public static final String CHOICES= "choices";
	public static final String CHOICES_KEY = "choicesKey";
	public static final String CHOICES_RECIPE_LOCATION = "choicesRecipeLocation";
	public static final String CHOICES_RECIPE_CHART_STEP_LOCATION = "choicesRecipeChartStepLocation";
	public static final String COLUMN_KEY = "columnKey";
	public static final String COLUMNS = "columns";
	//public static final String COLUMN_KEYED = "columnKeyed";
	public static final String COMPUTER = "computer";
	public static final String COLLECT_DATA_CONFIG = "collectDataConfig";
	public static final String CONFIG = "config";
	public static final String CONFIRM_CONTROLLERS_CONFIG = "confirmControllersConfig";
	public static final String CONTROL_PANEL_ID = "controlPanelId";
	public static final String CONTROL_PANEL_SCRATCH_ID = "-42";
	public static final String CUSTOM_WINDOW_PATH = "customWindowPath";
	public static final String CURRENT = "current";
	public static final String DATA = "data";
	public static final String DATA_ID = "dataId";
	public static final String DATA_LOCATION = "dataLocation";
	public static final String DATA_TYPE = "dataType";
	public static final String DEADTIME = "deadTime";
	public static final String DESCRIPTION = "description";
	public static final String DEFAULT_MESSAGE_QUEUE = "SFC-Message-Queue";
	public static final String DEFAULT_VALUE = "defaultValue";
	public static final String DELAY = "delay";
	public static final String DELAY_UNIT = "delayUnit";
	public static final String DIALOG = "dialog";
	public static final String DIALOG_TEMPLATE = "dialogTemplate";
	public static final String DIRECTORY = "directory";
	public static final String DISPLAY_MODE = "displayMode";
	public static final String DOWNLOAD = "download";
	public static final String DOWNLOAD_STATUS = "downloadStatus";		
	public static final String DRIVER = "driver";
	public static final String DYNAMIC = "dynamic";
	public static final String ELEMENTS = "elements";
	public static final String ENABLE_PAUSE = "enablePause";
	public static final String ENABLE_RESUME = "enableResume";
	public static final String ENABLE_CANCEL = "enableCancel";
	public static final String ENABLE_START = "enableStart";
	public static final String ENABLE_RESET = "enableReset";
	public static final String ERROR = "Error";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_COUNT_KEY = "errorCountKey";
	public static final String ERROR_COUNT_MODE = "errorCountMode";
	public static final String ERROR_COUNT_SCOPE = "errorCountScope";
	public static final String ERROR_TEXT = "errorText";
	public static final String EXTENSION = "extension";
	public static final String FAILURE = "Failure";
	public static final String FETCH_MODE = "fetchMode";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String FILE_LOCATION = "fileLocation";
	public static final String GATEWAY = "gateway";
	public static final String GLOBAL_ERROR_COUNT_KEY = "globalErrorCountKey";
	public static final String GLOBAL_ERROR_COUNT_LOCATION = "globalErrorCountLocation";
	public static final String HEADING1 = "heading1";
	public static final String HEADING2 = "heading2";
	public static final String HEADING3 = "heading3";
	public static final String HELP = "help";
	public static final String HIGH = "High";
	public static final String HIGH_LOW = "High/Low";
	public static final String HIGH_LIMIT = "highLimit";
	public static final String ID = "id";
	public static final String IMMEDIATE = "Immediate";
	public static final String INFO = "Info";
	public static final String INSTANCE_ID = "instanceId";
	public static final String IS_SFC_WINDOW = "isSfcWindow";
	public static final String ITEM_ID = "itemid";
	public static final String ISOLATION_MODE = "isolationMode";
	public static final String JSON_LIST = "jsonList";
	public static final String JSON_MATRIX = "jsonMatrix";
	public static final String LARGE_TEXT = "largeText";
	public static final String LOW_LIMIT = "lowLimit";
	public static final String LOW = "Low";
	public static final String KEY = "key";
	public static final String KEY_AND_ATTRIBUTE = "keyAndAttribute";
	public static final String KEY_MODE = "keyMode";
	//public static final String KEYED = "keyed";
	public static final String LABEL = "label";
	public static final String LENGTH = "length";
	public static final String MANUAL_DATA_CONFIG = "manualDataConfig";
	public static final String MAX_TIMING = "maxTiming";
	public static final String MESSAGE = "message";
	public static final String MESSAGE_QUEUE = "msgQueue";
	public static final String METHOD = "method";
	public static final String MINIMUM = "minimum";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAIN_MESSAGE = "mainMessage";
	public static final String MAXIMUM = "maximum";
	public static final String MAXIMUM_VALUE = "maximumValue";
	public static final String MODE = "mode";
	public static final String MONITOR = "monitor";
	public static final String MONITORING = "Monitoring";
	public static final String MONITOR_DOWNLOADS_CONFIG = "monitorDownloadsConfig";
	public static final String MULTIPLE = "multiple";
	public static final String NAME = "name";
	public static final String NO = "No";
	public static final String NO_LIMIT = "No Limit";
	public static final String NONE = "none";
	public static final String NORMAL = "normal";
	public static final String NOT_PERSISTENT = "NotPersistent";
	public static final String NOT_CONSISTENT = "NotConsistent";
	public static final String OC_ALERT_WINDOW = "ocAlertWindow";
	public static final String OC_ALERT_WINDOW_TYPE = "ocAlertWindowType";
	public static final String OFF = "Off";
	public static final String OK = "OK";
	public static final String OUT_OF_RANGE = "Out of Range";
	public static final String OUTPUT = "output";
	public static final String OUTPUT_TYPE = "outputType";
	public static final String PARENT_GROUP = "parent-group";
	public static final String PARENT = "parent";
	public static final String PCT = "Pct";
	public static final String PENDING = "Pending";
	public static final String POST = "post";
	public static final String POSITION = "position";
	public static final String POST_TO_QUEUE = "postToQueue";
	public static final String POST_NOTIFICATION = "postNotification";
	public static final String POSTING_METHOD = "postingMethod";
	public static final String PREVIOUS = "previous"; 
	public static final String PRIMARY_REVIEW_DATA = "primaryReviewData"; 
	public static final String PRIMARY_REVIEW_DATA_WITH_ADVICE = "primaryReviewDataWithAdvice"; 
	public static final String PRIMARY_TAB_LABEL = "primaryTabLabel"; 
	public static final String PRINT_FILE = "printFile";
	public static final String PRIORITY = "priority";
	public static final String PRIVATE = "private";
	public static final String PROMPT = "prompt";
	public static final String PROJECT = "project";
	public static final String PUBLIC = "public";
	public static final String PV_VALUE = "pvValue";
	public static final String PV_MONITOR_ACTIVE = "pvMonitorActive";
	public static final String PV_MONITOR_CONFIG = "pvMonitorConfig";
	public static final String PV_MONITOR_STATUS = "pvMonitorStatus";
	public static final String QUEUE = "queue";
	public static final String RAMP_TIME = "rampTime";
	public static final String RECIPE = "recipe";
	public static final String RECIPE_DATA_FOLDER = "SFC";
	public static final String RECIPE_LOCATION = "recipeLocation"; 
	public static final String RECIPE_CHART_STEP_LOCATION = "responseLocation"; 
	public static final String REGISTER_AND_DISPLAY = "registerAndDisplay";
	public static final String REGISTER_ONLY = "registerOnly";
	public static final String RESPONSE_KEY_AND_ATTRIBUTE = "responseKeyAndAttribute";
	public static final String RESULTS_MODE = "resultsMode"; 
	public static final String REQUIRE_ALL_INPUTS = "requireAllInputs";
	public static final String REVIEW_DATA_WITH_ADVICE = "reviewDataWithAdvice"; 
	public static final String REVIEW_DATA_WINDOW = "reviewDataWindow";
	public static final String REVIEW_FLOWS_WINDOW = "reviewFlowsWindow";
	public static final String REVIEW_FLOWS = "reviewFlows";
	public static final String ROW_KEY = "rowKey"; 
	public static final String RUN_TIME = "runTime";
	//public static final String ROW_KEYED = "rowKeyed"; 
	public static final String ROWS = "rows"; 
	public static final String RUNTIME = "runTime";    // Elapsed time
	public static final String S88_LEVEL = "s88Level";
	public static final String SCALE = "scale";
	public static final String SCREEN_HEADER = "screenHeader";
	public static final String SECONDARY_REVIEW_DATA = "secondaryReviewData"; 
	public static final String SECONDARY_REVIEW_DATA_WITH_ADVICE = "secondaryReviewDataWithAdvice"; 
	public static final String SECONDARY_SORT_KEY = "secondarySortKey"; 
	public static final String SECONDARY_TAB_LABEL = "secondaryTabLabel"; 
	public static final String SECURITY = "security";
	public static final String SEMI_AUTOMATIC = "semiAutomatic";
	public static final String SEQUENCE = "sequence";
	public static final String SERVER = "server";
	public static final String SESSION = "session";
	public static final String SESSIONS = "sessions";
	public static final String SETPOINT = "setpoint";
	public static final String SETPOINT_STATUS = "setpointStatus";
	public static final String SHOW_PRINT_DIALOG = "showPrintDialog";
	public static final String SINGLE = "single";
	public static final String SQL = "sql";
	public static final String STANDARD_DEVIATION = "stdDeviation";
	public static final String START_TIME = "startTime";
	public static final String STATIC = "static";
	public static final String STATE = "state";
	public static final String STATUS = "status";
	public static final String STEP_NAME = "name";
	public static final String STEP_TIME = "stepTime";
	public static final String STEP_TIMESTAMP = "stepTimestamp";
	public static final String SUCCESS = "Success";
	public static final String STRATEGY = "strategy";
	public static final String TAG = "tag";
	public static final String TAG_PATH = "tagPath";
	public static final String TARGET_VALUE = "targetValue";
	public static final String TIME = "time";
	public static final String TIMER_START = "timerStart";
	public static final String TIMING = "timing";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_BEHAVIOR = "timeoutBehavior";
	public static final String TIMEOUT_UNIT = "timeoutUnit";
	public static final String TIMER_CLEAR = "timerClear";
	public static final String TIMER_KEY = "timerKey";
	public static final String TIMER_LOCATION = "timerLocation";
	public static final String TIMER_SET = "timerSet";
	public static final String TIMESTAMP = "timestamp";
	public static final String TRANSLATION_ERROR = "Translation Error!";
	public static final String TOP_CENTER = "topCenter";
	public static final String TOP_LEFT = "topLeft";
	public static final String TOP_MESSAGE = "topMessage";
	public static final String TOP_RIGHT = "topRight";
	public static final String TYPE = "type";
	public static final String UNIT_SUFFIX = "Unit";
	public static final String UNITS = "units";
	public static final String UPDATE = "update";
	public static final String UPDATE_FREQUENCY = "updateFrequency";
	public static final String UPDATE_OR_CREATE = "updateOrCreate";
	public static final String USER = "user";
	public static final String UUID = "uuid";
	public static final String VALUE = "value";
	public static final String VALUE_TYPE = "valueType";
	public static final String VERBOSE = "verbose";
	public static final String VIEW_FILE = "viewFile";
	public static final String WAIT = "Wait";
	public static final String WARNING = "Warning";
	public static final String WATCH = "watch";
	public static final String WINDOW = "window";
	public static final String WINDOW_HEADER = "windowHeader";
	public static final String WINDOW_TITLE = "windowTitle";
	public static final String WRITE_CONFIRM = "writeConfirm";
	public static final String WRITE_CONFIRMED = "writeConfirmed";
	public static final String WRITE_OUTPUT_CONFIG = "writeOutputConfig";
	public static final String YES = "Yes";
	
	// Constants for EM-RECIPE-DATA
	public static final String CHG_LEV = "chg_lev";
	public static final String CTAG = "ctag";
	public static final String DSCR = "dscr";
	public static final String HILIM = "hilim";
	public static final String LOLIM = "lolim";
	public static final String MODATTR = "modattr";
	public static final String MODATTR_VAL = "modattr_val";
	public static final String PRES = "pres";
	public static final String RECC = "recc";
	public static final String STAG = "stag";

	// Timer (Write output/PV Monitoring/Download GUI) things
	// These are the states and the commands for the timer UDT
	public static final String TIMER_STATE = "state";
	public static final String TIMER_STATE_CANCEL = "cancel";
	public static final String TIMER_STATE_CLEAR = "clear";
	public static final String TIMER_STATE_PAUSE = "pause";
	public static final String TIMER_STATE_RESUME = "resume";
	public static final String TIMER_STATE_RUN = "run";
	public static final String TIMER_STATE_STOP = "stop";
	public static final String TIMER_RUN_MINUTES = "runTime";
	public static final String TIMER_START_TIME = "startTime";
	
	// scopes for recipe data:
	public static final String LOCAL = "local";
	public static final String PRIOR = "prior";
	public static final String SUPERIOR = "superior";
	public static final String NAMED = "named";
	public static final String GLOBAL = "global";  // same as Procedure, Unit Procedure
	public static final String OPERATION = "operation";	
	public static final String PHASE = "phase";
	public static final String REFERENCE = "reference";  // Used to implement pass-by-reference for libraries.
	
	// secondary sort order choices
	public static final String ALPHABETICAL = "alphabetical";
	public static final String ORDER = "order";
	
	// standard Ignition scopes:
	public static final String CHART_SCOPE = "chartScope";
	public static final String STEP_SCOPE = "stepScope";

	// recipe data value types
	public static final String FLOAT = "float";
	public static final String INT = "int";
	public static final String STRING = "string";
	public static final String BOOLEAN = "boolean";
	public static final String DATE_TIME = "date/time";
	
	//recipe data classes
	public static final String ARRAY = "array";
	public static final String INPUT = "input";
	public static final String OUTPUT_RAMP = "outputRamp";
	public static final String SIMPLE_VALUE = "simpleValue";
	public static final String SQC = "sqc";
	
	// Counting Modes (specifically for error counts but may be more generally applicable)
	public static final String COUNT_ABSOLUTE = "absolute";
	public static final String COUNT_INCREMENTAL = "incremental";
	
	// symbols for a TimeDelayStep unit choice
	// these have nothing to do with unit conversion, they
	// are unique to the TimeDelayStep
	public static final String DELAY_UNIT_SECOND = "SEC";
	public static final String DELAY_UNIT_MINUTE = "MIN";
	public static final String DELAY_UNIT_HOUR = "HR";

	// other
	public static final String MESSAGE_ID = "messageId";
	public static final String BY_NAME = "stepsByName";
	public static final String ENCLOSING_STEP_SCOPE_KEY = "enclosingStep";
	public static final String RESPONSE = "response";

	// Recipe Data Related choices:
	public static final String[] RECIPE_STATIC_STRATEGY_CHOICES = {STATIC, RECIPE};
	public static final String[] RECIPE_DATA_CLASS_CHOICES = {ARRAY, INPUT, OUTPUT, OUTPUT_RAMP, RECIPE, SIMPLE_VALUE, SQC};
	public static String[] RECIPE_LOCATION_CHOICES = {LOCAL, SUPERIOR, PHASE, OPERATION, GLOBAL, REFERENCE};
	public static String[] RECIPE_PLUS_VALUE_LOCATION_CHOICES = {BLANK, VALUE, LOCAL, SUPERIOR, PHASE, OPERATION, GLOBAL, REFERENCE};
	public static String[] RECIPE_PLUS_TAG_LOCATION_CHOICES = {BLANK, LOCAL, SUPERIOR, PHASE, OPERATION, GLOBAL, REFERENCE, TAG};
	public static String[] RECIPE_PLUS_CHART_STEP_CHOICES = {LOCAL, SUPERIOR, PHASE, OPERATION, GLOBAL, REFERENCE, CHART_SCOPE, STEP_SCOPE};
	
	// General choices
	public static final String[] AUDIT_LEVEL_CHOICES = {OFF, LOW, HIGH};
	public static final String[] AUTO_MODE_CHOICES = {AUTOMATIC, SEMI_AUTOMATIC};
	public static final String[] COLLECT_DATA_VALUE_TYPE_CHOICES = {CURRENT, AVERAGE, MINIMUM, MAXIMUM, STANDARD_DEVIATION};
	public static final String[] COMPUTER_CHOICES = {SERVER, LOCAL};
	public static final String[] COUNT_MODE_CHOICES = {COUNT_ABSOLUTE, COUNT_INCREMENTAL};
	public static final String[] FETCH_MODE_CHOICES = {SINGLE, MULTIPLE};
	public static final String[] FILE_LOCATION_CHOICES = {GATEWAY, CLIENT};
	public static final String[] KEY_MODE_CHOICES = {STATIC, DYNAMIC};
	public static final String[] MONITOR_DOWNLOADS_LABEL_CHOICES = {NAME, ITEM_ID};
	public static final String[] OC_ALERT_WINDOW_TYPE_CHOICES = {NORMAL, LARGE_TEXT};
	public static final String[] OUTPUT_VALUE_TYPE_CHOICES = {MODE, SETPOINT, OUTPUT, VALUE};
	public static final String[] POSITION_CHOICES = {CENTER, TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT};
	public static final String[] PRIORITY_CHOICES = {INFO, WARNING, ERROR};
	public static final String[] PV_TARGET_TYPE_CHOICES = {SETPOINT, VALUE, TAG, RECIPE};
	public static final String[] PV_STRATEGY_CHOICES = {MONITOR, WATCH};
	public static final String[] PV_LIMITS_CHOICES = {HIGH_LOW, HIGH, LOW};
	public static final String[] PV_DOWNLOAD_CHOICES = {IMMEDIATE, WAIT};
	public static final String[] PV_TYPE_CHOICES = {ABS, PCT};
	public static final String[] RESULTS_MODE_CHOICES = {UPDATE, UPDATE_OR_CREATE};
	public static final String[] SECONDARY_SORT_KEY_CHOICES = {ALPHABETICAL, ORDER};
	public static final String[] SECURITY_CHOICES = {PUBLIC, PRIVATE};
	public static final String[] TIMEOUT_BEHAVIOR_CHOICES = {ABORT, TIMEOUT, DEFAULT_VALUE};
	public static final String[] TIME_DELAY_UNIT_CHOICES = {DELAY_UNIT_SECOND, DELAY_UNIT_MINUTE, DELAY_UNIT_HOUR};
	public static final String[] TIME_LIMIT_STRATEGY_CHOICES = {NO_LIMIT, STATIC, RECIPE};
	public static final String[] TIME_DELAY_STRATEGY_CHOICES = {STATIC, RECIPE, CALLBACK, TAG, CHART_SCOPE};
	public static final String[] UNIT_CHOICES = {""};    // To be replaced at run-time
	public static final String[] VALUE_TYPE_CHOICES = {BOOLEAN, DATE_TIME,FLOAT,INT, STRING};
	
	public static final String SFC_DOWNLOAD_KEY_WINDOW = "SFC/DownloadKey";
	public static final String SFC_INPUT_WINDOW = "SFC/Input";
	public static final String SFC_MANUAL_DATA_WINDOW = "SFC/ManualDataEntry";
	public static final String SFC_MONITOR_DOWNLOADS_WINDOW = "SFC/MonitorDownloads";
	public static final String SFC_REVIEW_DATA_WINDOW = "SFC/ReviewData";
	public static final String SFC_REVIEW_FLOWS_WINDOW = "SFC/ReviewFlows";
	public static final String SFC_SAVE_DATA_WINDOW = "SFC/SaveData";
	public static final String SFC_SELECT_INPUT_WINDOW = "SFC/SelectInput";
	public static final String SFC_CONTROL_PANEL_WINDOW = "SFC/ControlPanel";
	public static final String SFC_NOTIFICATION_WINDOW = "SFC/Notification";
	public static final String MESSAGE_QUEUE_WINDOW = "Queue/Message Queue";
}
