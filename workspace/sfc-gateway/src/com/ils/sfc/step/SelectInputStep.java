package com.ils.sfc.step;

import com.ils.sfc.common.SelectInputStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SelectInputStep extends IlsAbstractChartStep implements SelectInputStepProperties {
	
	protected SelectInputStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.SELECT_INPUT);	
	}

	@Override
	public void deactivateStep() {
	}
}
