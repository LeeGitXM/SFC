package com.ils.sfc.step;

import com.ils.sfc.common.step.CollectDataStepProperties;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class CollectDataStep extends IlsAbstractChartStep implements CollectDataStepProperties {
	
	protected CollectDataStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.COLLECT_DATA);	
	}

}
