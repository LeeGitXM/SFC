package com.ils.sfc.common.step;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class DialogMessageStepDelegate extends AbstractIlsStepDelegate implements
DialogMessageStepProperties {

	protected DialogMessageStepDelegate() {
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
		// validate recipe data references:
		String strategy = element.get(IlsProperty.RECIPE_STATIC_STRATEGY);
		if(strategy.equals(Constants.RECIPE)) {
			String recipeLocation = element.get(IlsProperty.RECIPE_LOCATION);
			String key = element.get(IlsProperty.KEY);
			validator.validateRecipeKey(recipeLocation, key, chart, element);
		}
	}
}
