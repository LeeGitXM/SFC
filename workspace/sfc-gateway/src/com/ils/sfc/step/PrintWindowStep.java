package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.PrintWindowStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PrintWindowStep extends IlsAbstractChartStep implements PrintWindowStepProperties {
	
	public PrintWindowStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.PRINT_WINDOW);	
	}

}
