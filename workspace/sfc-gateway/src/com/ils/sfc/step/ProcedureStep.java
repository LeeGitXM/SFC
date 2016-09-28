package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.definitions.StepDefinition;

import system.ils.sfc.common.Constants;

public class ProcedureStep extends FoundationStep {
	//private static LoggerEx logger = LogUtil.getLogger(ProcedureStep.class.getName());

	public ProcedureStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
	}
	@Override
	public void activateStep(StepController controller) {
		exec(PythonCall.PROCEDURE);		
		scopeContext.getStepScope().put(Constants.S88_LEVEL, Constants.GLOBAL);
		super.activateStep(controller);
	}
}
