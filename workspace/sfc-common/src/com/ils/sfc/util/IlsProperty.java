package com.ils.sfc.util;

import java.util.HashSet;
import java.util.Set;

import com.inductiveautomation.ignition.common.config.BasicProperty;

@SuppressWarnings("serial")
public class IlsProperty<T> extends BasicProperty<T> implements java.io.Serializable {
	private int sortOrder;
	private String[] choices;
	
	public static final Set<String> ignoreProperties = new HashSet<String>();
	static {
		ignoreProperties.add("location");
		ignoreProperties.add("location-adjustment");
		ignoreProperties.add("id");
		ignoreProperties.add("type");
		ignoreProperties.add("factory-id");
	}

    public static final IlsProperty<Boolean> ACK_REQUIRED = new IlsProperty<Boolean>(IlsSfcNames.ACK_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> AUDIT_LEVEL = new IlsProperty<String>(IlsSfcNames.AUDIT_LEVEL, String.class, IlsSfcNames.AUDIT_LEVEL_CHOICES[0], IlsSfcNames.AUDIT_LEVEL_CHOICES);
    public static final IlsProperty<String> CALLBACK = new IlsProperty<String>(IlsSfcNames.CALLBACK, String.class, "");
    public static final IlsProperty<String> CHOICES_RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.CHOICES_RECIPE_LOCATION, String.class, "", IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> CHOICES_KEY = new IlsProperty<String>(IlsSfcNames.CHOICES_KEY, String.class, "");
    public static final IlsProperty<String> COMMAND = new IlsProperty<String>(IlsSfcNames.COMMAND, String.class, "");
    public static final IlsProperty<String> COMPUTER = new IlsProperty<String>(IlsSfcNames.COMPUTER, String.class, IlsSfcNames.COMPUTER_CHOICES[0], IlsSfcNames.COMPUTER_CHOICES);
    public static final IlsProperty<String> DATABASE = new IlsProperty<String>(IlsSfcNames.DATABASE, String.class, "");
    public static final IlsProperty<Integer> DELAY = new IlsProperty<Integer>(IlsSfcNames.DELAY, Integer.class, 0);
    public static final IlsProperty<String> DELAY_UNIT = new IlsProperty<String>(IlsSfcNames.DELAY_UNIT, String.class, IlsSfcNames.MINUTE);  
    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
    public static final IlsProperty<String> DIALOG = new IlsProperty<String>(IlsSfcNames.DIALOG, String.class, "");
    public static final IlsProperty<String> DIRECTORY = new IlsProperty<String>(IlsSfcNames.DIRECTORY, String.class, "");
    public static final IlsProperty<Boolean> ENABLE = new IlsProperty<Boolean>(IlsSfcNames.ENABLE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> EXTENSION = new IlsProperty<String>(IlsSfcNames.EXTENSION, String.class, ".txt");
    public static final IlsProperty<String> FETCH_MODE = new IlsProperty<String>(IlsSfcNames.FETCH_MODE, String.class, "", IlsSfcNames.FETCH_MODE_CHOICES);
    public static final IlsProperty<String> FILENAME = new IlsProperty<String>(IlsSfcNames.FILENAME, String.class, "");
    public static final IlsProperty<String> KEY = new IlsProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final IlsProperty<String> KEY_MODE = new IlsProperty<String>(IlsSfcNames.KEY_MODE, String.class, "", IlsSfcNames.KEY_MODE_CHOICES);
    public static final IlsProperty<String> LABEL = new IlsProperty<String>(IlsSfcNames.LABEL, String.class, "");
    public static final IlsProperty<Double> MAXIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MAXIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
    public static final IlsProperty<String> METHOD = new IlsProperty<String>(IlsSfcNames.METHOD, String.class, "");
    public static final IlsProperty<Double> MINIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MINIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> POSITION = new IlsProperty<String>(IlsSfcNames.POSITION, String.class, IlsSfcNames.POSITION_CHOICES[0], IlsSfcNames.POSITION_CHOICES);
    public static final IlsProperty<Boolean> POST_NOTIFICATION = new IlsProperty<Boolean>(IlsSfcNames.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Boolean> POST_TO_QUEUE = new IlsProperty<Boolean>(IlsSfcNames.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> PROMPT = new IlsProperty<String>(IlsSfcNames.PROMPT, String.class, "");
    public static final IlsProperty<Boolean> PRINT_FILE = new IlsProperty<Boolean>(IlsSfcNames.PRINT_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> PRIORITY = new IlsProperty<String>(IlsSfcNames.PRIORITY, String.class, IlsSfcNames.PRIORITY_CHOICES[0], IlsSfcNames.PRIORITY_CHOICES);
    public static final IlsProperty<String> QUEUE = new IlsProperty<String>(IlsSfcNames.QUEUE, String.class, "");
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "", IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> RESULTS_MODE = new IlsProperty<String>(IlsSfcNames.RESULTS_MODE, String.class, "", IlsSfcNames.RESULTS_MODE_CHOICES);
    public static final IlsProperty<String> SECURITY = new IlsProperty<String>(IlsSfcNames.SECURITY, String.class, IlsSfcNames.SECURITY_CHOICES[0], IlsSfcNames.SECURITY_CHOICES);
    public static final IlsProperty<Boolean> SHOW_PRINT_DIALOG = new IlsProperty<Boolean>(IlsSfcNames.SHOW_PRINT_DIALOG, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> SQL = new IlsProperty<String>(IlsSfcNames.SQL, String.class, "");
    public static final IlsProperty<String> TAG_PATH = new IlsProperty<String>(IlsSfcNames.TAG_PATH, String.class, "");
    public static final IlsProperty<String> TIME_DELAY_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, "", IlsSfcNames.TIME_DELAY_STRATEGY_CHOICES);
    public static final IlsProperty<String> RECIPE_STATIC_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, "", IlsSfcNames.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final IlsProperty<Double> SCALE = new IlsProperty<Double>(IlsSfcNames.SCALE, Double.class, .5);
    public static final IlsProperty<Integer> TIMEOUT = new IlsProperty<Integer>(IlsSfcNames.TIMEOUT, Integer.class, 0);
    public static final IlsProperty<String> TIMEOUT_UNIT = new IlsProperty<String>(IlsSfcNames.TIMEOUT_UNIT, String.class, IlsSfcNames.MINUTE);
    public static final IlsProperty<Boolean> TIMESTAMP = new IlsProperty<Boolean>(IlsSfcNames.TIMESTAMP, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<Boolean> VIEW_FILE = new IlsProperty<Boolean>(IlsSfcNames.VIEW_FILE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> WINDOW = new IlsProperty<String>(IlsSfcNames.WINDOW, String.class, "");

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
		
}
