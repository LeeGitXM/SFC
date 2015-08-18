package com.ils.sfc.step;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PhaseStep extends FoundationStep {
	private static LoggerEx logger = LogUtil.getLogger(PhaseStep.class.getName());

	public PhaseStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		scopeContext.getStepScope().put(Constants.S88_LEVEL, Constants.PHASE);
	}

}
