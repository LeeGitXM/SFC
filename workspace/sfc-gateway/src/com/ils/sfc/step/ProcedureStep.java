package com.ils.sfc.step;

import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

public class ProcedureStep extends IlsAbstractChartStep implements EnclosingStepProperties {
	
	protected ProcedureStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}
}
