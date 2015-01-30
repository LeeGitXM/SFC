package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.ControlPanelMessageStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class ControlPanelMessageStep extends IlsAbstractChartStep implements ControlPanelMessageStepProperties {
	
	public ControlPanelMessageStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.CONTROL_PANEL_MESSAGE);	
	}

}
