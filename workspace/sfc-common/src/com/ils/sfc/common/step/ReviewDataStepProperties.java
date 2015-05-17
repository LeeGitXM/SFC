package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ReviewDataStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.reviewDataStep";
	
    public static final IlsProperty<?>[] properties = {  
    	WINDOW_TITLE,
    	BUTTON_LABEL,
    	REVIEW_DATA_POSTING_METHOD,
    	BUTTON_KEY,
    	BUTTON_KEY_LOCATION,
    	POSITION,
    	SCALE,
    	REVIEW_DATA_WINDOW,
    	PRIMARY_REVIEW_DATA,
    	SECONDARY_REVIEW_DATA
    };
}
