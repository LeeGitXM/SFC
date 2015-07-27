package com.ils.sfc.gateway;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;


public class IlsScopeLocator implements ScopeLocator {
	private static LoggerEx logger = LogUtil.getLogger(IlsScopeLocator.class.getName());
	private final IlsSfcGatewayHook hook;
	
	public IlsScopeLocator(IlsSfcGatewayHook hook) {
		this.hook = hook;
	}
	
	/** Given an identifier like local, superior, previous, global, operation
	 *  return the corresponding STEP scope. The enclosing step scope is stored
	 *  in the chart scope of the level BELOW it
	 */
	@Override
	public synchronized PyChartScope locate(ScopeContext scopeContext, String identifier) {
		// check this step first, then walk up the hierarchy:
		PyChartScope stepScope = scopeContext.getStepOrPrevious();
		PyChartScope chartScope = scopeContext.getChartScope();
		String tagPath = RecipeDataAccess.getRecipeDataTagPath(chartScope, stepScope, identifier);
		String providerName = RecipeDataAccess.getProviderName(RecipeDataAccess.getIsolationMode(chartScope));
		return new RecipeDataChartScope(tagPath, providerName, hook.getContext());			
	}

}
