package com.ils.sfc.common.step;


import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class TimedDelayStepDelegate extends AbstractIlsStepDelegate implements
TimedDelayStepProperties {

	protected TimedDelayStepDelegate() {
		super(properties);
	}

	@Override
	public String getId() {
		return FACTORY_ID;
	}
	
	@Override
	public void validate(ChartUIElement element, ChartCompilationResults results) {
	}
	
	@Override
	public void validate(ChartInfo chart, ChartUIElement element, StepPropertyValidator validator) {
		
		// validate recipe data keys:
		String timeDelayStrategy = element.get(IlsProperty.TIME_DELAY_STRATEGY);
		if(Constants.RECIPE.equals(timeDelayStrategy)) {
			String scope = element.get(IlsProperty.RECIPE_LOCATION);
			String key = element.get(IlsProperty.KEY);
			validator.validateRecipeKey(scope, key, chart, element);
		}
	}

}
