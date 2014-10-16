package com.ils.sfc.common;


import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PostDelayNotificationStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.postDelayNotification";

    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
  
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, MESSAGE };
}
