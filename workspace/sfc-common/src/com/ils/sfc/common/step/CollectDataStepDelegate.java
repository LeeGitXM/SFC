package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.CollectDataConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class CollectDataStepDelegate extends AbstractIlsStepDelegate implements
CollectDataStepProperties {
	private static final LoggerEx logger = LogUtil.getLogger(CollectDataStepDelegate.class.getName());
	protected CollectDataStepDelegate() {
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
		// validate recipe data keys:
		try {
			CollectDataConfig config = CollectDataConfig.fromJSON(element.get(IlsProperty.COLLECT_DATA_CONFIG));
			for(CollectDataConfig.Row row: config.getRows()) {
				validator.validateRecipeKey(row.location, row.recipeKey, chart, element);
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}

}
