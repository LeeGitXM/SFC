package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 * This is for the step known as "Notify Dialog" 
 */
public interface DialogMessageStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.dialogMessageStep";
	
    public static final BasicProperty<?>[] properties = { 
    	POSITION, 
    	SCALE,
    	BUTTON_LABEL,
    	WINDOW_TITLE,
    	MESSAGE, 
    	
    	RECIPE_STATIC_STRATEGY, 
    	RECIPE_LOCATION, 
    	KEY, 
    	WINDOW,
    	ACK_REQUIRED, 
    };
}
