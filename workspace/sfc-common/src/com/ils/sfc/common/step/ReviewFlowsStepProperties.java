package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ReviewFlowsStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.reviewFlowsStep";
	
    public static final BasicProperty<?>[] properties = {  
    	POSITION,
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	REVIEW_FLOWS_WINDOW,
    	REVIEW_FLOWS_POSTING_METHOD,
    	AUTO_MODE,
    	BUTTON_KEY,
    	BUTTON_KEY_LOCATION,
    	HEADING1,
    	HEADING2,
    	HEADING3,
    	REVIEW_FLOWS
    };
}
