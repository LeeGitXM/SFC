package com.ils.sfc.common.step;


import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.StepPropertyValidator;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class PVMonitorStepDelegate extends AbstractIlsStepDelegate implements
PVMonitorStepProperties {
	private static LoggerEx logger = LogUtil.getLogger(PVMonitorStepDelegate.class.getName());
	
	protected PVMonitorStepDelegate() {
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
		logger.infof("In validate() for PV Monitor");
	}


	@Override
	public void validate(ChartInfo chart, ChartUIElement element, StepPropertyValidator validator) {
		logger.infof("In validate2() for PV Monitor");
		
		// validate recipe data keys:
		String timerLocation = element.get(IlsProperty.TIMER_LOCATION);
		String timerKey = element.get(IlsProperty.TIMER_KEY);
		if(!IlsSfcCommonUtils.isEmpty(timerKey)) {
			validator.validateRecipeKey(timerLocation, timerKey, chart, element);			
		}
		
		String timeLimitStrategy = element.get(IlsProperty.TIME_LIMIT_STRATEGY);
		if(timeLimitStrategy.equals(Constants.RECIPE)) {
			String tlRecipeKey = element.get(IlsProperty.TIME_LIMIT_RECIPE_KEY);
			String tlRecipeLoc = element.get(IlsProperty.TIME_LIMIT_RECIPE_LOCATION);
			validator.validateRecipeKey(tlRecipeLoc, tlRecipeKey, chart, element);
		}
		
		String recipeLocation = element.get(IlsProperty.RECIPE_LOCATION);
		try {
			PVMonitorConfig config = PVMonitorConfig.fromJSON(element.get(IlsProperty.PV_MONITOR_CONFIG));
			for(PVMonitorConfig.Row row: config.getRows()) {
				if(row.targetType.equals(Constants.RECIPE)) {
					String targetName = (String)row.targetNameIdOrValue;
					validator.validateRecipeKey(recipeLocation, targetName, chart, element);
				}
			}
		}
		catch(Exception e) {
			logger.error("Error validating block config", e);
		}
	}
	
}
