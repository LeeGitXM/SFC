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
	private Map<String, ChartStateEnum> chartStatesByRunId = new HashMap<String, ChartStateEnum>();
	private Set<String> cancelRequests = new HashSet<String>();
	private Set<String> pauseRequests = new HashSet<String>();
	
	public synchronized void registerSfcProject(String projectName) {
		sfcProjectNames.add(projectName);
	}

	/** ILS code has initiated a cancel (even if IA hasn't processed it yet)  */
	public synchronized void setCancelRequested(String chartRunId) {
		cancelRequests.add(chartRunId);
	}
	
	/** ILS code has initiated a paused (even if IA hasn't processed it yet)  */
	public synchronized void setPauseRequested(String chartRunId) {
		pauseRequests.add(chartRunId);
	}

	/** ILS code has initiated a paused (even if IA hasn't processed it yet)  */
	public synchronized void setResumeRequested(String chartRunId) {
		pauseRequests.remove(chartRunId);
	}

	/** Check if ILS code has initiated a cancel (even if IA hasn't processed it yet)  */
	public synchronized boolean getCancelRequested(String chartRunId) {
		return cancelRequests.contains(chartRunId);
	}
	
	/** Check if ILS code has initiated a paused (even if IA hasn't processed it yet)  */
	public synchronized boolean getPauseRequested(String chartRunId) {
		return pauseRequests.contains(chartRunId);
	}
	
	@Override
	public synchronized void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		String runIdAsString = chartId.toString();
		chartStatesByRunId.put(runIdAsString, newChartState);
		
		// message the clients about the chart status:
		if(sfcProjectNames.size() > 0) {
			for(String projectName: sfcProjectNames) {
				PyDictionary payload = new PyDictionary();
				payload.put("instanceId", runIdAsString);
				payload.put("status", newChartState.toString());
				try {
					PythonCall.SEND_CHART_STATUS.exec(projectName, payload);
				} catch (JythonExecException e) {
					logger.error("error sending chart status", e);
				}
			}
			
			// prevent a memory leak by clearing the map after the chart finishes
			if(newChartState.isTerminal()) {
				chartStatesByRunId.remove(runIdAsString);
				cancelRequests.remove(runIdAsString);
				pauseRequests.remove(runIdAsString);
			}
		}
		else {
			//logger.error("Error sending chart status msg: no sfc project names");
		}
		//System.out.println("chart " + chartId.toString() + " " + newChartState.toString());
	}

	@Override
	public synchronized void onElementStateChange(UUID elementId, UUID chartId, ElementStateEnum oldElementState,
			ElementStateEnum newElementState) {
	}

	@Override
	public synchronized void onBeforeChartStart(ChartContext chartContext) {
		createTags(chartContext);
		//String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
		//controller.setElement(stepComponent.getElement(), chartPath);

	}

	private synchronized void createTags(ChartContext chartContext) {
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
