package com.ils.sfc.util;

/** A single place to define names that need to be shared between Java and Jython. */
public class IlsSfcNames {

	// step properties:
	public static final String ACKNOWLEDGEMENT_REQUIRED = "acknowledge";
	public static final String CALLBACK = "callback";
	public static final String COMMAND = "callback";
	public static final String CHOICES= "choices";
	public static final String CHOICES_KEY = "choicesKey";
	public static final String CHOICES_RECIPE_LOCATION = "choicesRecipeLocation";
	public static final String DATABASE = "database";
	public static final String DESCRIPTION = "description";
	public static final String DELAY = "delay";
	public static final String DELAY_UNIT = "delayUnit";
	public static final String DIALOG = "dialog";
	public static final String DYNAMIC = "dynamic";
	public static final String ENABLE = "enable";
	public static final String FETCH_MODE = "fetchMode";
	public static final String KEY = "key";
	public static final String KEY_MODE = "keyMode";
	public static final String MESSAGE = "message";
	public static final String METHOD = "method";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAXIMUM_VALUE = "maximumValue";
	public static final String MULTIPLE = "multiple";
	public static final String NAME = "name";
	public static final String POST_TO_QUEUE = "postToQueue";
	public static final String POST_NOTIFICATION = "postNotification";
	public static final String PRIORITY = "priority";
	public static final String PROMPT = "prompt";
	public static final String QUEUE = "queue";
	public static final String RECIPE = "recipe";
	public static final String RECIPE_LOCATION = "recipeLocation"; 
	public static final String RESULTS_MODE = "resultsMode"; 
	public static final String SINGLE = "single";
	public static final String SQL = "sql";
	public static final String STATIC = "static";
	public static final String STATUS = "status";
	public static final String STRATEGY = "strategy";
	public static final String TAG_PATH = "tagPath";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_UNIT = "timeoutUnit";
	public static final String UPDATE = "update";
	public static final String UPDATE_OR_CREATE = "updateOrCreate";


	// scopes for recipe data:
	public static final String LOCAL = "local";
	public static final String PREVIOUS = "previous";
	public static final String SUPERIOR = "superior";
	public static final String NAMED = "named";
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
		LOCAL, PREVIOUS, SUPERIOR, NAMED, PROCEDURE, PHASE, OPERATION};
	public static final String[] TIME_DELAY_STRATEGY_CHOICES = {RECIPE, STATIC};
	public static final String[] RESULTS_MODE_CHOICES = {UPDATE, UPDATE_OR_CREATE};
	public static final String[] FETCH_MODE_CHOICES = {SINGLE, MULTIPLE};
	public static final String[] KEY_MODE_CHOICES = {STATIC, DYNAMIC};
}
