package com.ils.sfc.step;

import com.ils.sfc.common.ShowQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ShowQueueStep extends IlsAbstractChartStep implements ShowQueueStepProperties {

	public ShowQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.SHOW_QUEUE);
	}

	@Override
	public void deactivateStep() {
	}

}