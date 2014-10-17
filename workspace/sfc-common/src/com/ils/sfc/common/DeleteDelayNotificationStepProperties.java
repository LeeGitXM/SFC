package com.ils.sfc.common;


import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface DeleteDelayNotificationStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.deleteDelayNotification";

    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION };
}
