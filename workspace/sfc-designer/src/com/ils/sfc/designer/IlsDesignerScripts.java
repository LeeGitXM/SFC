package com.ils.sfc.designer;

import java.util.List;

import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcRequestHandler;
import com.ils.sfc.common.chartStructure.ChartModelInfo;
import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.ils.sfc.common.chartStructure.StepStructure;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

public class IlsDesignerScripts {
	private static LoggerEx logger = LogUtil.getLogger(IlsDesignerScripts.class.getSimpleName());
	private static IlsSfcDesignerHook hook;

	public static void createRecipeDataTags() {
		ChartStructureCompiler compiler = hook.getChartStructureManager().getCompiler();
		compiler.compile(); // get the latest definitions
		IlsSfcRequestHandler requestHandler = new IlsSfcRequestHandler();
		String provider = requestHandler.getProviderName(false);
		for(StepStructure step: compiler.getSteps()) {
			long resourceId = step.getChart().getResourceId();
			ChartModelInfo chartInfo = compiler.getChartInformation(resourceId);
			String chartPath = chartInfo.chartPath;
			String stepName = step.getName();
			if(step.getProperties() == null) {
				logger.errorf("no properties for step %s", stepName);
				continue;
			}
			JSONObject assDataJson = step.getProperties().get(IlsProperty.ASSOCIATED_DATA);
			if(assDataJson == null) continue;
			String stepPath = chartPath + "/" + stepName;
			try {
				List<Data> recipeData = Data.fromAssociatedData(assDataJson);
				for(Data data: recipeData) {
					data.setProvider(provider);
					data.setStepPath(stepPath);
					data.createTag(null);
					logger.infof("Created tag %s", data.getTagPath());
				}
			}
			catch(Exception e) {
				logger.error("Error creating recipe data tag", e);
			}
		}
	}

	public static void setHook(IlsSfcDesignerHook _hook) {
		hook = _hook;
	}
}
