package com.ils.sfc.common.step;

import com.inductiveautomation.sfc.elements.steps.enclosing.AbstractEnclosingStepDelegate;

public class PhaseStepDelegate extends AbstractEnclosingStepDelegate implements
PhaseStepProperties {

	@Override
	public String getId() {
		return "com.ils.phaseStep";
	}

}