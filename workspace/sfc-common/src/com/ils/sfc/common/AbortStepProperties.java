package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface AbortStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.abortStep";
	
    public static final Property<String> DESCRIPTION = new BasicProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");
    public static final Property<String> MESSAGE = new BasicProperty<String>(IlsSfcNames.MESSAGE, String.class, "");

    public static final Property<?>[] properties = { DESCRIPTION, MESSAGE };
}