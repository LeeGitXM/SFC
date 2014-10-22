package com.ils.sfc.step;

import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

public class IlsEnclosingStep extends IlsAbstractChartStep implements EnclosingStepProperties {

	public IlsEnclosingStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

}
