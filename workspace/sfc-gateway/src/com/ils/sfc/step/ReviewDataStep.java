package com.ils.sfc.step;

import com.ils.sfc.common.step.ReviewDataStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ReviewDataStep extends IlsAbstractChartStep implements ReviewDataStepProperties {
	
	protected ReviewDataStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.SET_QUEUE);	
	}

}
