package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ManualDataEntryStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.manualDataEntryStep";
	
    public static final IlsProperty<?>[] properties = {
    	POSITION,
    	SCALE,
    	WINDOW_TITLE,
    	MANUAL_DATA_WINDOW,
    	MANUAL_DATA_POSTING_METHOD,
    	BUTTON_LABEL,
    	TIMEOUT,
    	AUTO_MODE,
    	MANUAL_DATA_CONFIG
    };
}
