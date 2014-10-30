package com.ils.sfc.step;

import com.ils.sfc.common.step.CloseWindowStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class CloseWindowStep extends IlsAbstractChartStep implements CloseWindowStepProperties {
	
	protected CloseWindowStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.CLOSE_WINDOW);	
	}

	@Override
	public void deactivateStep() {
	}
}
