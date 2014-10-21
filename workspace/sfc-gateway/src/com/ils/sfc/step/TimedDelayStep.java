package com.ils.sfc.step;

import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class TimedDelayStep extends IlsAbstractChartStep implements TimedDelayStepProperties {
	
	protected TimedDelayStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.TIMED_DELAY);	
	}

	@Override
	public void deactivateStep() {
	}
}