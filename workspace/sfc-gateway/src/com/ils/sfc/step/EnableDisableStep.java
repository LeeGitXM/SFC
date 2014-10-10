package com.ils.sfc.step;

import com.ils.sfc.common.EnableDisableStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class EnableDisableStep extends IlsAbstractChartStep implements EnableDisableStepProperties {
	public EnableDisableStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.QUEUE_INSERT);
	}

	@Override
	public void deactivateStep() {
	}

}
