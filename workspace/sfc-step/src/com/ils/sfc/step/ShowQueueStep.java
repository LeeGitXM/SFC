package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.ShowQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ShowQueueStep extends IlsAbstractChartStep implements ShowQueueStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(ShowQueueStep.class);

	public ShowQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("ShowQueueStep onStart()");
		// TODO: clear the queue
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
