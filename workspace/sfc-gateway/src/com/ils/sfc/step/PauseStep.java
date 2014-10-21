package com.ils.sfc.step;

import com.ils.sfc.common.step.PauseStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PauseStep extends IlsAbstractChartStep implements PauseStepProperties {

	public PauseStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.PAUSE);
	}

	@Override
	public void deactivateStep() {
	}

}
