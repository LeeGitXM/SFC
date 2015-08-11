package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PauseStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.pauseStep";
	
    public static final BasicProperty<?>[] properties = {};
}
