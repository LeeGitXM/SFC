package com.ils.sfc.step;

import com.ils.sfc.common.ClearQueueStepProperties;
import com.ils.sfc.step.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ClearQueueStep extends IlsAbstractChartStep implements ClearQueueStepProperties {

	public ClearQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.CLEAR_QUEUE);
	}

	@Override
	public void deactivateStep() {
	}

}
