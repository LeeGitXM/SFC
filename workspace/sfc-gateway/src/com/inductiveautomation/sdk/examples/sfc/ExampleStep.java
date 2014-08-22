package com.inductiveautomation.sdk.examples.sfc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inductiveautomation.sfc.api.AbstractBlockingStep;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ExampleStep extends AbstractBlockingStep implements ChartStep, ExampleStepProperties {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ChartContext context;
	private final Integer propertyValue;

	public ExampleStep(ChartContext context, StepDefinition definition) {
		super(context, definition);

		this.context = context;
		this.propertyValue = definition.getProperties().getOrDefault(EXAMPLE_PROPERTY);
	}

	@Override
	protected void onStart() {
		logger.debug("Example step started");

		context.getChartScope().setVariable("Hello",
				"World, propertyValue = " + propertyValue);
	}

	@Override
	protected void onPause() {

	}

	@Override
	protected void onResume() {

	}

	@Override
	protected void onStop() {

	}

}
