package com.ils.sfc.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.common.sqltags.model.Tag;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.parser.TagPathParser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.sqltags.SQLTagsManager;
import com.inductiveautomation.ignition.gateway.sqltags.model.BasicWriteRequest;
import com.inductiveautomation.ignition.gateway.sqltags.model.WriteRequest;
import com.inductiveautomation.opcua.types.structs.WriteValue;
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
		PyChartScope stepScope = scopeContext.getStepScope();
		PyChartScope chartScope = scopeContext.getChartScope();
		return resolveScope(chartScope, stepScope, identifier.trim());
	}

	/** @see #locate(ScopeContext, String) 
	 * An alternate entry point that's more convenient for ILS code. */
	public synchronized PyChartScope resolveScope(PyChartScope chartScope, 
			PyChartScope stepScope, String scopeIdentifier) {
		PyChartScope resolvedStepScope = null;
		if(scopeIdentifier.equals(Constants.LOCAL)) {
			resolvedStepScope = stepScope;
		}
		else if(scopeIdentifier.equals(Constants.PREVIOUS)) {
			resolvedStepScope = stepScope.getSubScope(ScopeContext.PREVIOUS);
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
	private String getEnclosingStepScope(PyChartScope chartScope) {
		if(chartScope.get(Constants.ENCLOSING_STEP_SCOPE_KEY) != null) {  // don't use containsKey !
			PyChartScope parentChartScope = chartScope.getSubScope(ScopeContext.PARENT);
			boolean parentIsRoot = parentChartScope.getSubScope(ScopeContext.PARENT) == null;
			PyChartScope enclosingStepScope = chartScope.getSubScope(Constants.ENCLOSING_STEP_SCOPE_KEY);				
			String scopeIdentifier = (String) enclosingStepScope.get(Constants.S88_LEVEL_KEY);
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
	
	/** Split a dot-separated path */
	String[] splitPath(String path) {
		return path.split("\\.");
	}
	
	/** Get an object from a nested dictionary given a dot-separated
	 *  path
	 */
	private Object pathGet(PyChartScope scope, String path) {
		String[] keys = splitPath(path);
		String lastKey = keys[keys.length-1];
		Object value = getLastScope(scope, keys, path).get(lastKey);
		// work around the extremely annoying inability of JSONObject to handle null values:
		if(value == JSONObject.NULL) {
			value = null;
		}
		return value;
	}
	
	/** Set an object in a nested dictionary given a dot-separated
	 *  path. All levels must already exist.
	 */
	private void pathSet(PyChartScope scope, String path, Object value) {
		String[] keys = splitPath(path);
		String lastKey = keys[keys.length-1];
		
		PyChartScope lastScope = getLastScope(scope, keys, path);
		if(!lastScope.hasKey(lastKey)) {
			throw new IllegalArgumentException("recipe data not found: " + path);
		}
		// don't allow a primitive value to be set over an object value
		// e.g. if someone forgot to add ".value"
		if(lastScope.get(lastKey) instanceof PyChartScope) {
			throw new IllegalArgumentException("recipe data not found: " + path + "--did you forget to add \".value\"?");			
		}
		// work around the extremely annoying inability of JSONObject to handle null values:
		if(value == null) {
			value = JSONObject.NULL;
		}
		lastScope.put(lastKey, value);
	}
	
	
	/** Get the penultimate object in the reference string, which should be
	 *  a Map. May throw NPE or cast exception with bad data
	 */
	private PyChartScope getLastScope(PyChartScope scope, String[] keys, String path) {
	   for(int i = 0; i < keys.length - 1; i++) {
		   scope = scope.getSubScope(keys[i]);
		   if(scope == null || !(scope instanceof PyChartScope)) {
				throw new IllegalArgumentException("recipe data not found: " + path);			   
		   }
	   }		
	   String lastKey = keys[keys.length-1];
	   if(!scope.containsKey(lastKey)) {
			throw new IllegalArgumentException("recipe data not found: " + path);			   		   
	   }
	   return scope;
	}
	
	public static PyChartScope getTopScope(PyChartScope scope) {
		while(scope.getSubScope(ScopeContext.PARENT) != null) {
			scope = scope.getSubScope(ScopeContext.PARENT);
		}
		return scope;
	}

	/** Get a text representation of all the recipe data for a particular scope. 
	 * @throws JSONException */
	public synchronized String getRecipeDataText(PyChartScope chartScope, PyChartScope stepScope, 
		String scopeIdentifier) throws JSONException {
			PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
			JSONObject jsonObject;
			jsonObject = Data.fromStepScope(resolvedScope);
			return jsonObject.toString();
		}

	public synchronized Object s88Get(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier) {
		if(IlsSfcCommonUtils.isEmpty(path)) return null;
		Object value = null;
		if(Constants.TAG.equals(scopeIdentifier)) {
			TagPath tagPath;
			try {
				tagPath = TagPathParser.parse(path);
			} catch (IOException e) {
				logger.error("Couldn't parse tag path for s88Get", e);
				return null;
			};
			Tag tag = hook.getContext().getTagManager().getTag(tagPath);
			if(tag != null) {
			value = tag.getValue().getValue();
			}
			else {
				logger.error("no tag for path " + tagPath);
			}
		}
		else {
			PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
			value = pathGet(resolvedScope, path);
			//System.out.println("s88Get " + path + " = " + value + " scope " + chartScope.get("instanceId"));
		}
		return value;
	}
	
	/** Set the given step scope as changed. */
	public synchronized void s88ScopeChanged(PyChartScope chartScope, PyChartScope stepScope) {
		String chartRunId = (String)getTopScope(chartScope).get("instanceId");
		hook.getRecipeDataChangeMgr().addChangedScope(stepScope, chartRunId);		
	}
	
	public synchronized void s88Set(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		if(Constants.TAG.equals(scopeIdentifier)) {
			TagPath tagPath = null;
			try {
				tagPath = TagPathParser.parse(path);
			} catch (IOException e) {
				logger.error("Couldn't parse tag path for s88Get", e);
			};
			SQLTagsManager tagManager = hook.getContext().getTagManager();
			BasicWriteRequest<TagPath> writeRequest = new BasicWriteRequest<TagPath>(tagPath, value);
			List<WriteRequest<TagPath>> writeRequests = new ArrayList<WriteRequest<TagPath>>();
			writeRequests.add(writeRequest);
			tagManager.write(writeRequests, null, true);
		}
		else {
			PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
			pathSet(resolvedScope, path, value);
			//System.out.println("s88Set " + path + " = " + value + " scope " + chartScope.get("instanceId"));
			s88ScopeChanged(chartScope, resolvedScope);
		}
	}
}
