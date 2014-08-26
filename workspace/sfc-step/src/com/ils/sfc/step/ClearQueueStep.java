package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.ClearQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ClearQueueStep extends IlsAbstractChartStep implements ClearQueueStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(ClearQueueStep.class);

	public ClearQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	protected void onStart() {
		logger.debug("ClearQueueStep onStart()");
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
