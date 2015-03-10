package com.ils.sfc.gateway;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.python.core.PyDictionary;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;

public class IlsChartObserver implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsChartObserver.class.getName());
	private Set<String> sfcProjectNames = new HashSet<String>();
	private Map<String, String> chartStatesByRunId = new HashMap<String, String>();
	private Map<String, String> elementStatesById = new HashMap<String, String>();
	
	public void registerSfcProject(String projectName) {
		sfcProjectNames.add(projectName);
	}
	
	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		String runIdAsString = chartId.toString();
		String status =  newChartState.toString();
		chartStatesByRunId.put(runIdAsString, status);
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
		}
		else {
			//logger.error("Error sending chart status msg: no sfc project names");
		}
		//System.out.println("chart " + chartId.toString() + " " + newChartState.toString());
	}

	@Override
	public void onElementStateChange(UUID elementId, ElementStateEnum oldElementState,
			ElementStateEnum newElementState) {
		elementStatesById.put(elementId.toString(), newElementState.toString());		
		//System.out.println("element " + elementId.toString() + " " + newElementState.toString());
	}

}
