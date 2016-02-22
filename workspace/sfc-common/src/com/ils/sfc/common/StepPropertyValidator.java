package com.ils.sfc.common;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.elements.steps.action.ActionStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** A helper class for validating the property values of Ils custom
 *  steps. Validation of most properties could be done locally within the
 *  step, but since Recipe Data references can go between charts, more
 *  global information is necessary to validate them--hence the need
 *  for something like this.
 */
public class StepPropertyValidator {
	private static LoggerEx logger = LogUtil.getLogger(StepPropertyValidator.class.getName());
	private Project globalProject;
	private ClientStepRegistry registry;
	private SimpleHierarchyAnalyzer hierarchyAnalyzer;
	private List<Object[]> errors = new ArrayList<Object[]>();
	
	public StepPropertyValidator(Project globalProject, ClientStepRegistry registry) {
		this.globalProject = globalProject;
		this.registry = registry;
	}

	/** Validate all properties for all charts */
	public void validateAllCharts() {
		hierarchyAnalyzer = new SimpleHierarchyAnalyzer(globalProject, registry);
		hierarchyAnalyzer.analyze();
		for(ChartInfo chart: hierarchyAnalyzer.getChartsByPath().values()) {
			for(ChartUIElement element: chart.model.getChartElements()) {
				validateStep(chart, element);
			}
		}
	}

	/** Validate a single step. In line with OO best practice, we let each
	 *  step type handle its own particular validation via a polymorphic 
	 *  validate() method. If the step finds any errors it will call back into this
	 *  validator to report it.   */
	private void validateStep(ChartInfo chart, ChartUIElement element) {
		String factoryId = element.get(IlsProperty.FACTORY_ID);
		if(factoryId == null) return;
		if(factoryId.startsWith("com.ils")) {
			// Create one of our instances and let it do the validation
			ClientStepFactory stepFactory = registry.getStepFactory(factoryId).get();
			//StepUI stepUI = stepFactory.createStepUI(element);
			AbstractIlsStepDelegate stepDelegate = (AbstractIlsStepDelegate)stepFactory;
			stepDelegate.validate(chart, element, this);
		}
		else if(factoryId.equals(ActionStepProperties.FACTORY_ID)) {
			String startScript = element.get(ActionStepProperties.START_SCRIPT);
			validateScript(startScript, chart, element);
		}
	}

	private void validateScript(String script, ChartInfo chart, ChartUIElement element) {
		final String s88GetPrefix = "s88Get";
		final String s88SetPrefix = "s88Set";
		try {
			BufferedReader in = new BufferedReader(new StringReader(script));
			String line = null;
			while((line = in.readLine()) != null) {
				if(line.contains(s88GetPrefix) || line.contains(s88SetPrefix)) {
					int startIndex = line.indexOf(s88GetPrefix);
					int lparenIndex = line.indexOf("(", startIndex);
					if(lparenIndex >= 0) {
						int rparenIndex = line.indexOf(")", lparenIndex);
						if(rparenIndex >= 0) {
							String[] args = line.substring(lparenIndex+1, rparenIndex).split(",");
							String key = null;
							String location = null;
							if(line.contains(s88GetPrefix)) {
								location = args[3].trim();
								key = args[2].trim();
							}
							else {  // s88Set
								location = args[4].trim();
								key = args[2].trim();								
							}
							validateRecipeKey(location, key, chart, element);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("Error validating script", e);
		}
	}

	/** Check if the given recipe data exists as a step property--if not, add an 
	 *  error to that effect. Note: this does not necessarily mean the corresponding
	 *  tag has been created yet--this will happen automatically.
	 */
	public void validateRecipeKey(String scope, String keyPath, ChartInfo chart, ChartUIElement step) {
		try {
		ChartUIElement scopeElement = hierarchyAnalyzer.getElementForScope(scope, chart.path, step);
		if(scopeElement == null) {
			String errMsg = String.format("Step not found for recipe scope: %s:%s", scope, keyPath);
			addError(Long.valueOf(chart.resourceId), chart.path, getStepName(step), errMsg);			
		}
		else {
			List<Data> recipeData = Data.fromStepProperties(scopeElement);
			if(recipeData == null || !Data.hasPath(recipeData, keyPath)) {
				String errMsg = String.format("Recipe data not found: %s:%s", scope, keyPath);
				addError(Long.valueOf(chart.resourceId), chart.path, getStepName(step), errMsg);
			}
		}
		}
		catch(Exception e) {
			logger.error("Error checking for recipe data", e);
		}
	}

	private String getStepName(ChartUIElement element) {
		return element.get(IlsProperty.NAME);
	}

	private void addError(Long resourceId, String chartPath, String stepName, String message) {
		errors.add(new Object[] {resourceId, chartPath + "/" + stepName, message});
	}
	
	public static List<Object[]> validate(Project globalProject,
			ClientStepRegistry registry) {
		StepPropertyValidator validator = new StepPropertyValidator(
			globalProject, registry);
		validator.validateAllCharts();
		return validator.errors;
	}

}
