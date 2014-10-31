package com.ils.sfc.step;

import com.ils.sfc.common.step.ShowWindowStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ShowWindowStep extends IlsAbstractChartStep implements ShowWindowStepProperties {

	public ShowWindowStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.SHOW_WINDOW);
	}

	@Override
	public void deactivateStep() {
	}

}