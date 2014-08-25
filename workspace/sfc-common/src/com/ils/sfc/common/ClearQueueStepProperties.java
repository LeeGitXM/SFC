package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ClearQueueStepProperties extends ChartStepProperties {

	public static final String FACTORY_ID = "com.ils.clearQueueStep";

    public static final Property<String> MESSAGE_PROPERTY = new BasicProperty<String>("message", String.class, "");

}
