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

    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");

    public static final IlsProperty<String> STRATEGY = new IlsProperty<String>(IlsSfcNames.STRATEGY, String.class, "");
    public static final IlsProperty<String> CALLBACK = new IlsProperty<String>(IlsSfcNames.CALLBACK, String.class, "");
    public static final IlsProperty<String> KEY = new IlsProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "");
    public static final IlsProperty<Boolean> POST_NOTIFICATION = new IlsProperty<Boolean>(IlsSfcNames.POST_NOTIFICATION, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Integer> DELAY = new IlsProperty<Integer>(IlsSfcNames.DELAY, Integer.class, 0);
    public static final IlsProperty<String> DELAY_UNIT = new IlsProperty<String>(IlsSfcNames.DELAY_UNIT, String.class, IlsSfcNames.MINUTE);
   
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, STRATEGY, CALLBACK, KEY, RECIPE_LOCATION, DELAY, DELAY_UNIT, POST_NOTIFICATION };
}
