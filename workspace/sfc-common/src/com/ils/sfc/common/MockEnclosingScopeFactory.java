package com.ils.sfc.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.inductiveautomation.sfc.api.PyChartScope;

/**
 * A factory that produces a set of initial chart parameters that mocks out an enclosing 
 * hierarchy so that the child chart can be run in isolation, but the recipe data can be accessed 
 * as if the entire hierarchy were running.
 * 
 * If it is desired to run a single step in isolation, a parent chart consisting of a single Enclosing
 * step should be used, and the child dynamically assigned to be the desired step.
 *  
 * Usage: the result is passed to startChart() in place of the usual list of initial chart parameters.
 *
 */
public class MockEnclosingScopeFactory {
	private Map<String, Object> initialParams;
	private List<MockInfo> levelsBottomUp = new ArrayList<MockInfo>();


	/**
	 * @param initialParams the initial params you would ordinarily pass to startChart()
	 */
	public MockEnclosingScopeFactory(Map<String, Object> initialParams) {
		this.initialParams = initialParams;		
	}
	
 	public void addLevelBottomUp(String chartPath, String stepName, String stepFactoryId) {
 		levelsBottomUp.add(new MockInfo(chartPath, stepName, stepFactoryId)); 		
 	}
 	public void addLevelBottomUp(MockInfo mock) {
 		levelsBottomUp.add(mock); 		
 	}
	@SuppressWarnings("unchecked")
	public Map<String,Object> getInitialChartParams() {
		PyChartScope childScope = new PyChartScope();
		PyChartScope lowestChildScope = childScope;
		PyChartScope parentScope = null;
		for(MockInfo parentInfo: levelsBottomUp) {
			parentScope = new PyChartScope();
			childScope.put("parent", parentScope);
			parentScope.put("chartPath", parentInfo.getChartPath());
			PyChartScope enclosingStepScope = new PyChartScope();
			enclosingStepScope.put("name", parentInfo.getStepName());
			if(parentInfo.getStepFactoryId().equals(ProcedureStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.GLOBAL);
			}
			else if(parentInfo.getStepFactoryId().equals(OperationStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.OPERATION);
			}
			else if(parentInfo.getStepFactoryId().equals(PhaseStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.PHASE);
			}
			childScope.put(Constants.ENCLOSING_STEP_SCOPE_KEY, enclosingStepScope);
			childScope = parentScope;
		}
		
		if( parentScope!=null ) {
			// copy expected top-level params to top-level mock parent:
			parentScope.put(Constants.PROJECT, initialParams.get(Constants.PROJECT));
			parentScope.put(Constants.USER, initialParams.get(Constants.USER));
			parentScope.put(Constants.ISOLATION_MODE, initialParams.get(Constants.ISOLATION_MODE));
		}
		
		// copy lowest child scope info to initial Params
		initialParams.putAll(lowestChildScope);
		return initialParams;
	}

}
