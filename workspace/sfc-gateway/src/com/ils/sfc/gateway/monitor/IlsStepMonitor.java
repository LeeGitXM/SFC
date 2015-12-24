/**
 * Copyright 2015. ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway.monitor;
/** 
 * This class holds the most recent state of a chart instance and its steps.
 * The intent of the class is to provide a well-known location to
 * query last-known chart state. It's intent is to assist testing.
 * It is not normally running.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.chartStructure.StepStructure;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartManagerService;

public class IlsStepMonitor implements ChartObserver {
	private final static String TAG = "IlsStepMonitor";
	private final static LoggerEx log = LogUtil.getLogger(IlsStepMonitor.class.getPackage().getName());
	private final static boolean DEBUG_STEP_MONITOR = false;  // Easy way to turn on logging while debugging
	private final ChartManagerService chartService;
	private final ChartStructureManager structureManager;
	private final Map<String, ChartStateHolder>   chartStateForId;
	private final Map<String, ElementStateHolder> elementStateForKey;
	
	public IlsStepMonitor(ChartStructureManager sm,ChartManagerService cms ) {
		this.structureManager = sm;
		this.chartService = cms;
		chartStateForId = new HashMap<>();
		elementStateForKey = new HashMap<>();
	}
	
	
	/**
	 * We allow the capability to clear the maps in order to detect 
	 * steps that have not been reached.
	 */
	public void clear() {
		chartStateForId.clear();
		elementStateForKey.clear();
	}
	public void start() {
		if( log.isDebugEnabled() || DEBUG_STEP_MONITOR ) log.infof("%s.start: ================================ Start Monitor =======================",TAG);
		chartService.addChartObserver(this);
	}
	
	// To be called as the hook shuts down.
	public void shutdown() {
		stop();
	}
	
	/**
	 * This call sets the mapping between the running chart Id and its name.
	 * Thus far I haven't figured out how to do this internally. 
	 * @param id
	 * @param name is the full folder path to the top-level chart.
	 *             It does not start with a "/".
	 */
	public synchronized void watch(String id,String name) {
		if( log.isDebugEnabled() || DEBUG_STEP_MONITOR ) log.infof("%s.watch: %s is %s",TAG,name,id);
		chartStateForId.put(id, new ChartStateHolder(name));
	}
	
	public void stop() {
		chartService.removeChartObserver(this);
		clear();
	}
	/**
	 * Do nothing for a chart.
	 */
	@Override
	public synchronized void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,ChartStateEnum newChartState) {
		String id = chartId.toString();
		ChartStateHolder stateHolder = chartStateForId.get(id);
		if( stateHolder!=null ) {
			stateHolder.setState(newChartState);
			if( log.isDebugEnabled() || DEBUG_STEP_MONITOR ) log.infof("%s.onChartStateChange: %s is %s",TAG,stateHolder.getPath(), newChartState.toString());
		}
	}

	@Override
	public synchronized void onElementStateChange(UUID chartId, UUID elementId, ElementStateEnum oldState,ElementStateEnum newState) {		
		String key = makeKey(chartId.toString(),elementId.toString());
		ElementStateHolder stateHolder = elementStateForKey.get(key);
		if( stateHolder==null ) {
			stateHolder = new ElementStateHolder();
			elementStateForKey.put(key, stateHolder);
		}
		stateHolder.setState(newState);
		if( log.isDebugEnabled() || DEBUG_STEP_MONITOR ) log.infof("%s.onElementStateChange: %s is %s",TAG, key, newState.toString());
	}
	/**
	 * @return the most recent state for the specified chart
	 * @return
	 */
	public String chartState(String chartId) {
		String result = "NOT_FOUND";
		ChartStateHolder stateHolder = chartStateForId.get(chartId);
		if( stateHolder!=null ) {
			result = stateHolder.getState().name();
			if( log.isDebugEnabled() || DEBUG_STEP_MONITOR ) log.infof("%s.chartState: %s is %s",TAG,stateHolder.getPath(), result);
		}
		return result;
	}
	/**
	 * @return a count of the number of times this step has been activated.
	 */
	public long stepCount(String chartId,String stepName) {
		long count = 0;
		ChartStateHolder chartStateHolder = chartStateForId.get(chartId);
		if( chartStateHolder!=null ) {
			StepStructure ss =  structureManager.getCompiler().getStepInformation(chartStateHolder.getPath(),stepName);
			if( ss!=null ) {
				String stepId = ss.getId();
				if( stepId != null ) {
					String key = makeKey(chartId,stepId);
					ElementStateHolder stateHolder = elementStateForKey.get(key);
					if( stateHolder!=null ) count = stateHolder.getCount();
				}
			}
		}
		return count;
	}
	/**
	 * @return the most recent state for the specified chart and step
	 */
	public String stepState(String chartId,String stepName) {
		String result = "NO_SUCH_CHART";
		ChartStateHolder chartStateHolder = chartStateForId.get(chartId);
		if( chartStateHolder!=null ) {
			StepStructure ss =  structureManager.getCompiler().getStepInformation(chartStateHolder.getPath(),stepName);
			if( ss!=null ) {
				String stepId = ss.getId();
				result = "NO_SUCH_STEP";
				if( stepId != null ) {
					String key = makeKey(chartId,stepId);
					ElementStateHolder stateHolder = elementStateForKey.get(key);
					result = "NOT_VISITED";
					if( stateHolder!=null ) result = stateHolder.getState().name();
				}
			}
		}
		return result;
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
		// Do nothing	
	}
	
	/**
	 * The state-holder contains the current state as well as a count 
	 * of the number of times the step/transition was started.
	 */
	private class ChartStateHolder {
		private ChartStateEnum state;
		private String path;

		public ChartStateHolder(String cpath) {
			this.path  = cpath;
			this.state = ChartStateEnum.Initial;
		}
		public ChartStateEnum getState() { return this.state; }
		public String getPath() { return this.path; }
		public void setState(ChartStateEnum s) { this.state = s; }
 	}
	/**
	 * The state-holder contains the current state as well as a count 
	 * of the number of times the step/transition was started.
	 */
	private class ElementStateHolder {
		private ElementStateEnum state;
		private long count = 0;

		public ElementStateHolder() {
			this.state = ElementStateEnum.Inactive;
			this.count = 0;
		}
		public ElementStateEnum getState() { return this.state; }
		public long getCount() { return this.count; }
		public void setState(ElementStateEnum s) { 
			state = s;
			if( s.equals(ElementStateEnum.Activating)) count++; 
		}
 	}
}
