package com.ils.sfc.common;

import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class RawQueryStepDelegate extends AbstractIlsStepDelegate implements
RawQueryStepProperties {

	protected RawQueryStepDelegate() {
		super(properties);
	}

	@Override
	public String getId() {
		return FACTORY_ID;
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
		// TODO: check stuff in element
		//results.addError(new CompilationError("bad stuff", element.getLocation()));
	}

}
