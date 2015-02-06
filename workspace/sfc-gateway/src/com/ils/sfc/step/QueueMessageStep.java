package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.QueueMessageStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class QueueMessageStep extends IlsAbstractChartStep implements QueueMessageStepProperties {
	
	public QueueMessageStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.QUEUE_INSERT);
	}

}
