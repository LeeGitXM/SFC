package com.ils.sfc.gateway;

import org.json.JSONException;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;

/**
 * Fundamental methods for creating and accessing tag-based recipe data from a 
 * running chart.
 */
public class RecipeDataAccess {
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataAccess.class.getName());
	private static GatewayRequestHandler reqHandler = GatewayRequestHandler.getInstance();
	
	/** Get the chart/step path for the given recipe data scope (not including provider). */
	public static String getRecipeDataTagPath(PyChartScope chartScope, PyChartScope stepScope, String scope) {
		PyChartScope resolvedStepScope = resolveStepScope(chartScope, stepScope, scope);
		PyChartScope resolvedChartScope = resolveChartScope(chartScope, scope);
		String stepName = (String)resolvedStepScope.get("name");
		String chartPath = (String) resolvedChartScope.get("chartPath");
		String resolvedStepPath = chartPath + "/" + stepName;
		return resolvedStepPath;
	}	

	public static boolean s88DataExists(PyChartScope chartScope,
			PyChartScope stepScope, String path, String scopeIdentifier) {
		String providerName = getProviderName(getIsolationMode(chartScope));
		String tagPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier);
		Object[] args = {providerName, tagPath};
		try {
			Boolean result = (Boolean) PythonCall.RECIPE_DATA_EXISTS.exec(args);
			return result;
		} catch (JythonExecException e) {
			logger.error("Recipe Data existence check failed", e);
			return false;
		}					
	}

	public static void s88Set(PyChartScope chartScope, PyChartScope stepScope,
		String path, String scopeIdentifier, Object value) {
		String providerName = getProviderName(getIsolationMode(chartScope));
		String fullPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier) +
			"/" + path;
		Object[] args = {providerName, fullPath, value};
		try {
			PythonCall.SET_RECIPE_DATA.exec(args);
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag read failed", e);
		}					
	}

	public static Object s88Get(PyChartScope chartScope,
			PyChartScope stepScope, String path, String scopeIdentifier) {
		String providerName = getProviderName(getIsolationMode(chartScope));
		String fullPath = getRecipeDataTagPath(chartScope, stepScope, scopeIdentifier) +
				"/" + path;
		Object[] args = {providerName, fullPath};
		try {
			Object value = PythonCall.GET_RECIPE_DATA.exec(args);
			return value;
		} catch (JythonExecException e) {
			logger.error("Recipe Data tag read failed", e);
			return null;
		}			
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

	public static String getProviderName(boolean isolationMode) {
		String providerName = reqHandler.getProviderName(isolationMode);
		return providerName;
	}

	static boolean getIsolationMode(PyChartScope scope) {
		PyChartScope topScope = getTopScope(scope);
		boolean isolationMode = (Boolean)topScope.get(Constants.ISOLATION_MODE);
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
