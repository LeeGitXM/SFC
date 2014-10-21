package com.ils.sfc.step;

import com.ils.sfc.common.step.SimpleQueryStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SimpleQueryStep extends IlsAbstractChartStep implements SimpleQueryStepProperties {
	
	protected SimpleQueryStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.SIMPLE_QUERY);	
	}

	@Override
	public void deactivateStep() {
	}
}
