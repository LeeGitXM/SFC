package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface OcAlertStepProperties extends ChartStepProperties{
	public static final String FACTORY_ID = "com.ils.ocAlertStep";
	
    public static final BasicProperty<?>[] properties = {
    	POST,
    	TOP_MESSAGE,
    	BOTTOM_MESSAGE,
    	MAIN_MESSAGE,
    	BUTTON_LABEL,
    	BUTTON_CALLBACK,
    	OC_ALERT_WINDOW_TYPE,
    	OC_ALERT_WINDOW,
    	ACK_REQUIRED
    };
}
