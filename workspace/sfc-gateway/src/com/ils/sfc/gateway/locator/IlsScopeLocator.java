/**
 *   (c) 2017  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.gateway.locator;


import com.ils.sfc.gateway.IlsSfcGatewayHook;
import com.ils.sfc.gateway.recipe.RecipeDataAccess;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;

import system.ils.sfc.common.Constants;

/**
 * Handle locating the special ILS recipe data sources as well as tag sources.
 */
public class IlsScopeLocator implements ScopeLocator {
	private final IlsSfcGatewayHook hook;
	private static LoggerEx log = LogUtil.getLogger(IlsScopeLocator.class.getName());
	
	public IlsScopeLocator(IlsSfcGatewayHook hook) {
		this.hook = hook;
	}
	
	/** Given an identifier like local, superior, prior, global, operation
	 *  return the corresponding STEP scope. The enclosing step scope is stored
	 *  in the chart scope of the level BELOW it
	 */
	@Override
	public synchronized PyChartScope locate(ScopeContext scopeContext, String identifier) {
		PyChartScope chartScope = scopeContext.getChartScope();
		String providerName = RecipeDataAccess.getTagProvider(chartScope);
		log.infof("PAH - In contructor with a IlsScopeLocator with identifier: %s", identifier);

		if( !identifier.equalsIgnoreCase(Constants.TAG)) {
			
			// I think this is always called from a transition.  The following call will get the stepScope of the previous 
			// step, which is really handy when the transition uses scope locator PRIOR
			PyChartScope stepScope = scopeContext.getStepOrPrevious();
			PyChartScope rootScope = scopeContext.getRoot();
			log.infof("PAH - creating a S88Scope locator with identifier: %s", identifier);
			return new S88Scope(hook.getContext(),chartScope,stepScope,identifier, "");
		}
		else {
			log.infof("PAH - creating a TyagChartScope locator using provider: %s!", providerName);
			return new TagChartScope(providerName, hook.getContext());	
		}
	}

}
