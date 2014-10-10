package com.ils.sfc.common;


import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface TimedDelayStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.TimedDelayStep";

    public static final Property<String> DESCRIPTION = new BasicProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");

    public static final Property<String> STRATEGY = new BasicProperty<String>(IlsSfcNames.STRATEGY, String.class, "");
    public static final Property<String> CALLBACK = new BasicProperty<String>(IlsSfcNames.CALLBACK, String.class, "");
    public static final Property<String> KEY = new BasicProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final Property<String> RECIPE_LOCATION = new BasicProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "");
    public static final Property<Boolean> POST_NOTIFICATION = new BasicProperty<Boolean>(IlsSfcNames.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final Property<Integer> DELAY = new BasicProperty<Integer>(IlsSfcNames.DELAY, Integer.class, 0);
    public static final Property<String> DELAY_UNIT = new BasicProperty<String>(IlsSfcNames.DELAY_UNIT, String.class, IlsSfcNames.MINUTE);
   
    public static final Property<?>[] properties = { 
    	DESCRIPTION, STRATEGY, CALLBACK, KEY, RECIPE_LOCATION, DELAY, DELAY_UNIT, POST_NOTIFICATION };
}
