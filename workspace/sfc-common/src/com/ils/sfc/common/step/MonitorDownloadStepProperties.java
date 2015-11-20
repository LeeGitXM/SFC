package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface MonitorDownloadStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.monitorDownloadStep";
	
    public static final BasicProperty<?>[] properties = {
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE, 
    	MONITOR_DOWNLOADS_WINDOW,
    	MONITOR_DOWNLOADS_POSTING_METHOD,
    	TIMER_LOCATION,
    	TIMER_KEY,
    	TIMER_CLEAR,
    	TIMER_SET,
    	RECIPE_LOCATION,
    	MONITOR_DOWNLOADS_CONFIG
    };
}
