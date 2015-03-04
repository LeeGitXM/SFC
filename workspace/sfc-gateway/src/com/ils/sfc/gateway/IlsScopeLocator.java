package com.ils.sfc.gateway;

import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;

public class IlsScopeLocator implements ScopeLocator {
	
	/** Given an identifier like local, superior, previous, global, operation
	 *  return the corresponding STEP scope. The enclosing step scope is stored
	 *  in the chart scope of the level BELOW it
	 */
	@Override
	public PyChartScope locate(ScopeContext scopeContext, String identifier) {
		// check this step first, then walk up the hierarchy:
		PyChartScope stepScope = scopeContext.getStepScope();
		PyChartScope chartScope = scopeContext.getChartScope();
		return resolveScope(chartScope, stepScope, identifier);
	}

	/** @see #locate(ScopeContext, String) 
	 * An alternate entry point that's more convenient for ILS code. */
	public PyChartScope resolveScope(PyChartScope chartScope, 
			PyChartScope stepScope, String scopeIdentifier) {
		PyChartScope resolvedScope = null;
		if(scopeIdentifier.equals(IlsSfcNames.LOCAL)) {
			resolvedScope = stepScope;
		}
		else if(scopeIdentifier.equals(IlsSfcNames.PREVIOUS)) {
			resolvedScope = stepScope.getSubScope(ScopeContext.PREVIOUS);
		}
		else if(scopeIdentifier.equals(IlsSfcNames.SUPERIOR)) {
			resolvedScope = (PyChartScope) chartScope.get(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY);
		}
		else {  // search for a named scope
			while(chartScope != null) {
				if(scopeIdentifier.equals(getEnclosingStepScope(chartScope))) {
					resolvedScope = chartScope.getSubScope(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY);
					break;
				}
				else {  // look up the hierarchy
					chartScope = chartScope.getSubScope("parent");
				}
			}
		}
		if(resolvedScope != null) {
			resolvedScope = resolvedScope.getSubScope(IlsSfcNames.RECIPE_DATA);
		}
		return resolvedScope;
	}

	/** Return the scope of the enclosing step. Return "global" if the chart scope
	 *  has no parent. Returns null if no level is available.
	 */
	String getEnclosingStepScope(PyChartScope chartScope) {
		if(chartScope.get(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY) != null) {  // don't use containsKey !
			PyChartScope parentChartScope = chartScope.getSubScope(ScopeContext.PARENT);
			boolean parentIsRoot = parentChartScope.getSubScope(ScopeContext.PARENT) == null;
			PyChartScope enclosingStepScope = chartScope.getSubScope(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY);				
			String scopeIdentifier = (String) enclosingStepScope.get(IlsSfcNames.S88_LEVEL_KEY);
			if(scopeIdentifier != null && !scopeIdentifier.toString().equals(IlsSfcNames.NONE)) {
				return scopeIdentifier;
			}
			else if(parentIsRoot) {
				// global steps don't need to be tagged
				return IlsSfcNames.GLOBAL;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	/** Split a dot-separated path */
	String[] splitPath(String path) {
		return path.split("\\.");
	}
	
	/** Get an object from a nested dictionary given a dot-separated
	 *  path
	 */
	Object pathGet(PyChartScope scope, String path) {
		String[] keys = splitPath(path);
		String lastKey = keys[keys.length-1];
		return getLastScope(scope, keys, path).get(lastKey);
	}
	
	/** Set an object in a nested dictionary given a dot-separated
	 *  path. All levels must already exist.
	 */
	void pathSet(PyChartScope scope, String path, Object value) {
		String[] keys = splitPath(path);
		String lastKey = keys[keys.length-1];
		try {
			getLastScope(scope, keys, path).put(lastKey, value);
		}
		catch(Exception e) {
			throw new IllegalArgumentException("no data at path " + path);
		}
	}
	
	
	/** Get the penultimate object in the reference string, which should be
	 *  a Map. May throw NPE or cast exception with bad data
	 */
	PyChartScope getLastScope(PyChartScope scope, String[] keys, String path) {
	   for(int i = 0; i < keys.length - 1; i++) {
		   scope = scope.getSubScope(keys[i]);
		   if(scope == null || !(scope instanceof PyChartScope)) {
				throw new IllegalArgumentException("illegal path " + path);			   
		   }
	   }		
	   String lastKey = keys[keys.length-1];
	   if(!scope.containsKey(lastKey)) {
			throw new IllegalArgumentException("illegal path " + path);			   		   
	   }
	   return scope;
	}

	public Object s88Get(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier) {
		PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
		Object value = pathGet(resolvedScope, path);
		return value;
	}
	
	public void s88Set(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
		pathSet(resolvedScope, path, value);
	}
}
