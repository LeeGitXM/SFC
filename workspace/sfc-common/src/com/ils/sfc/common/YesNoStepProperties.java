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
	
    public static final Property<String> PROMPT = new BasicProperty<String>("prompt", String.class, "");
    public static final Property<String> RECIPE_LOCATION = new BasicProperty<String>("recipeLocation", String.class, "");
    public static final Property<String> KEY = new BasicProperty<String>("key", String.class, "");
     
    public static final Property<?>[] properties = { 
    	PROMPT, RECIPE_LOCATION, KEY
    };
}
