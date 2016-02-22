package com.ils.sfc.common.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class MonitorDownloadStepDelegate extends AbstractIlsStepDelegate implements
MonitorDownloadStepProperties {
	private static final LoggerEx logger = LogUtil.getLogger(MonitorDownloadStepDelegate.class.getName());

	protected MonitorDownloadStepDelegate() {
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
		String timerLocation = element.get(IlsProperty.TIMER_LOCATION);
		String timerKey = element.get(IlsProperty.TIMER_KEY);
		if(!IlsSfcCommonUtils.isEmpty(timerKey)) {
			validator.validateRecipeKey(timerLocation, timerKey, chart, element);			
		}
		
		String recipeLocation = element.get(IlsProperty.RECIPE_LOCATION);
		try {
			MonitorDownloadsConfig config = MonitorDownloadsConfig.fromJSON(element.get(IlsProperty.MONITOR_DOWNLOADS_CONFIG));
			for(MonitorDownloadsConfig.Row row: config.getRows()) {
				validator.validateRecipeKey(recipeLocation, row.key, chart, element);
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}
	
}
