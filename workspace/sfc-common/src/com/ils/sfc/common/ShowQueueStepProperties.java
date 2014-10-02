package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ShowQueueStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.showQueueStep";
	
    public static final Property<?>[] properties = { };
}
