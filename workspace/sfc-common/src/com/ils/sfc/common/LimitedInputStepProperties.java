package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface LimitedInputStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.limitedInputStep";

    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");

    public static final IlsProperty<String> PROMPT = new IlsProperty<String>(IlsSfcNames.PROMPT, String.class, "");
    public static final IlsProperty<String> RECIPE_LOCATION = new IlsProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "");
    public static final IlsProperty<String> KEY = new IlsProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final IlsProperty<Double> MINIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MINIMUM_VALUE, Double.class, 0.);
    public static final IlsProperty<Double> MAXIMUM_VALUE = new IlsProperty<Double>(IlsSfcNames.MAXIMUM_VALUE, Double.class, 0.);
       
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, PROMPT, RECIPE_LOCATION, KEY, MINIMUM_VALUE, MAXIMUM_VALUE
    };
}
