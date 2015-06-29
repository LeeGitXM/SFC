package com.ils.sfc.gateway;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;

/** Since we are having such trouble getting static module variables to work in external Python,
 *  this is a class that holds onto arbitrary objects associated with a particular chart run, 
 *  for as long as that chart is active. It is accessible from Jython through IlsGatewayScripts.
 *  This can serve as a bridge between message handlers, which have no chart context, and step
 *  methods, which do.
 */
public class IlsDropBox implements ChartObserver {
	private static LoggerEx logger = LogUtil.getLogger(IlsDropBox.class.getName());
	private Map<String, Map<String, Object>> objectsByIdByChartRunId = 
		new HashMap<String, Map<String, Object>>();
	
	public synchronized void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		String runIdAsString = chartId.toString();	
		if(!objectsByIdByChartRunId.containsKey(runIdAsString)) {
			objectsByIdByChartRunId.put(runIdAsString, new HashMap<String,Object>());
		}
		// prevent a memory leak by clearing the map after the chart finishes
		if(newChartState.isTerminal()) {
			objectsByIdByChartRunId.remove(runIdAsString);
		}
	}

	public synchronized void put(String chartId, String objectId, Object object) {
		Map<String, Object> objectsById = objectsByIdByChartRunId.get(chartId);
		if(objectsById != null) {
			objectsById.put(objectId, object);
		}
		else {
			logger.error("could not find dropbox for chartId " + chartId);
		}
	}
	
	public synchronized Object get(String chartId, String objectId) {
		Map<String, Object> objectsById = objectsByIdByChartRunId.get(chartId);
		if(objectsById != null) {
			return objectsById.get(objectId);
		}
		else {
			// This can happen as a race condition if a client hasn't rcvd a chart-over msg
			logger.debug("could not find dropbox for chartId " + chartId);
			return null;
		}		
	}
	
	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,
			ElementStateEnum arg2, ElementStateEnum arg3) {
	}

}
