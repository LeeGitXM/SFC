package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.MessageQueueStepProperties;
import com.inductiveautomation.sfc.api.AbstractBlockingStep;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class MessageQueueStep extends AbstractBlockingStep implements ChartStep, MessageQueueStepProperties {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ChartContext context;
	private final Integer propertyValue;

	public MessageQueueStep(ChartContext context, StepDefinition definition) {
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
