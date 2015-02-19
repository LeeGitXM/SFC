package com.ils.sfc.common;


/** A single place to define names that need to be shared between Java and Jython. */
public class IlsSfcNames {

	// step properties:
	public static final String ACK_REQUIRED = "ackRequired";
	public static final String ADVICE = "advice";
	public static final String AUDIT_LEVEL = "auditLevel";
	public static final String AUTO_MODE = "autoMode";
	public static final String AUTOMATIC = "automatic";
	public static final String BOTTOM_CENTER = "bottomCenter";
	public static final String BOTTOM_LEFT = "bottomLeft";
	public static final String BOTTOM_RIGHT = "bottomRight";
	public static final String BUTTON_LABEL = "buttonLabel";
	public static final String BUTTON_KEY = "buttonKey";
	public static final String BUTTON_KEY_LOCATION = "buttonKeyLocation";
	public static final String CALLBACK = "callback";
	public static final String CATEGORY = "category";
	public static final String CENTER = "center";
	public static final String CHILDREN = "children";
	public static final String CLASS = "class";
	public static final String COMMAND = "callback";
	public static final String CHOICES= "choices";
	public static final String CHOICES_KEY = "choicesKey";
	public static final String CHOICES_RECIPE_LOCATION = "choicesRecipeLocation";
	public static final String COMPUTER = "computer";
	public static final String DATA = "data";
	public static final String DATABASE = "database";
	public static final String DESCRIPTION = "description";
	public static final String DELAY = "delay";
	public static final String DELAY_UNIT = "delayUnit";
	public static final String DIALOG = "dialog";
	public static final String DIALOG_TEMPLATE = "dialogTemplate";
	public static final String DIRECTORY = "directory";
	public static final String DISPLAY_MODE = "displayMode";
	public static final String DOWNLOAD = "download";
	public static final String DOWNLOAD_STATUS = "downloadStatus";		
	public static final String DYNAMIC = "dynamic";
	public static final String ENABLE_PAUSE = "enablePause";
	public static final String ENABLE_RESUME = "enableResume";
	public static final String ENABLE_CANCEL = "enableCancel";
	public static final String ERROR = "Error";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_TEXT = "errorText";
	public static final String EXTENSION = "extension";
	public static final String FETCH_MODE = "fetchMode";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String GUI_LABEL = "guiLabel";
	public static final String GUI_UNITS = "guiUnits";
	public static final String HELP = "help";
	public static final String HIGH = "High";
	public static final String HIGH_LIMIT = "highLimit";
	public static final String ID = "id";
	public static final String INFO = "Info";
	public static final String LOW_LIMIT = "lowLimit";
	public static final String LOW = "Low";
	public static final String KEY = "key";
	public static final String KEY_MODE = "keyMode";
	public static final String LABEL = "label";
	public static final String MAX_TIMING = "maxTiming";
	public static final String MESSAGE = "message";
	public static final String METHOD = "method";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAXIMUM_VALUE = "maximumValue";
	public static final String MULTIPLE = "multiple";
	public static final String NAME = "name";
	public static final String NONE = "none";
	public static final String OFF = "Off";
	public static final String PARENT_GROUP = "parent-group";
	public static final String POSITION = "position";
	public static final String POST_TO_QUEUE = "postToQueue";
	public static final String POST_NOTIFICATION = "postNotification";
	public static final String POSTING_METHOD = "postingMethod";
	public static final String PRINT_FILE = "printFile";
	public static final String PRIORITY = "priority";
	public static final String PRIVATE = "private";
	public static final String PROMPT = "prompt";
	public static final String PUBLIC = "public";
	public static final String PV_VALUE = "pvValue";
	public static final String PV_MONITOR_ACTIVE = "pvMonitorActive";
	public static final String PV_MONITOR_STATUS = "pvMonitorStatus";
	public static final String QUEUE = "queue";
	public static final String RAMP_TIME = "rampTime";
	public static final String RECIPE = "recipe";
	public static final String RECIPE_LOCATION = "recipeLocation"; 
	public static final String REGISTER_AND_DISPLAY = "registerAndDisplay";
	public static final String REGISTER_ONLY = "registerOnly";
	public static final String RESULTS_MODE = "resultsMode"; 
	public static final String REVIEW_DATA = "reviewData"; 
	public static final String REVIEW_DATA_WITH_ADVICE = "reviewDataWithAdvice"; 
	public static final String REVIEW_FLOWS = "reviewFlows"; 
	public static final String S88_LEVEL = "s88Level";
	public static final String SCALE = "scale";
	public static final String SCREEN_HEADER = "screenHeader";
	public static final String SECURITY = "security";
	public static final String SEMI_AUTOMATIC = "semiAutomatic";
	public static final String SEQUENCE = "sequence";
	public static final String SERVER = "server";
	public static final String SHOW_PRINT_DIALOG = "showPrintDialog";
	public static final String SINGLE = "single";
	public static final String SQL = "sql";
	public static final String STATIC = "static";
	public static final String STEP_TIME = "stepTime";
	public static final String STEP_TIMESTAMP = "stepTimestamp";
	public static final String STRATEGY = "strategy";
	public static final String TAG = "tag";
	public static final String TAG_PATH = "tagPath";
	public static final String TARGET_VALUE = "targetValue";
	public static final String TIMING = "timing";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_UNIT = "timeoutUnit";
	public static final String TIMESTAMP = "timestamp";
	public static final String TOP_CENTER = "topCenter";
	public static final String TOP_LEFT = "topLeft";
	public static final String TOP_RIGHT = "topRight";
	public static final String TYPE = "type";
	public static final String UNIT_SUFFIX = "Unit";
	public static final String UNITS = "units";
	public static final String UPDATE = "update";
	public static final String UPDATE_FREQUENCY = "updateFrequency";
	public static final String UPDATE_OR_CREATE = "updateOrCreate";
	public static final String UUID = "uuid";
	public static final String VALUE = "value";
	public static final String VIEW_FILE = "viewFile";
	public static final String WARNING = "Warning";
	public static final String WINDOW = "window";
	public static final String WRITE_CONFIRM = "writeConfirm";
	public static final String WRITE_CONFIRMED = "writeConfirmed";


	// scopes for recipe data:
	public static final String LOCAL = "local";
	public static final String PREVIOUS = "previous";
	public static final String SUPERIOR = "superior";
	//public static final String NAMED = "named";
	public static final String GLOBAL = "global";
	public static final String OPERATION = "operation";	
	public static final String PHASE = "phase";

	
	// frequently used units
	// These must correspond to the actual unit names in the DB:
	public static final String SECOND= "SEC";
	public static final String MINUTE = "MIN";
	public static final String TIME_UNIT_TYPE = "TIME";

	// other
	public static final String MESSAGE_ID = "messageId";
	public static final String BY_NAME = "stepsByName";
	public static final String ENCLOSING_STEP_SCOPE_KEY = "enclosingStepScope";
	public static final String S88_LEVEL_KEY = "s88Level";

	// choices:
	public static final String[] RECIPE_STATIC_STRATEGY_CHOICES = {STATIC, RECIPE};
	public static String[] RECIPE_LOCATION_CHOICES = {LOCAL, PREVIOUS, SUPERIOR,
		GLOBAL, OPERATION, PHASE};
	public static final String[] TIME_DELAY_STRATEGY_CHOICES = {STATIC, RECIPE, CALLBACK};
	public static final String[] RESULTS_MODE_CHOICES = {UPDATE, UPDATE_OR_CREATE};
	public static final String[] FETCH_MODE_CHOICES = {SINGLE, MULTIPLE};
	public static final String[] KEY_MODE_CHOICES = {STATIC, DYNAMIC};
	public static final String[] AUTO_MODE_CHOICES = {AUTOMATIC, SEMI_AUTOMATIC};
	public static final String[] COMPUTER_CHOICES = {SERVER, LOCAL};
	public static final String[] POSITION_CHOICES = {CENTER, TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT};
	public static final String[] SECURITY_CHOICES = {PUBLIC, PRIVATE};
	public static final String[] PRIORITY_CHOICES = {INFO, WARNING, ERROR};
	public static final String[] AUDIT_LEVEL_CHOICES = {OFF, LOW, HIGH};
	public static final String[] DISPLAY_MODE_CHOICES = {REGISTER_AND_DISPLAY, REGISTER_ONLY};
	public static final String[] S88_LEVEL_CHOICES = {NONE, GLOBAL, OPERATION, PHASE};
}
