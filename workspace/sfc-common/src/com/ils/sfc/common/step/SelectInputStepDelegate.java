package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SelectInputStepDelegate extends AbstractIlsStepDelegate implements
SelectInputStepProperties {

	protected SelectInputStepDelegate() {
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
		String recipeLocation = element.get(IlsProperty.RECIPE_LOCATION);
		String key = element.get(IlsProperty.KEY);
		validator.validateRecipeKey(recipeLocation, key, chart, element);
		
		String chRecipeLocation = element.get(IlsProperty.CHOICES_RECIPE_LOCATION);
		String chKey = element.get(IlsProperty.CHOICES_KEY);
		validator.validateRecipeKey(chRecipeLocation, chKey, chart, element);
	}
}
