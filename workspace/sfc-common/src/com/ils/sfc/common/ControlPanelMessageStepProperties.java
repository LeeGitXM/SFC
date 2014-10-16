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
	
    public static final IlsProperty<String> DESCRIPTION = new IlsProperty<String>(IlsSfcNames.DESCRIPTION, String.class, "");

    public static final IlsProperty<String> MESSAGE = new IlsProperty<String>(IlsSfcNames.MESSAGE, String.class, "");
    public static final IlsProperty<Boolean> ACKNOWLEDGEMENT_REQUIRED = new IlsProperty<Boolean>(IlsSfcNames.ACKNOWLEDGEMENT_REQUIRED, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<String> PRIORITY = new IlsProperty<String>(IlsSfcNames.PRIORITY, String.class, "");
    public static final IlsProperty<Boolean> POST_TO_QUEUE = new IlsProperty<Boolean>(IlsSfcNames.POST_TO_QUEUE, Boolean.class, Boolean.FALSE);
    public static final IlsProperty<Integer> TIMEOUT = new IlsProperty<Integer>(IlsSfcNames.TIMEOUT, Integer.class, 0);
    public static final IlsProperty<String> TIMEOUT_UNIT = new IlsProperty<String>(IlsSfcNames.TIMEOUT_UNIT, String.class, IlsSfcNames.MINUTE);
     
    public static final IlsProperty<?>[] properties = { 
    	DESCRIPTION, MESSAGE, ACKNOWLEDGEMENT_REQUIRED, PRIORITY, POST_TO_QUEUE, TIMEOUT, TIMEOUT_UNIT
    };
}
