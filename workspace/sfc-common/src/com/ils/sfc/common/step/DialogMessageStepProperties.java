package com.ils.sfc.common.step;

import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface DialogMessageStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.dialogMessageStep";
	
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, RECIPE_STATIC_STRATEGY, RECIPE_LOCATION, KEY, DIALOG, METHOD, MESSAGE, ACK_REQUIRED, TIMEOUT, TIMEOUT_UNIT, POSITION, SCALE
    };
}
