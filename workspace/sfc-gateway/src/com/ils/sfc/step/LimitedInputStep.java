package com.ils.sfc.step;

import com.ils.sfc.common.LimitedInputStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class LimitedInputStep extends IlsAbstractChartStep implements LimitedInputStepProperties {
	
	protected LimitedInputStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.GET_LIMITED_INPUT);	
	}

	@Override
	public void deactivateStep() {
	}
}
