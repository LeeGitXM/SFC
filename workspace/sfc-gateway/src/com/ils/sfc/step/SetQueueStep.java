package com.ils.sfc.step;

import com.ils.sfc.common.SetQueueStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SetQueueStep extends IlsAbstractChartStep implements SetQueueStepProperties {
	
	protected SetQueueStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.SET_QUEUE);	
	}

	@Override
	public void deactivateStep() {
	}
}
