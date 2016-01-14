package com.ils.sfc.step;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class OperationStep extends FoundationStep {
	private static LoggerEx logger = LogUtil.getLogger(OperationStep.class.getName());

	public OperationStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
	}

	@Override
	public void activateStep(StepController controller) {
		exec(PythonCall.OPERATION);		
		scopeContext.getStepScope().put(Constants.S88_LEVEL, Constants.OPERATION);
		super.activateStep(controller);
	}

}
