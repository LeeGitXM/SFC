package com.ils.sfc.designer.recipeEditor;

import java.util.HashMap;
import java.util.Map;

public class RecipeDataTypes {
	
	// Data types
	public static final String GROUP = "Group";
	public static final String SINGLE_VALUE = "Value";
	public static final String VALUE_ARRAY = "Value Array";
	public static final String INPUT = "Input";
	public static final String OUTPUT = "Output";
	public static final String OUTPUT_RAMP = "Output Ramp";
	public static final String MATRIX = "Matrix";
	public static final String QUANTITY_ARRAY = "Quantity Array";
	public static final String QUANTITY_LIST = "Quantity List";
	public static final String SQC = "SQC";
	public static final String SEQUENCE = "Sequence";
	public static final String TEXT_LIST = "Text List";
	
	public static final String[] RECIPE_DATA_TYPES = {SINGLE_VALUE,  VALUE_ARRAY, GROUP, INPUT, OUTPUT,
		OUTPUT_RAMP, MATRIX, QUANTITY_ARRAY, SQC, QUANTITY_LIST, SEQUENCE, TEXT_LIST, };
	
	// Common properties
	public static final String TYPE = "type";
	public static final String LABEL = "label";
	public static final String DESCRIPTION = "description";
	public static final String ADVICE = "advice";
	public static final String HELP = "help";
	public static final String UNITS = "units";
	
	public static void addCommonProperties(String type, Map<String,Object> map) {
		map.put(TYPE, type);
		map.put(LABEL, type);
		map.put(DESCRIPTION, type);
		map.put(ADVICE, type);
		map.put(HELP, type);
	}

	public static void addUnitProperties(Map<String,Object> map) {
		map.put(UNITS, "");
	}

	public static Map<String,Object> createGroup() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(GROUP, map);
		return map;
	}

	public static final String VAL = "val";
	
	public static Map<String,Object> createSingleValue() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(SINGLE_VALUE, map);
		addUnitProperties(map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}

	public static Map<String,Object> createValueArray() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(VALUE_ARRAY, map);
		addUnitProperties(map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}

	public static final String TAG = "tag";
	public static final String VAL_TYPE = "valType";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_TEXT = "errorText";
	public static final String PV_MONITOR_STATUS = "pvMonitorStatus";
	public static final String PV_MONITOR_ACTIVE = "pvMonitorActive";
	public static final String PV_VALUE = "pvValue";

	private static void addIOProperties(Map<String, Object> map) {
		map.put(TAG, "");
		map.put(VAL, "");
		map.put(VAL_TYPE, "");
		map.put(ERROR_CODE, "");
		map.put(ERROR_TEXT, "");
		map.put(PV_MONITOR_STATUS, "");
		map.put(PV_MONITOR_ACTIVE, "");
		map.put(PV_VALUE, "");		
	}

	
	public static Map<String,Object> createInput() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(INPUT, map);
		addUnitProperties(map);
		addIOProperties(map);
		return map;
	}

	public static final String WRITE_CONFIRM = "writeConfirm";
	public static final String WRITE_CONFIRMED = "writeConfirmed";
	public static final String DOWNLOAD = "download";
	public static final String DOWNLOAD_STATUS = "downloadStatus";		
	public static final String TIMING = "timing";
	public static final String STEP_TIME = "stepTime";
	public static final String STEP_TIMESTAMP = "stepTimestamp";
	public static final String TEST_SETPOINT_FOR_ZERO = "testSetpointForZero";
	public static final String MAX_TIMING = "maxTiming";

	public static void addOutputProperties(Map<String,Object> map) {
		map.put(WRITE_CONFIRM, "");
		map.put(WRITE_CONFIRMED, "");
		map.put(DOWNLOAD, "");
		map.put(DOWNLOAD_STATUS, "");
		map.put(TIMING, "");
		map.put(STEP_TIME, "");
		map.put(STEP_TIMESTAMP, "");
		map.put(TEST_SETPOINT_FOR_ZERO, "");
		map.put(MAX_TIMING, "");		
	}

	public static Map<String,Object> createOutput() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(OUTPUT, map);
		addUnitProperties(map);
		addIOProperties(map);
		addOutputProperties(map);
		return map;
	}

	public static final String RAMP_TIME = "rampTime";
	public static final String UPDATE_FREQUENCY = "updateFrequency";

	public static Map<String,Object> createOutputRamp() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(OUTPUT_RAMP, map);
		addUnitProperties(map);
		addIOProperties(map);
		addOutputProperties(map);
		map.put(RAMP_TIME, "");
		map.put(UPDATE_FREQUENCY, "");
		return map;
	}

	public static Map<String,Object> createMatrix() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(MATRIX, map);
		addUnitProperties(map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}
	
	public static Map<String,Object> createQuantityArray() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(QUANTITY_ARRAY, map);
		addUnitProperties(map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}

	public static Map<String,Object> createQuantityList() {
		Map<String,Object> map = new HashMap<String,Object>();
		addUnitProperties(map);
		addCommonProperties(QUANTITY_LIST, map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}

	public static final String LOW_LIMIT = "lowLimit";
	public static final String HIGH_LIMIT =  "highLimit";
	public static final String TARGET = "target";

	public static Map<String,Object> createSQC() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(SQC, map);
		map.put(LOW_LIMIT, null);
		map.put(HIGH_LIMIT, null);
		map.put(TARGET, null);
		return map;
	}

	public static Map<String,Object> createSequence() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(SEQUENCE, map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}

	public static Map<String,Object> createTextList() {
		Map<String,Object> map = new HashMap<String,Object>();
		addCommonProperties(TEXT_LIST, map);
		map.put(VAL, Double.valueOf(0.));
		return map;
	}
	
}
