package com.ils.sfc.common.step;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SimpleQueryStepDelegate extends AbstractIlsStepDelegate implements
SimpleQueryStepProperties {

	protected SimpleQueryStepDelegate() {
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
		String keyMode = element.get(IlsProperty.KEY_MODE);
		if(keyMode.equals(Constants.STATIC)) {
			String recipeLocation = element.get(IlsProperty.RECIPE_LOCATION);
			String key = element.get(IlsProperty.KEY);
			validator.validateRecipeKey(recipeLocation, key, chart, element);
		}
	}

}
