package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface MonitorDownloadStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.monitorDownloadStep";
	
    public static final IlsProperty<?>[] properties = {
    	POSITION,
    	SCALE,
    	WINDOW_TITLE,
    	MONITOR_DOWNLOAD_WINDOW,
    	MONITOR_DOWNLOAD_POSTING_METHOD,
    	TIMER_LOCATION,
    	TIMER_KEY,
    	TIMER_CLEAR,
    	TIMER_SET,
    	MONITOR_DOWNLOAD_CONFIG
    };
}
