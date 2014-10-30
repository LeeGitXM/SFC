package com.ils.sfc.step;

import com.ils.sfc.common.step.PrintWindowStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PrintWindowStep extends IlsAbstractChartStep implements PrintWindowStepProperties {
	
	protected PrintWindowStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.PRINT_WINDOW);	
	}

	@Override
	public void deactivateStep() {
	}
}
