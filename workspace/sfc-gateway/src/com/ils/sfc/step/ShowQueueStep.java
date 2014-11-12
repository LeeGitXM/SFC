package com.ils.sfc.step;

import com.ils.sfc.common.step.ShowQueueStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ShowQueueStep extends IlsAbstractChartStep implements ShowQueueStepProperties {

	public ShowQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.SHOW_QUEUE);
	}

}
