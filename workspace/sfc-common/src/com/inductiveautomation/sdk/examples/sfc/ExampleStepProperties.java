package com.inductiveautomation.sdk.examples.sfc;

import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;

/**
 * This interface just exists to store some constants for the step's properties and factory id
 *
 */
public interface ExampleStepProperties extends ChartStepProperties {

	public static final String FACTORY_ID = "com.acme.examplestep";

    public static final Property<Integer> EXAMPLE_PROPERTY = new BasicProperty<Integer>("example-property", Integer.class, 11);

}
