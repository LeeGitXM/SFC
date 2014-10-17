package com.ils.sfc.common;

import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface LimitedInputStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.limitedInputStep";

    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, PROMPT, RECIPE_LOCATION, KEY, MINIMUM_VALUE, MAXIMUM_VALUE
    };
}
