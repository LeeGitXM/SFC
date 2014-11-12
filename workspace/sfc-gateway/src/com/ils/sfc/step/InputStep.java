package com.ils.sfc.step;

import com.ils.sfc.common.step.InputStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class InputStep extends IlsAbstractChartStep implements InputStepProperties {
	
	protected InputStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.GET_INPUT);	
	}

}
