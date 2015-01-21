package com.ils.sfc.common.step;

import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ReviewDataWithAdviceStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.reviewDataWithAdviceStep";
	
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
    	REVIEW_DATA_WITH_ADVICE
    };
}
