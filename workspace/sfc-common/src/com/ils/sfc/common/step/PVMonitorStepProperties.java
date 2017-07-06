package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PVMonitorStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.pvMonitorStep";
	
    public static final BasicProperty<?>[] properties = {
        RECIPE_LOCATION,
    	TIMER_LOCATION,
    	TIMER_KEY,
    	TIMER_SET,
        TIME_LIMIT_STRATEGY,
        TIME_LIMIT_RECIPE_KEY,
        TIME_LIMIT_STATIC_VALUE,
        TIME_LIMIT_RECIPE_LOCATION,
    	ERROR_COUNT_SCOPE,
    	ERROR_COUNT_KEY,
    	ERROR_COUNT_MODE,
    	ACTIVATION_CALLBACK,
    	PV_MONITOR_CONFIG
    };
}
