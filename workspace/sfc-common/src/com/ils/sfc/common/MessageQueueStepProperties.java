package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface MessageQueueStepProperties extends ChartStepProperties {

	public static final String FACTORY_ID = "com.ils.messageQueueStep";

    public static final Property<Integer> EXAMPLE_PROPERTY = new BasicProperty<Integer>("example-property", Integer.class, 11);

}
