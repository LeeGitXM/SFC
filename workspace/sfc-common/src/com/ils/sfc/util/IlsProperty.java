package com.ils.sfc.util;

import java.util.HashSet;
import java.util.Set;

import com.inductiveautomation.ignition.common.config.BasicProperty;

@SuppressWarnings("serial")
public class IlsProperty<T> extends BasicProperty<T> {
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

    public static final IlsProperty<Boolean> ACKNOWLEDGEMENT_REQUIRED = new IlsProperty<Boolean>(IlsSfcNames.ACKNOWLEDGEMENT_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> CALLBACK = new IlsProperty<String>(IlsSfcNames.CALLBACK, String.class, "");
    public static final IlsProperty<String> CHOICES_RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.CHOICES_RECIPE_LOCATION, String.class, "", IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> CHOICES_KEY = new IlsProperty<String>(IlsSfcNames.CHOICES_KEY, String.class, "");
    public static final IlsProperty<String> COMMAND = new IlsProperty<String>(IlsSfcNames.COMMAND, String.class, "");
    public static final IlsProperty<String> DATABASE = new IlsProperty<String>(IlsSfcNames.DATABASE, String.class, "");
    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
    public static final IlsProperty<String> DIALOG = new IlsProperty<String>(IlsSfcNames.DIALOG, String.class, "");
    public static final IlsProperty<Boolean> ENABLE = new IlsProperty<Boolean>(IlsSfcNames.ENABLE, Boolean.class, Boolean.TRUE);
    public static final IlsProperty<String> FETCH_MODE = new IlsProperty<String>(IlsSfcNames.FETCH_MODE, String.class, "", IlsSfcNames.FETCH_MODE_CHOICES);
    public static final IlsProperty<String> KEY = new IlsProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final IlsProperty<String> KEY_MODE = new IlsProperty<String>(IlsSfcNames.KEY_MODE, String.class, "", IlsSfcNames.KEY_MODE_CHOICES);
    public static final IlsProperty<Double> MAXIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MAXIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
    public static final IlsProperty<String> METHOD = new IlsProperty<String>(IlsSfcNames.METHOD, String.class, "");
    public static final IlsProperty<Double> MINIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MINIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<Boolean> POST_TO_QUEUE = new IlsProperty<Boolean>(IlsSfcNames.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> PROMPT = new IlsProperty<String>(IlsSfcNames.PROMPT, String.class, "");
    public static final IlsProperty<String> PRIORITY = new IlsProperty<String>(IlsSfcNames.PRIORITY, String.class, "");
    public static final IlsProperty<String> QUEUE = new IlsProperty<String>(IlsSfcNames.QUEUE, String.class, "");
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "", IlsSfcNames.RECIPE_LOCATION_CHOICES);
    public static final IlsProperty<String> RESULTS_MODE = new IlsProperty<String>(IlsSfcNames.RESULTS_MODE, String.class, "", IlsSfcNames.RESULTS_MODE_CHOICES);
    public static final IlsProperty<String> SQL = new IlsProperty<String>(IlsSfcNames.SQL, String.class, "");
    public static final IlsProperty<String> TAG_PATH = new IlsProperty<String>(IlsSfcNames.TAG_PATH, String.class, "");
    public static final IlsProperty<String> TIME_DELAY_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, "", IlsSfcNames.TIME_DELAY_STRATEGY_CHOICES);
    public static final IlsProperty<String> RECIPE_STATIC_STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, "", IlsSfcNames.RECIPE_STATIC_STRATEGY_CHOICES);
    public static final IlsProperty<String> STATUS = new IlsProperty<String>(IlsSfcNames.STATUS, String.class, "Info");    
    public static final IlsProperty<Integer> TIMEOUT = new IlsProperty<Integer>(IlsSfcNames.TIMEOUT, Integer.class, 0);
    public static final IlsProperty<String> TIMEOUT_UNIT = new IlsProperty<String>(IlsSfcNames.TIMEOUT_UNIT, String.class, IlsSfcNames.MINUTE);

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