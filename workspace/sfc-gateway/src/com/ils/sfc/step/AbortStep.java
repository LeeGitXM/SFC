package com.ils.sfc.step;

import com.ils.sfc.common.AbortStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class AbortStep extends IlsAbstractChartStep implements AbortStepProperties {

	public AbortStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.ABORT);
	}

	@Override
	public void deactivateStep() {
	}

}
