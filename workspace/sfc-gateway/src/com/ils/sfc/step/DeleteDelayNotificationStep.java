package com.ils.sfc.step;

import com.ils.sfc.common.step.DeleteDelayNotificationStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class DeleteDelayNotificationStep extends IlsAbstractChartStep implements DeleteDelayNotificationStepProperties {
	
	protected DeleteDelayNotificationStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.DELETE_DELAY_NOTIFICATION);	
	}

}
