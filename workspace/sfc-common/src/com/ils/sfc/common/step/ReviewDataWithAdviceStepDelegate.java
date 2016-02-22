package com.ils.sfc.common.step;

import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewDataWithAdviceStepDelegate extends AbstractIlsStepDelegate implements
ReviewDataWithAdviceStepProperties {

	protected ReviewDataWithAdviceStepDelegate() {
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

	@Override
	public void validate(ChartInfo chart, ChartUIElement element, StepPropertyValidator validator) {
		ReviewDataStepDelegate del = new ReviewDataStepDelegate();
		// this class is the same as ReviewDataStepDelegate except for advice...
		del.validate(chart,  element, validator);
	}
}
