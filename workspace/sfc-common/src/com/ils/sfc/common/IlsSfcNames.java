package com.ils.sfc.common;

/** A single place to define names that need to be shared between Java and Jython. */
public class IlsSfcNames {

	public static final String ACKNOWLEDGEMENT_REQUIRED = "acknowledge";
	public static final String CALLBACK = "callback";
	public static final String COMMAND = "callback";
	public static final String DESCRIPTION = "description";
	public static final String DELAY = "delay";
	public static final String DELAY_UNIT = "delayUnit";
	public static final String KEY = "key";
	public static final String MESSAGE = "message";
	public static final String MESSAGE_ID = "messageId";
	public static final String NAME = "name";
	public static final String POST_TO_QUEUE = "postToQueue";
	public static final String POST_NOTIFICATION = "postNotification";
	public static final String PRIORITY = "priority";
	public static final String PROMPT = "prompt";
	public static final String QUEUE = "queue";
	public static final String RESPONSE_HANDLER = "sfcInputResponse";
	public static final String STATUS = "status";
	public static final String STRATEGY = "strategy";
	public static final String TIMEOUT = "timeout";
	public static final String TIMEOUT_UNIT = "timeoutUnit";
	public static final String TIME_UNIT_TYPE = "Time";

	public static final String RECIPE_LOCATION = "recipeLocation";
	public static final String LOCAL = "Local";
	public static final String PREVIOUS = "Previous";
	public static final String SUPERIOR = "Superior";
	public static final String NAMED = "Named";
	public static final String PROCEDURE = "Procedure";
	public static final String PHASE = "Phase";
	public static final String OPERATION = "Operation";	
	public static final String[] RECIPE_LOCATION_CHOICES = {
		LOCAL, PREVIOUS, SUPERIOR, NAMED, PROCEDURE, PHASE, OPERATION};

			
	// These must correspond to the actual unit names in the DB:
	public static final String SECOND= "SECOND";
	public static final String MINUTE = "MINUTE";
	public static final String HOUR = "HOUR";

}
