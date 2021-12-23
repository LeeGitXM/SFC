package com.ils.sfc.gateway.recipe;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.gateway.GatewayRequestHandler;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;

import system.ils.sfc.common.Constants;

/**
 * Fundamental methods for creating and accessing tag-based recipe data from a 
 * running chart.
 */
public class RecipeDataAccess {
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataAccess.class.getName());
	private static GatewayRequestHandler requestHandler = null;

	/**
	 * @see IlsSfcGatewayHook for initialization.
	 * @param rh
	 */
	public static void setRequestHandler(GatewayRequestHandler rh) {
		requestHandler = rh;
	}

	/** Get the chart/step path for the given recipe data scope (not including provider). */
	public static String getRecipeDataTagPath(PyChartScope chartScope, PyChartScope stepScope, String scope) {
		PyChartScope resolvedStepScope = resolveStepScope(chartScope, stepScope, scope);
		PyChartScope resolvedChartScope = resolveChartScope(chartScope, scope);
		return getFullStepPath(resolvedChartScope, resolvedStepScope);
	}

	public static String getFullStepPath(PyChartScope chartScope, PyChartScope stepScope) {
		String stepName = (String)stepScope.get("name");
		String chartPath = (String) chartScope.get("chartPath");
		String stepPath = chartPath + "/" + stepName;
		return stepPath;
	}	

	public static String getFullChartPath(PyChartScope chartScope) {
		String myPath = (String)chartScope.get(Constants.CHART_PATH);
		if(chartScope.get(Constants.PARENT) != null ) {
			return getFullChartPath(chartScope.getSubScope(Constants.PARENT)) + "/" + myPath;
		}
		else {
			return myPath;
		}
	}

	/** Log an error to the chart's logger. */
	public static void logChartError(PyChartScope chartScope, String msg, Exception e) {
		String loggerName = getFullChartPath(chartScope);
		LoggerEx logger = LogUtil.getLogger(loggerName);
		if(e != null) {
			logger.error(msg, e);
		}
		else {
			logger.error(msg);
		}
	}

	public static boolean s88DataExists(PyChartScope chartScope,
			PyChartScope stepScope, String path, String scopeIdentifier) {
		try {
			String providerName = getTagProvider(chartScope);
			String tagPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier);
			Object[] args = {providerName, tagPath};
			Boolean result = (Boolean) PythonCall.RECIPE_DATA_EXISTS.exec(args);
			return result;
		} catch (JythonExecException e) {
			logChartError(chartScope, "Recipe Data existence check failed", e);
			return false;
		}					
	}
	public static Object s88Get(PyChartScope chartScope,
			PyChartScope stepScope, String s88Path, String scopeIdentifier) {


		try {
			String providerName = getTagProvider(chartScope);
			Object value = null;
			String tagPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier) +
					"/" + s88Path;
			Object[] args = {providerName, tagPath};
			value = PythonCall.GET_RECIPE_DATA.exec(args);
			return value;
		} catch (JythonExecException e) {
			logChartError(chartScope, "Recipe Data tag read failed", e);
			return null;
		}		

	}

	public static Object s88GetOld(PyChartScope chartScope,
			PyChartScope stepScope, String s88Path, String scopeIdentifier) {

		throw new IllegalStateException("This is the old version of s88Get in RecipeDataAccess");
		/*		
					try {
						String providerName = getProviderName(getIsolationMode(chartScope));
						Object value = null;
						String tagPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier) +
							"/" + s88Path;
						Object[] args = {providerName, tagPath};
						value = PythonCall.GET_RECIPE_DATA.exec(args);
						return value;
					} catch (JythonExecException e) {
						logChartError(chartScope, "Recipe Data tag read failed", e);
						return null;
					}		
		 */	
	}


	/** Get the chart scope corresponding to the given recipe data scope. */
	private static PyChartScope resolveChartScope(PyChartScope chartScope, 
			String scopeIdentifier) {
		if(scopeIdentifier.equals(Constants.LOCAL) || scopeIdentifier.equals(Constants.PRIOR)) {
			return chartScope;
		}
		else if(scopeIdentifier.equals(Constants.SUPERIOR)) {
			return (PyChartScope) chartScope.getSubScope("parent");
		}
		else {  // search for a named scope
			while(chartScope != null) {
				if(scopeIdentifier.equals(getEnclosingStepScope(chartScope))) {
					return chartScope.getSubScope("parent");
				}
				else {  // look up the hierarchy
					chartScope = chartScope.getSubScope("parent");
				}
			}
		}

		throw new IllegalArgumentException("could not resolve scope " + scopeIdentifier);		
	}

	/** Get the step scope corresponding to the given recipe data scope. */
	private static PyChartScope resolveStepScope(PyChartScope chartScope, 
			PyChartScope stepScopeOrPrior, String scopeIdentifier) {
		PyChartScope resolvedStepScope = null;
		if(scopeIdentifier.equals(Constants.LOCAL) || scopeIdentifier.equals(Constants.PRIOR)) {
			resolvedStepScope = stepScopeOrPrior;
		}
		else if(scopeIdentifier.equals(Constants.SUPERIOR)) {
			resolvedStepScope = (PyChartScope) chartScope.get(Constants.ENCLOSING_STEP_SCOPE_KEY);
		}
		else {  // search for a named scope
			while(chartScope != null) {
				if(scopeIdentifier.equals(getEnclosingStepScope(chartScope))) {
					resolvedStepScope = chartScope.getSubScope(Constants.ENCLOSING_STEP_SCOPE_KEY);
					break;
				}
				else {  // look up the hierarchy
					chartScope = chartScope.getSubScope("parent");
				}
			}
		}
		if(resolvedStepScope != null) {
			return resolvedStepScope; // resolvedStepScope.getSubScope(IlsSfcNames.RECIPE_DATA);
		}
		else {
			throw new IllegalArgumentException("could not resolve scope " + scopeIdentifier);
		}
	}

	/** Return the scope of the enclosing step. Return "global" if the chart scope
	 *  has no parent. Returns null if no particular level is available.
	 */
	private static String getEnclosingStepScope(PyChartScope chartScope) {
		if(chartScope.get(Constants.ENCLOSING_STEP_SCOPE_KEY) != null) {  // don't use containsKey !
			PyChartScope parentChartScope = chartScope.getSubScope(ScopeContext.PARENT);
			boolean parentIsRoot = parentChartScope.getSubScope(ScopeContext.PARENT) == null;
			PyChartScope enclosingStepScope = chartScope.getSubScope(Constants.ENCLOSING_STEP_SCOPE_KEY);				
			String scopeIdentifier = (String) enclosingStepScope.get(Constants.S88_LEVEL);
			if(scopeIdentifier != null && !scopeIdentifier.toString().equals(Constants.NONE)) {
				return scopeIdentifier;
			}
			else if(parentIsRoot) {
				// global steps don't need to be tagged
				return Constants.GLOBAL;
			}
			else {
				return null;
			}
		}
		return null;
	}

	public static PyChartScope getTopScope(PyChartScope scope) {
		while(scope.getSubScope(ScopeContext.PARENT) != null) {
			scope = scope.getSubScope(ScopeContext.PARENT);
		}
		return scope;
	}


	/**
	 * The scope contains the correct provider for the current isolation mode
	 * @param scope
	 * @return provider name
	 */
	public static String getTagProvider(PyChartScope scope) {
		PyChartScope topScope = getTopScope(scope);
		String providerName = topScope.get(Constants.TAG_PROVIDER).toString();
		return providerName;
	}

	public static boolean getIsolationMode(PyChartScope scope) {
		PyChartScope topScope = getTopScope(scope);
		boolean isolationMode = false;
		if( topScope.get(Constants.ISOLATION_MODE) !=null ) {
			isolationMode = ((Boolean)topScope.get(Constants.ISOLATION_MODE)).booleanValue();
		}
		else {
			logger.warnf("RecipeDataAccess.getIsolationMode: %s not defined in top scope, assuming production",Constants.ISOLATION_MODE);
		}
		return isolationMode;
	}

	/** Handle a problem with recipe data access. Returns an object appropriate for read failures,
	 *  but if the implementation throws this is irrelevant (though the compiler may like it). */
	private Object handleAccessError(String msg, String path, Exception e) {
		String fullMsg = msg + "; path: " + path + (e != null ? " exception: " + e.getMessage() : "");
		logger.error(fullMsg);
		throw new IllegalArgumentException(fullMsg);
	}


}
