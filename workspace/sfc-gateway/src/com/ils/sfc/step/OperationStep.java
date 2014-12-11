package com.ils.sfc.step;

import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

public class OperationStep extends IlsAbstractChartStep implements EnclosingStepProperties {
	
	protected OperationStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}
}
