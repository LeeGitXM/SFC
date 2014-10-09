package com.ils.sfc.common;

import com.inductiveautomation.ignition.common.config.BasicDescriptiveProperty;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ControlPanelMessageStepProperties extends ChartStepProperties {
	public static final String FACTORY_ID = "com.ils.controlPanelMessageStep";
	
    public static final Property<String> DESCRIPTION = new BasicProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");

    public static final Property<String> MESSAGE = new BasicProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
    public static final Property<Boolean> ACKNOWLEDGEMENT_REQUIRED = new BasicProperty<Boolean>(IlsSfcNames.ACKNOWLEDGEMENT_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final Property<String> PRIORITY = new BasicProperty<String>(IlsSfcNames.PRIORITY, String.class, "");
    public static final Property<Boolean> POST_TO_QUEUE = new BasicProperty<Boolean>(IlsSfcNames.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final Property<Integer> TIMEOUT = new BasicProperty<Integer>(IlsSfcNames.TIMEOUT, Integer.class, 0);
    public static final Property<String> TIMEOUT_UNIT = new BasicProperty<String>(IlsSfcNames.TIMEOUT_UNIT, String.class, "minutes");
     
    public static final Property<?>[] properties = { 
    	DESCRIPTION, MESSAGE, ACKNOWLEDGEMENT_REQUIRED, PRIORITY, POST_TO_QUEUE, TIMEOUT, TIMEOUT_UNIT
    };
}
