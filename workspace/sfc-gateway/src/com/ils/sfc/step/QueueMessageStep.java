package com.ils.sfc.step;

import com.ils.sfc.common.step.QueueMessageStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class QueueMessageStep extends IlsAbstractChartStep implements QueueMessageStepProperties {
	
	public QueueMessageStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.QUEUE_INSERT);
	}

}
