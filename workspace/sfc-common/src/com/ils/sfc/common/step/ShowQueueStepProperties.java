package com.ils.sfc.common.step;

import static com.ils.sfc.common.IlsProperty.BUTTON_LABEL;
import static com.ils.sfc.common.IlsProperty.KEY;
import static com.ils.sfc.common.IlsProperty.POSITION;
import static com.ils.sfc.common.IlsProperty.PROMPT;
import static com.ils.sfc.common.IlsProperty.RECIPE_LOCATION;
import static com.ils.sfc.common.IlsProperty.SCALE;
import static com.ils.sfc.common.IlsProperty.WINDOW_TITLE;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ShowQueueStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.showQueueStep";

	public static final BasicProperty<?>[] properties = { 
        	POSITION,
        	SCALE
        };
	
}
