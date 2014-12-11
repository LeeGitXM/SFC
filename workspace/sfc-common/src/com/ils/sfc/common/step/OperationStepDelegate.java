package com.ils.sfc.common.step;

import com.inductiveautomation.sfc.elements.steps.enclosing.AbstractEnclosingStepDelegate;

public class OperationStepDelegate extends AbstractEnclosingStepDelegate implements
OperationStepProperties {

	@Override
	public String getId() {
		return "com.ils.operationStep";
	}

}