package com.ils.sfc.common.step;

import com.inductiveautomation.sfc.elements.steps.enclosing.AbstractEnclosingStepDelegate;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;

public class IlsEnclosingStepDelegate extends AbstractEnclosingStepDelegate implements
EnclosingStepProperties {

	@Override
	public String getId() {
		return "com.ils.enclosingStep";
	}

}
