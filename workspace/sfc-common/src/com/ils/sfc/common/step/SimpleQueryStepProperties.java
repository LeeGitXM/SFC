package com.ils.sfc.common.step;

import com.ils.sfc.util.IlsProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

import static com.ils.sfc.util.IlsProperty.*;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface SimpleQueryStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.simpleQueryStep";
	
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, DATABASE, SQL, RESULTS_MODE, FETCH_MODE, RECIPE_LOCATION, KEY_MODE, KEY
    };
}