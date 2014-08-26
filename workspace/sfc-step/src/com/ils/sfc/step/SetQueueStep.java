package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.SetQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SetQueueStep extends IlsAbstractChartStep implements SetQueueStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(SetQueueStep.class);

	public SetQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("SetQueueStep onStart(); queue: " + getQueue());
		// TODO: set the queue
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

	public String getQueue() {
		return getDefinition().getProperties().getOrDefault(SetQueueStepProperties.QUEUE_PROPERTY);
	}
	
	public void setQueue(String queue) {
		getDefinition().getProperties().set(QUEUE_PROPERTY, queue);
	}
}
