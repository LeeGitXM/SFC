package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.LimitedInputStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class LimitedInputStep extends IlsAbstractChartStep implements LimitedInputStepProperties {
	
	public LimitedInputStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.GET_LIMITED_INPUT);	
	}

}
