package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ManualDataEntryStepDelegate extends AbstractIlsStepDelegate implements
ManualDataEntryStepProperties {
	private static final LoggerEx logger = LogUtil.getLogger(ManualDataEntryStepDelegate.class.getName());

	protected ManualDataEntryStepDelegate() {
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
			ManualDataEntryConfig config = ManualDataEntryConfig.fromJSON(element.get(IlsProperty.MANUAL_DATA_CONFIG));
			for(ManualDataEntryConfig.Row row: config.getRows()) {
				validator.validateRecipeKey(row.destination, row.key, chart, element);
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}
}
