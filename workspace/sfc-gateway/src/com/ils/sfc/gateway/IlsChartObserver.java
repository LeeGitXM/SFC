package com.ils.sfc.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONObject;
import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.common.config.PropertySet;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.elements.AbstractStepElement;

/** An observer that listens to SFC chart status changes and messages the client
 *  so that the ControlPanel status display (e.g.) can stay up to date. */
public class IlsChartObserver implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsChartObserver.class.getName());
	private Set<String> sfcProjectNames = new HashSet<String>();
	private Map<String, String> chartStatesByRunId = new HashMap<String, String>();
	
	public void registerSfcProject(String projectName) {
		sfcProjectNames.add(projectName);
	}
	
	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		String runIdAsString = chartId.toString();
		String status =  newChartState.toString();
		chartStatesByRunId.put(runIdAsString, status);
		
		// message the clients about the chart status:
		if(sfcProjectNames.size() > 0) {
			for(String projectName: sfcProjectNames) {
				PyDictionary payload = new PyDictionary();
				payload.put("instanceId", runIdAsString);
				payload.put("status", status);
				try {
					PythonCall.SEND_CHART_STATUS.exec(projectName, payload);
				} catch (JythonExecException e) {
					logger.error("error sending chart status", e);
				}
			}
			
			// prevent a memory leak by clearing the map after the chart finishes
			if(newChartState.isTerminal()) {
				chartStatesByRunId.remove(runIdAsString);
			}
		}
		else {
			//logger.error("Error sending chart status msg: no sfc project names");
		}
		//System.out.println("chart " + chartId.toString() + " " + newChartState.toString());
	}

	@Override
	public void onElementStateChange(UUID elementId, UUID chartId, ElementStateEnum oldElementState,
			ElementStateEnum newElementState) {
	}

	@Override
	public void onBeforeChartStart(ChartContext chartContext) {
		createTags(chartContext);
		//String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
		//controller.setElement(stepComponent.getElement(), chartPath);

	}

	private void createTags(ChartContext chartContext) {
		String chartPath = (String)chartContext.getChartScope().get("chartPath");
		for(ChartElement<?> element: chartContext.getElements()) {
			if(element instanceof StepElement) {
				StepElement stepElement = (StepElement)element;
				PropertySet stepProperties = stepElement.getDefinition().getProperties();
				JSONObject assDataJson = stepProperties.get(IlsProperty.ASSOCIATED_DATA);
				if(assDataJson == null) continue;
				String stepName = stepProperties.get(IlsProperty.NAME);
				String stepPath = chartPath + "/" + stepName;
				try {
					List<Data> recipeData = Data.fromAssociatedData(assDataJson);
					for(Data data: recipeData) {
						String provider = IlsGatewayScripts.getProviderName(false);
						data.setProvider(provider);
						data.setStepPath(stepPath);
						data.createTag(null);
					}
				} catch (Exception e) {
					logger.error("Error creating tags for chart " + chartPath, e);
				}
			}
		}
	}

}
