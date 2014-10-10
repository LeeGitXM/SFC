package com.ils.sfc.step;

import com.ils.sfc.common.PostDelayNotificationStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PostDelayNotificationStep extends IlsAbstractChartStep implements PostDelayNotificationStepProperties {
	
	protected PostDelayNotificationStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.POST_DELAY_NOTIFICATION);	
	}

	@Override
	public void deactivateStep() {
	}
}
