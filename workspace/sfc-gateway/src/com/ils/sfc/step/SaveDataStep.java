package com.ils.sfc.step;

import com.ils.sfc.common.step.SaveDataStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class SaveDataStep extends IlsAbstractChartStep implements SaveDataStepProperties {
	
	protected SaveDataStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.SAVE_DATA);	
	}

}
