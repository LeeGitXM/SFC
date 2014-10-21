package com.ils.sfc.common.step;

import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ControlPanelMessageStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.controlPanelMessageStep";
	     
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, MESSAGE, ACKNOWLEDGEMENT_REQUIRED, PRIORITY, POST_TO_QUEUE, TIMEOUT, TIMEOUT_UNIT
    };
}
