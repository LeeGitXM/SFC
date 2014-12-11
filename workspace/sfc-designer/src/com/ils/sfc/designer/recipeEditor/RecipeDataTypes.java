package com.ils.sfc.designer.recipeEditor;

import com.ils.sfc.designer.recipeEditor.CreateRecipeDataDialog.Attribute;

public class RecipeDataTypes {
	// Data types
	public static final String GROUP = "Group";
	public static final String INPUT = "Input";
	public static final String OUTPUT = "Output";
	public static final String OUTPUT_RAMP = "Output Ramp";
	public static final String MATRIX = "Matrix";
	public static final String QUANTITY_ARRAY = "Quantity Array";
	public static final String SQC = "SQC";
	//public static final String STRUCTURE = "Structure";
	public static final String QUANTITY_LIST = "Quantity List";
	public static final String SEQUENCE = "Sequence";
	public static final String TEXT_LIST = "Text List";
	public static final String VALUE_ARRAY = "Value Array";
	public static final String VALUE = "Value";
	public static final String[] RECIPE_DATA_TYPES = {GROUP, INPUT, OUTPUT,
		OUTPUT_RAMP, MATRIX, QUANTITY_ARRAY, SQC, QUANTITY_LIST, SEQUENCE,
		TEXT_LIST, VALUE_ARRAY, VALUE};
	
	// keys
	public static final String UNITS = "units";
	
	// IO attributes
	public static final Attribute TAG = new Attribute("tag", "");
	public static final Attribute VAL = new Attribute("val", "");
	public static final Attribute VAL_TYPE = new Attribute("valType", "");
	public static final Attribute ERROR_CODE = new Attribute("errorCode", "");
	public static final Attribute ERROR_TEXT = new Attribute("errorText", "");
	public static final Attribute PV_MONITOR_STATUS = new Attribute("pvMonitorStatus", "");
	public static final Attribute PV_MONITOR_ACTIVE = new Attribute("pvMonitorActive", "");
	public static final Attribute PV_VALUE = new Attribute("pvValue", "");
	public static final Attribute[] IO_ATTRIBUTES = {TAG, VAL, VAL_TYPE, ERROR_CODE,
		ERROR_TEXT, PV_MONITOR_STATUS, PV_MONITOR_ACTIVE, PV_VALUE};
	
	// Misc attributes
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String DATA = "data";
	
	// Output attributes
	public static final Attribute WRITE_CONFIRM = new Attribute("writeConfirm", Boolean.FALSE);
	public static final Attribute WRITE_CONFIRMED = new Attribute("writeConfirmed", Boolean.FALSE);
	public static final Attribute DOWNLOAD = new Attribute("download", Boolean.FALSE);
	public static final Attribute DOWNLOAD_STATUS = new Attribute("downloadStatus", "");
	public static final Attribute TIMING = new Attribute("timing", ""); 
	public static final Attribute STEP_TIME = new Attribute("stepTime", ""); 
	public static final Attribute STEP_TIMESTAMP = new Attribute("stepTimestamp", ""); 
	public static final Attribute TEST_SETPOINT_FOR_ZERO = new Attribute("testSetpointForZero", Boolean.TRUE);
	public static final Attribute MAX_TIMIMG = new Attribute("maxTiming", "");
	public static final Attribute[] OUTPUT_ATTRIBUTES = {WRITE_CONFIRM, WRITE_CONFIRMED,
		DOWNLOAD, DOWNLOAD_STATUS, TIMING, STEP_TIME, STEP_TIMESTAMP, TEST_SETPOINT_FOR_ZERO,
		MAX_TIMIMG };
	
	// SQC Attributes
	public static final Attribute LOW_LIMIT = new Attribute( "lowLimit", "");
	public static final Attribute HIGH_LIMIT = new Attribute( "highLimit", "");
	public static final Attribute TARGET = new Attribute( "target", "");
	public static final Attribute[] SQC_ATTRIBUTES = {LOW_LIMIT, HIGH_LIMIT, TARGET};
}
