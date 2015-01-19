package com.ils.sfc.step;

import com.ils.sfc.common.step.PostDelayNotificationStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PostDelayNotificationStep extends IlsAbstractChartStep implements PostDelayNotificationStepProperties {
	
	public PostDelayNotificationStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.POST_DELAY_NOTIFICATION);	
	}

}
