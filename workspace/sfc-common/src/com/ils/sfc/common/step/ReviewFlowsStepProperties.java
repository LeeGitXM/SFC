package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ReviewFlowsStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.reviewFlowsStep";
	
    public static final IlsProperty<?>[] properties = {  
    	SCREEN_HEADER,
    	BUTTON_LABEL,
    	DIALOG_TEMPLATE,
    	POSTING_METHOD,
    	AUTO_MODE,
    	DISPLAY_MODE,
    	BUTTON_KEY,
    	BUTTON_KEY_LOCATION,
    	POSITION,
    	SCALE,
    	REVIEW_FLOWS
    };
}
