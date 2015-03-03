package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface PhaseStepProperties extends EnclosingStepProperties {
	public static final String FACTORY_ID = "com.ils.phaseStep";
	
    public static final IlsProperty<?>[] properties = {
    	IlsProperty.QUEUE,
    };
}
