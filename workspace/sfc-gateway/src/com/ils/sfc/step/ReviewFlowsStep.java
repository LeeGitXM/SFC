package com.ils.sfc.step;

import com.ils.sfc.common.step.ReviewFlowsStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ReviewFlowsStep extends IlsAbstractChartStep implements ReviewFlowsStepProperties {
	
	public ReviewFlowsStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.REVIEW_DATA);	
	}

}
