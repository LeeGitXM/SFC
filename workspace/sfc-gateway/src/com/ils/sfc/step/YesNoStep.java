package com.ils.sfc.step;

import com.ils.sfc.common.step.YesNoStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class YesNoStep extends IlsAbstractChartStep implements YesNoStepProperties {
	
	public YesNoStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.YES_NO);	
	}

}
