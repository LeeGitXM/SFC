package com.ils.sfc.step;

import com.ils.sfc.common.step.DialogMessageStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class DialogMessageStep extends IlsAbstractChartStep implements DialogMessageStepProperties {
	
	protected DialogMessageStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.DIALOG_MESSAGE);	
	}

	@Override
	public void deactivateStep() {
	}
}
