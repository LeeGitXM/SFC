/**
 * Copyright 2015. ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway.monitor;
/** 
 * This class holds the most recent state of a chart and its steps.
 * The intent of the class is to provide a well-known location to
 * query last-known chart state.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.SfcGatewayHook;

public class IlsStepMonitor implements ChartObserver {
	private final static String TAG = "IlsStepMonitor";
	private final static LoggerEx log = LogUtil.getLogger(IlsStepMonitor.class.getPackage().getName());
	private ChartManagerService chartManager = null;
	private IlsStepNameMapper stepNameMapper = null;
	private final Map<String, String> chartNameForId;
	private final Map<String, String> elementStateForKey;
	
	public IlsStepMonitor() {
		chartNameForId = new HashMap<String, String>();
		elementStateForKey = new HashMap<String, String>();
	}
	
	public void initialize(GatewayContext context,ChartManagerService service,SfcGatewayHook iaSfcHook) {
		chartManager = service;
		stepNameMapper = new IlsStepNameMapper(context,iaSfcHook);
		stepNameMapper.initialize();
	}
	public void clear() {
	}
	public void start() {
		if( chartManager!=null ) chartManager.addChartObserver(this);
	}
	public void shutdown() {
		stop();
		chartManager = null;
	}
	
	/**
	 * This call sets the mapping between the running chart Id and its name.
	 * Thus far I haven't figured out how to do this internally. 
	 * @param id
	 * @param name is the full folder path to the top-level chart.
	 *             It does not start with a "/".
	 */
	public void watch(String id,String name) {
		log.debugf("%s.watch: %s is %s",TAG,name,id);
		chartNameForId.put(id, name);
	}
	
	public void stop() {
		if( chartManager!=null ) chartManager.removeChartObserver(this);
	}
	/**
	 * Do nothing for a chart.
	 */
	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,ChartStateEnum newChartState) {
		log.debugf("%s.onChartStateChange: %s is %s",TAG,chartId.toString(), newChartState.toString());
	}

	@Override
	public void onElementStateChange(UUID chartId, UUID elementId, ElementStateEnum oldState,ElementStateEnum newState) {		
		String key = makeKey(chartId.toString(),elementId.toString());
		elementStateForKey.put(key, newState.name());
		log.tracef("%s.onElementStateChange: %s is %s",TAG,key,newState.name());
	}

	/**
	 * @return the most recent state for the specified chart and step
	 * @return
	 */
	public String stepState(String chartId,String stepName) {
		String stepState = "CHART_UNKNOWN";
		String chartName = chartNameForId.get(chartId);
		if( chartName!=null && stepNameMapper!=null ) {
			stepState = "STEP_UNKNOWN";
			String stepId = stepNameMapper.idForName(chartName,stepName);
			if( stepId != null ) {
				stepState = "UNSET";
				String key = makeKey(chartId,stepId);
				String state = elementStateForKey.get(key);
				if( state!=null ) stepState = state;
				else {
					log.warnf("%s.stepState: No current state for %s ",TAG,key);
				}
			}
		}
		log.debugf("%s.stepState: %s:%s is %s",TAG,chartId.toString(),stepName,stepState);
		return stepState;
	}
	
	private String makeKey(String chartId,String stepId) {
		StringBuilder sb = new StringBuilder();
		sb.append(chartId);
		sb.append(":");
		sb.append(stepId);
		return sb.toString();
	}

	@Override
	public void onBeforeChartStart(ChartContext arg0) {
		// TODO Auto-generated method stub
		
	}
}
