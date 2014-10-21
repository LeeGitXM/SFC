package com.ils.sfc.step;

import com.ils.sfc.common.step.DebugPropertiesStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class DebugPropertiesStep extends IlsAbstractChartStep implements DebugPropertiesStepProperties {
	
	protected DebugPropertiesStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.DEBUG_PROPERTIES);	
	}

	@Override
	public void deactivateStep() {
	}
}
