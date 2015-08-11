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
    	WINDOW_TITLE,
    	REVIEW_DATA_POSTING_METHOD,
    	BUTTON_KEY,
    	BUTTON_KEY_LOCATION,
    	POSITION,
    	SCALE,
    	REVIEW_DATA_WINDOW,
    	PRIMARY_REVIEW_DATA,
    	PRIMARY_TAB_LABEL,
    	SECONDARY_REVIEW_DATA,
    	SECONDARY_TAB_LABEL
    };
}
