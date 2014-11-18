package com.ils.sfc.common.step;


import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface EnableDisableStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.enableDisable";

    public static final IlsProperty<?>[] properties = { 
    	ENABLE_PAUSE, ENABLE_RESUME, ENABLE_CANCEL };
}
