package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ManualDataEntryStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.manualDataEntryStep";
	
    public static final BasicProperty<?>[] properties = {
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	WINDOW_HEADER,
    	MANUAL_DATA_WINDOW,
    	AUTO_MODE,
    	REQUIRE_ALL_INPUTS,
    	MANUAL_DATA_CONFIG
    };
}
