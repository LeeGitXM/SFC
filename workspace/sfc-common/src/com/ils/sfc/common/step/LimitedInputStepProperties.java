package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface LimitedInputStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.limitedInputStep";

    public static final BasicProperty<?>[] properties = { 
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	PROMPT, 
    	RECIPE_CHART_STEP_LOCATION, 
    	KEY,
    	MAXIMUM_VALUE,
    	DEFAULT_VALUE,
    	MINIMUM_VALUE 
    };
}
