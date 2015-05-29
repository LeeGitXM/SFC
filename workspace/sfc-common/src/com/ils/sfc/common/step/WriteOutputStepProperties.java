package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface WriteOutputStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.writeOutputStep";
	
    public static final IlsProperty<?>[] properties = {
    	VERBOSE,
    	RECIPE_LOCATION,
    	TIMER_LOCATION,
    	TIMER_KEY,
    	TIMER_SET,
    	WRITE_OUTPUT_CONFIG
    };
}
