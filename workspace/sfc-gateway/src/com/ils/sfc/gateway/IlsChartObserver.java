package com.ils.sfc.gateway;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.python.core.PyDictionary;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.gateway.recipe.RecipeDataAccess;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.api.elements.StepElement;

/** An observer that listens to SFC chart status changes and messages the client
 *  so that the ControlPanel status display (e.g.) can stay up to date. */
public class IlsChartObserver implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsChartObserver.class.getName());

	@Override
	public synchronized void onBeforeChartStart(ChartContext chartContext) {
		
		// If this is an ILS chart, create the recipe data tags
		PyDictionary chartScope = chartContext.getChartScope();
		String projectName = (String)chartScope.get(Constants.PROJECT);
		if(projectName != null) {  // only ILS charts will have this
			createTags(chartContext);
		}
	}
	
	private synchronized void createTags(ChartContext chartContext) {
		PyChartScope chartScope = chartContext.getChartScope();
		String chartPath = (String)chartScope.get("chartPath");
		boolean isolationMode = RecipeDataAccess.getIsolationMode(chartScope);
		String tagProvider = RecipeDataAccess.getProviderName(isolationMode);
		for(ChartElement<?> element: chartContext.getElements()) {
			if(element instanceof StepElement) {
				StepElement stepElement = (StepElement)element;
				try {
					PropertySet stepProperties = stepElement.getDefinition().getProperties();
					List<Data> recipeData = Data.fromStepProperties(stepProperties);
					String stepName = stepProperties.get(IlsProperty.NAME);
					String stepPath = chartPath + "/" + stepName;
					for(Data data: recipeData) {
						data.setProvider(tagProvider);
						data.setStepPath(stepPath);
						data.createTag();
					}
				} catch (Exception e) {
					logger.error("Error creating tags for chart " + chartPath, e);
				}
			}
		}
	}


	@Override
	public void onChartStateChange(UUID arg0, ChartStateEnum arg1,
			ChartStateEnum arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,
			ElementStateEnum arg2, ElementStateEnum arg3) {
		// TODO Auto-generated method stub
		
	}

}

