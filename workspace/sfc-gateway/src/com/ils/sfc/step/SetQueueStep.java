package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.SetQueueStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SetQueueStep extends IlsAbstractChartStep implements SetQueueStepProperties {
	
	public SetQueueStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	protected PythonCall getPythonCall(){return PythonCall.SET_QUEUE;}


}
