package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewFlowsStepDelegate extends AbstractIlsStepDelegate implements
ReviewFlowsStepProperties {
	private static final LoggerEx logger = LogUtil.getLogger(ReviewFlowsStepDelegate.class.getName());
	
	protected ReviewFlowsStepDelegate() {
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
		String buttonLocation = element.get(IlsProperty.BUTTON_KEY_LOCATION);
		String buttonKey = element.get(IlsProperty.BUTTON_KEY);
		validator.validateRecipeKey(buttonLocation, buttonKey, chart, element);

		try {
			ReviewFlowsConfig config = ReviewFlowsConfig.fromJSON(element.get(IlsProperty.REVIEW_FLOWS));
			for(ReviewFlowsConfig.Row row: config.getRows()) {
				validator.validateRecipeKey(row.destination, row.flow1Key, chart, element);
				validator.validateRecipeKey(row.destination, row.flow2Key, chart, element);
				if(!"sum".equals(row.flow3Key)) {
					validator.validateRecipeKey(row.destination, row.flow3Key, chart, element);
				}
				if(!IlsSfcCommonUtils.isEmpty(row.configKey)) {
					validator.validateRecipeKey(row.destination, row.configKey, chart, element);
				}
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}

}
