package com.ils.sfc.gateway.recipe;

import system.ils.sfc.common.Constants;

import com.ils.sfc.gateway.IlsSfcGatewayHook;
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
		PyChartScope stepScope = scopeContext.getStepOrPrevious();
		PyChartScope chartScope = scopeContext.getChartScope();
		String providerName = RecipeDataAccess.getProviderName(RecipeDataAccess.getIsolationMode(chartScope));
		if(identifier.equals(Constants.TAG)) {
			return new RecipeDataChartScope("", providerName, hook.getContext(), false);				
		}
		else {
			// check this step first, then walk up the hierarchy:
			String tagPath = RecipeDataAccess.getRecipeDataTagPath(chartScope, stepScope, identifier);
			return new RecipeDataChartScope(tagPath, providerName, hook.getContext(), true);	
		}
	}

}
