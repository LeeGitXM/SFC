package com.ils.sfc.common.step;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.common.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface SaveQueueStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.saveQueueStep";
	
    public static final BasicProperty<?>[] properties = {
     	DIRECTORY,
    	FILENAME,  // includes extension
    	TIMESTAMP,
    };
}
