package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.EnableDisableStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class EnableDisableStep extends IlsAbstractChartStep implements EnableDisableStepProperties {
	
	public EnableDisableStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.ENABLE_DISABLE);
	}

}
