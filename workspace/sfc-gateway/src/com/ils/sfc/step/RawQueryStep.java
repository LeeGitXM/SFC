package com.ils.sfc.step;

import com.ils.sfc.common.RawQueryStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class RawQueryStep extends IlsAbstractChartStep implements RawQueryStepProperties {
	
	protected RawQueryStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.RAW_QUERY);	
	}

	@Override
	public void deactivateStep() {
	}
}
