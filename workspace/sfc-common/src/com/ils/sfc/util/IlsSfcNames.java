package com.ils.sfc.util;

/** A single place to define names that need to be shared between Java and Jython. */
public class IlsSfcNames {

	// step properties:
	public static final String ACK_REQUIRED = "ackRequired";
	public static final String AUDIT_LEVEL = "auditLevel";
	public static final String BOTTOM_CENTER = "bottomCenter";
	public static final String BOTTOM_LEFT = "bottomLeft";
	public static final String BOTTOM_RIGHT = "bottomRight";
	public static final String CALLBACK = "callback";
	public static final String CENTER = "center";
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
	public static final String DIRECTORY = "directory";
	public static final String DYNAMIC = "dynamic";
	public static final String ENABLE_PAUSE = "enablePause";
	public static final String ENABLE_RESUME = "enableResume";
	public static final String ENABLE_CANCEL = "enableCancel";
	public static final String ERROR = "Error";
	public static final String EXTENSION = "extension";
	public static final String FETCH_MODE = "fetchMode";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String HIGH = "High";
	public static final String INFO = "Info";
	public static final String LOW = "Low";
	public static final String KEY = "key";
	public static final String KEY_MODE = "keyMode";
	public static final String LABEL = "label";
	public static final String MESSAGE = "message";
	public static final String METHOD = "method";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAXIMUM_VALUE = "maximumValue";
	public static final String MULTIPLE = "multiple";
	public static final String NAME = "name";
	public static final String OFF = "Off";
	public static final String POSITION = "position";
	public static final String POST_TO_QUEUE = "postToQueue";
	public static final String POST_NOTIFICATION = "postNotification";
	public static final String PRINT_FILE = "printFile";
	public static final String PRIORITY = "priority";
	public static final String PRIVATE = "private";
	public static final String PROMPT = "prompt";
	public static final String PUBLIC = "public";
	public static final String QUEUE = "queue";
	public static final String RECIPE = "recipe";
	public static final String RECIPE_LOCATION = "recipeLocation"; 
	public static final String RESULTS_MODE = "resultsMode"; 
	public static final String SCALE = "scale";
	public static final String SECURITY = "security";
	public static final String SERVER = "server";
	public static final String SHOW_PRINT_DIALOG = "showPrintDialog";
	public static final String SINGLE = "single";
	public static final String SQL = "sql";
	public static final String STATIC = "static";
	public static final String STRATEGY = "strategy";
	public static final String TAG_PATH = "tagPath";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_UNIT = "timeoutUnit";
	public static final String TIMESTAMP = "timestamp";
	public static final String TOP_CENTER = "topCenter";
	public static final String TOP_LEFT = "topLeft";
	public static final String TOP_RIGHT = "topRight";
	public static final String UPDATE = "update";
	public static final String UPDATE_OR_CREATE = "updateOrCreate";
	public static final String VALUE = "value";
	public static final String VIEW_FILE = "viewFile";
	public static final String WARNING = "Warning";
	public static final String WINDOW = "window";


	// scopes for recipe data:
	public static final String LOCAL = "local";
	public static final String PREVIOUS = "previous";
	public static final String SUPERIOR = "superior";
	public static final String NAMED = "named";
	public static final String GLOBAL = "global";
	public static final String PROCEDURE = "procedure";
	public static final String PHASE = "phase";
	public static final String OPERATION = "operation";	

	
	// frequently used units
	// These must correspond to the actual unit names in the DB:
	public static final String SECOND= "SECOND";
	public static final String MINUTE = "MINUTE";
	public static final String HOUR = "HOUR";
	public static final String TIME_UNIT_TYPE = "TIME";

	// other
	public static final String MESSAGE_ID = "messageId";
	public static final String BY_NAME = "stepsByName";

	// choices:
	public static final String[] RECIPE_STATIC_STRATEGY_CHOICES = {STATIC, RECIPE};
	public static final String[] RECIPE_LOCATION_CHOICES = {
		LOCAL, PREVIOUS, SUPERIOR, NAMED, GLOBAL, PROCEDURE, PHASE, OPERATION, VALUE};
	public static final String[] TIME_DELAY_STRATEGY_CHOICES = {STATIC, RECIPE, CALLBACK};
	public static final String[] RESULTS_MODE_CHOICES = {UPDATE, UPDATE_OR_CREATE};
	public static final String[] FETCH_MODE_CHOICES = {SINGLE, MULTIPLE};
	public static final String[] KEY_MODE_CHOICES = {STATIC, DYNAMIC};
	public static final String[] COMPUTER_CHOICES = {SERVER, LOCAL};
	public static final String[] POSITION_CHOICES = {CENTER, TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT};
	public static final String[] SECURITY_CHOICES = {PUBLIC, PRIVATE};
	public static final String[] PRIORITY_CHOICES = {INFO, WARNING, ERROR};
	public static final String[] AUDIT_LEVEL_CHOICES = {OFF, LOW, HIGH};
}
