package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface YesNoStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.yesNoStep";
	
    public static final Property<String> PROMPT = new BasicProperty<String>(IlsSfcNames.PROMPT, String.class, "");
    public static final Property<String> RECIPE_LOCATION = new BasicProperty<String>(IlsSfcNames.RECIPE_LOCATION, String.class, "");
    public static final Property<String> KEY = new BasicProperty<String>(IlsSfcNames.KEY, String.class, "");
    public static final Property<String> DESCRIPTION = new BasicProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
     
    public static final Property<?>[] properties = { 
    	DESCRIPTION, PROMPT, RECIPE_LOCATION, KEY
    };
}
