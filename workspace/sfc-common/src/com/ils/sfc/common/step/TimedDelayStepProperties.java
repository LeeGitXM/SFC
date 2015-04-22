package com.ils.sfc.common.step;


import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface TimedDelayStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.timedDelayStep";

    public static final IlsProperty<?>[] properties = { 
    	TIME_DELAY_STRATEGY, CALLBACK, KEY, RECIPE_LOCATION, DELAY, DELAY_UNIT, POST_NOTIFICATION };
}
