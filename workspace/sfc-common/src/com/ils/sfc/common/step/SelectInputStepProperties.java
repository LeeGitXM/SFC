package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface SelectInputStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.selectInputStep";
	     
    public static final BasicProperty<?>[] properties = { 
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	PROMPT, 
    	CHOICES_RECIPE_LOCATION, 
    	CHOICES_KEY, 
    	RECIPE_CHART_STEP_LOCATION,
    	RESPONSE_KEY_AND_ATTRIBUTE
    };
}
