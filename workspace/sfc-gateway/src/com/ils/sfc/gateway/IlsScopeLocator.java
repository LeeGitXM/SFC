package com.ils.sfc.gateway;

import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;

public class IlsScopeLocator implements ScopeLocator {
	public static final String enclosingStepScopeKey = "enclosingStepScope";
	public static final String s88LevelKey = "s88Level";
	
	@Override
	public PyChartScope locate(ScopeContext scopeContext, String identifier) {
		// check this step first, then walk up the hierarchy:
		PyChartScope stepScope = scopeContext.getStepScope();
		PyChartScope chartScope = scopeContext.getChartScope();
		while(stepScope != null) {
			if(identifier.equals(stepScope.get(s88LevelKey))) {
				break;
			}
			else {
				stepScope = chartScope.getSubScope(enclosingStepScopeKey);
			}
		}
		return stepScope;
	}

}
