package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface OperationStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.operationStep";
	
    public static final Property<?>[] properties = AbstractIlsStepDelegate.FOUNDATION_STEP_PROPERTIES_WITH_MSG_QUEUE;
}
