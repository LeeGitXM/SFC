package com.ils.sfc.gateway;

import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;

public class IlsScopeLocator implements ScopeLocator {
	/** Given an identifier like local, superior, previous, global, operation
	 *  return the corresponding STEP scope. The enclosing step scope is store
	 *  in the chart scope of the level BELOW it
	 */
	@Override
	public PyChartScope locate(ScopeContext scopeContext, String identifier) {
		// check this step first, then walk up the hierarchy:
		PyChartScope stepScope = scopeContext.getStepScope();
		PyChartScope chartScope = scopeContext.getChartScope();
		return IlsGatewayScripts.resolveScope(chartScope, stepScope, identifier);
	}

}
