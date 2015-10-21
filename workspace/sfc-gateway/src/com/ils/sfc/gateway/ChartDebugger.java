package com.ils.sfc.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.MockEnclosingScopeFactory;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.ChartManager;
import com.inductiveautomation.sfc.api.StepRegistry;

/** Functionality for debugging SFC charts */
public class ChartDebugger {
	private String TAG = this.getClass().getSimpleName();
	private LoggerEx logger = LogUtil.getLogger(TAG);
	private Project globalProject;
	private StepRegistry stepRegistry;
	
	public ChartDebugger(Project globalProject, StepRegistry stepRegistry) {
		this.stepRegistry = stepRegistry;
		this.globalProject = globalProject;
	}
	
	/** Run a chart in isolation, mocking out parent hierarchy if necessary. */
	public UUID debugChart(String chartPath, String clientProject, String user, boolean isolation) {
		try {
			
			// add the normal expected properties at the top level
			Map<String,Object> initialParameters = new HashMap<String,Object>();
			initialParameters.put(Constants.PROJECT, clientProject);
			initialParameters.put(Constants.ISOLATION_MODE, isolation);
			initialParameters.put(Constants.USER, user); 	
			initialParameters.put("startTime", new java.util.Date());
			
			// build a dummy hierarchy for any enclosing parent steps
			SimpleHierarchyAnalyzer analyzer = new SimpleHierarchyAnalyzer(globalProject, stepRegistry);
			analyzer.analyze();
			MockEnclosingScopeFactory factory = new MockEnclosingScopeFactory(initialParameters, 
				analyzer.getEnclosureHierarchyBottomUp(chartPath, false));
			Map<String,Object> debugParams = factory.getInitialChartParams();
			
			// get the message queue from the hierarchy
			String messageQueue = analyzer.getMessageQueue(chartPath);
			if(messageQueue == null) messageQueue = "msgQueueNotFoundInHierarchy";
			getTopParams(debugParams).put(Constants.MESSAGE_QUEUE, messageQueue);
			
			// start the chart
			return ChartManager.get().startChart(chartPath, debugParams, user);
		}
		catch(Exception e) {
			logger.errorf("%s.startChart: exception trying to start chart %s\n", TAG, chartPath, e);
			return null;			
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> getTopParams(Map<String,Object> params) {
		while(params.get(Constants.PARENT) != null) {
			params = (Map<String,Object>) params.get(Constants.PARENT);
		}
		return params;
	}
}
