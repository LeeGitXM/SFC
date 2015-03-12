package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class TimedDelayStep extends IlsAbstractChartStep implements TimedDelayStepProperties {
	
	public TimedDelayStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context,scopeContext,  definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.TIMED_DELAY);	
	}

	@Override
	public void pauseStep() {
	}

	@Override
	public void resumeStep() {
	}
}
