package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface TestStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.testStep";
	
    public static final IlsProperty<String> MESSAGE_PROPERTY = new IlsProperty<String>("message", String.class, "");
    public static final IlsProperty<String> STATUS_PROPERTY = new IlsProperty<String>("status", String.class, "Info");
    
    public static final IlsProperty<?>[] properties = { 
    	MESSAGE_PROPERTY,
    	STATUS_PROPERTY };
}
