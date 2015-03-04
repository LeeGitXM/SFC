package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PhaseStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.phaseStep";
	
    public static final Property<?>[] properties = IlsProperty.FOUNDATION_STEP_PROPERTIES;
}
