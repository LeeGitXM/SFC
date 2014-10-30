package com.ils.sfc.common.step;

import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface CloseWindowStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.CloseWindowStep";
	
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION,
    	WINDOW
    };
}
