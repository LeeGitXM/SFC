package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ReviewDataStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.reviewDataStep";
	
    public static final BasicProperty<?>[] properties = {  
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	BUTTON_KEY,
    	BUTTON_KEY_LOCATION,
    	PRIMARY_REVIEW_DATA,
    	PRIMARY_TAB_LABEL,
    	SECONDARY_REVIEW_DATA,
    	SECONDARY_TAB_LABEL,
    	ACTIVATION_CALLBACK,
    	CUSTOM_WINDOW_PATH
    };
}
