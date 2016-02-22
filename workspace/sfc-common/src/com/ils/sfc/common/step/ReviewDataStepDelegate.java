package com.ils.sfc.common.step;

import java.util.ArrayList;
import java.util.List;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ReviewDataStepDelegate extends AbstractIlsStepDelegate implements
ReviewDataStepProperties {
	private static final LoggerEx logger = LogUtil.getLogger(ReviewDataStepDelegate.class.getName());
	
	protected ReviewDataStepDelegate() {
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
			ReviewDataConfig primaryConfig = ReviewDataConfig.fromJSON(element.get(IlsProperty.PRIMARY_REVIEW_DATA));
			ReviewDataConfig secondaryConfig = ReviewDataConfig.fromJSON(element.get(IlsProperty.SECONDARY_REVIEW_DATA));
			List<ReviewDataConfig.Row> rows = new ArrayList<ReviewDataConfig.Row>();
			rows.addAll(primaryConfig.getRows());
			rows.addAll(secondaryConfig.getRows());
			for(ReviewDataConfig.Row row: rows) {
				validator.validateRecipeKey(row.recipeScope, row.valueKey, chart, element);
				if(!IlsSfcCommonUtils.isEmpty(row.configKey)) {
					validator.validateRecipeKey(row.recipeScope, row.configKey, chart, element);
				}
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}
}

