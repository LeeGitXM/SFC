package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PVMonitorStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.pvMonitorStep";
	
    public static final IlsProperty<?>[] properties = {
    	TIMER_LOCATION,
    	TIMER_KEY,
    	TIMER_SET,
    	TIME_LIMIT_STRATEGY,
        RECIPE_LOCATION,
        KEY,
        NON_NULL_VALUE,
        DATA_LOCATION,
    	PV_MONITOR_CONFIG
    };
}
