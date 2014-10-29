package com.ils.sfc.step;

import com.ils.sfc.common.step.PrintFileStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PrintFileStep extends IlsAbstractChartStep implements PrintFileStepProperties {
	
	protected PrintFileStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		exec(PythonCall.PRINT_FILE);	
	}

	@Override
	public void deactivateStep() {
	}
}
