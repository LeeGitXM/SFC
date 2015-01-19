package com.ils.sfc.step;

import com.ils.sfc.common.step.RawQueryStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class RawQueryStep extends IlsAbstractChartStep implements RawQueryStepProperties {
	
	public RawQueryStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.RAW_QUERY);	
	}

}
