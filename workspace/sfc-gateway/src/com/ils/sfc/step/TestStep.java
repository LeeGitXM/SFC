package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.TestStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.ils.sfc.util.IlsSfcIOIF;

public class TestStep extends IlsAbstractChartStep implements TestStepProperties {
	private static final Logger logger = LoggerFactory.getLogger(TestStep.class);

	public TestStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	@Override
	public void activateStep() {
		logger.debug("TestStep activateStep()");
	}

	@Override
	public void deactivateStep() {
		logger.debug("TestStep deactivateStep()");
	}

}
