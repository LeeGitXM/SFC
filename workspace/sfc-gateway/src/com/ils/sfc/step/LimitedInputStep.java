package com.ils.sfc.step;

import com.ils.sfc.common.step.LimitedInputStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class LimitedInputStep extends IlsAbstractChartStep implements LimitedInputStepProperties {
	
	public LimitedInputStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.GET_LIMITED_INPUT);	
	}

}
