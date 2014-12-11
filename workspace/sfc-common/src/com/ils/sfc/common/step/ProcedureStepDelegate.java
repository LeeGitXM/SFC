package com.ils.sfc.common.step;

import com.inductiveautomation.sfc.elements.steps.enclosing.AbstractEnclosingStepDelegate;

public class ProcedureStepDelegate extends AbstractEnclosingStepDelegate implements
ProcedureStepProperties {

	@Override
	public String getId() {
		return "com.ils.procedureStep";
	}

}