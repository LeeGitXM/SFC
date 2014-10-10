package com.ils.sfc.step;

import com.ils.sfc.common.YesNoStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class YesNoStep extends IlsAbstractChartStep implements YesNoStepProperties {
	
	protected YesNoStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.YES_NO);	
	}

	@Override
	public void deactivateStep() {
	}
}
