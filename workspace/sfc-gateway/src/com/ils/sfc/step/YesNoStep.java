package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.YesNoStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class YesNoStep extends IlsAbstractChartStep implements YesNoStepProperties {
	
	public YesNoStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	protected PythonCall getPythonCall(){return PythonCall.YES_NO;}

}
