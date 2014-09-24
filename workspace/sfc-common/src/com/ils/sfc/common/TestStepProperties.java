package com.ils.sfc.common;

import com.ils.sfc.util.IlsSfcIOIF;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface TestStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.testStep";

    public static final Property<String> MESSAGE_PROPERTY = new BasicProperty<String>("message", String.class, "");
    public static final Property<String> STATUS_PROPERTY = new BasicProperty<String>("status", String.class, IlsSfcIOIF.MessageStatus.Info.toString());

}
