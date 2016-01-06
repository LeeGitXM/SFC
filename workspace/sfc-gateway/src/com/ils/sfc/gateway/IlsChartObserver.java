package com.ils.sfc.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private static class ChartRunInfo {
		ChartStateEnum chartState;
		String chartRunId;
		String chartPath;
		String parentRunId;
		String projectName;
		Map<String,ElementStateEnum> elementStateById = new HashMap<String,ElementStateEnum>();
		
		public ChartRunInfo(String chartRunId, String chartPath, String parentRunId, String projectName) {
			this.chartRunId = chartRunId;
			this.chartPath = chartPath;
			this.parentRunId = parentRunId;
			this.projectName = projectName;
		} 
	}
	
	private static LoggerEx logger = LogUtil.getLogger(IlsChartObserver.class.getName());
	private static Map<String,ChartRunInfo> runInfoById = new HashMap<String,ChartRunInfo>();
	private Set<String> cancelRequests = new HashSet<String>();
	private Set<String> pauseRequests = new HashSet<String>();

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
		ChartRunInfo info = runInfoById.get(runIdAsString);
		if(info == null) {
			return;  // not an ILS chart
		}
		info.chartState = newChartState;
		
		//sendStatusToClient(newChartState, runIdAsString, info.projectName);
		
		// prevent a memory leak by clearing the map after the chart finishes
		if(newChartState.isTerminal()) {
			runInfoById.remove(runIdAsString);
			cancelRequests.remove(runIdAsString);
			pauseRequests.remove(runIdAsString);
		}
	}

/*
	private void sendStatusToClient(ChartStateEnum newChartState,
			String runIdAsString, String projectName) {
		PyDictionary payload = new PyDictionary();
		payload.put("instanceId", runIdAsString);
		payload.put("status", newChartState.toString());
		try {
			PythonCall.SEND_CHART_STATUS.exec(projectName, payload);
		} catch (JythonExecException e) {
			logger.error("error sending chart status", e);
		}
	}
*/
	@Override
	public synchronized void onElementStateChange(UUID chartId, UUID elementId, ElementStateEnum oldElementState,
		ElementStateEnum newElementState) {
		ChartRunInfo info = runInfoById.get(chartId.toString());
		if(info == null) {
			return;  // not an ILS chart
		}
		info.elementStateById.put(elementId.toString(), newElementState);
		//System.out.println(chartId.toString() + " " + elementId.toString() + " " + newElementState);

	}

	@Override
	public synchronized void onBeforeChartStart(ChartContext chartContext) {
		PyDictionary chartScope = chartContext.getChartScope();
		String projectName = (String)chartScope.get(Constants.PROJECT);
		// TODO: look up hierarchy for project name. If not seen, ignore--not our chart
		if(projectName == null) return; // not a top-level ILS SFC
		String chartPath = (String)chartScope.get(Constants.CHART_PATH);
		String chartRunId = (String)chartScope.get(Constants.INSTANCE_ID);
		String parentRunId = chartScope.get(Constants.PARENT) != null ?
			(String)((PyDictionary)chartScope.get(Constants.PARENT)).get(Constants.INSTANCE_ID) : 
			null;		
		ChartRunInfo info = new ChartRunInfo(chartRunId, chartPath, parentRunId, projectName);
		runInfoById.put(chartRunId,  info);
		createTags(chartContext);
}

	private synchronized void createTags(ChartContext chartContext) {
		PyChartScope chartScope = chartContext.getChartScope();
		String chartPath = (String)chartScope.get("chartPath");
		boolean isolationMode = RecipeDataAccess.getIsolationMode(chartScope);
		String tagProvider = RecipeDataAccess.getProviderName(isolationMode);
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
						data.setProvider(tagProvider);
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
