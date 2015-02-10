package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyList;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.step.IlsAbstractChartStep;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ExecutionQueue;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/** Handles receiving and caching SFC-related responses from clients.
 *  This was implemented in Java because I couldn't get a persistent global in Jython */
// TODO: should responses be persisted, so they survive a gateway restart??
// TODO: if messages are multicast, multiple responses could complicate things,
// or at least inflate memory if not "claimed"
public class IlsGatewayScripts {	
	private static LoggerEx logger = LogUtil.getLogger(IlsGatewayScripts.class.getName());
	private static Map<String,PyDictionary> repliesById = Collections.synchronizedMap(
		new HashMap<String,PyDictionary>());

	public static PyDictionary getResponse(String id) {
		PyDictionary reply = repliesById.get(id);
		if(reply != null) {
			repliesById.remove(id);
		}
		return reply;
	}
	
	public static void setResponse(String id, PyDictionary payload) {
		repliesById.put(id, payload);
	}
	
	public static PyList getReviewDataConfig(String stepId, boolean addAdvice) {
		return null;
		/*
		PyList result = new PyList();
	    RecipeData recipeData = RecipeDataManager.getData();
	    ReviewDataConfig dataConfig = recipeData.getReviewDataConfig(stepId);
       	System.out.println("addAdvice: " + addAdvice);
	    for(ReviewDataConfig.Row row: dataConfig.getRows()) {
	    	PyList rowConfig = new PyList();
	        result.add(rowConfig);
	        rowConfig.add(row.prompt);
 	        if(addAdvice) {
	        	rowConfig.add(row.advice);
	        }
	        RecipeScope scopeEnum = RecipeScope.valueOf(row.recipeScope);
	        Object value = null;
			try {
				value = recipeData.get(scopeEnum, stepId, row.valueKey);
			} catch (RecipeDataException e) {
				logger.error("error getting recipe value", e);
			}
	        rowConfig.add(value);
	        rowConfig.add(row.units);
	    }
	        return result;
	        */
	}
	
	/** For testing, create an instance of the given step and run the action method on it, 
	 *  returning null if OK else an error message. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String activateStep(String stepClassName, Map chartProperties, Map stepProperties) {
		try {
			final PyChartScope chartScope = new PyChartScope();
			chartScope.putAll(chartProperties);
			ChartContext chartContext = new ChartContext() {
				public void abort(Throwable arg0) {}
				public PyChartScope getChartScope() {return chartScope;}
				public ChartElement getElement(UUID arg0) {return null;}
				public List<ChartElement> getElements() {return null;}
				public ExecutionQueue getExecutionQueue() {return null;}
				public GatewayContext getGatewayContext() {return null;}
				public ScriptManager getScriptManager() {return null;}
				public void pause() {}
				public ScopeLocator getScopeLocator() {return null;}
				public boolean isRunning() {return false;}

			};
			BasicPropertySet propertySet = new BasicPropertySet();
			for(Object key: stepProperties.keySet()) {
				Object value = stepProperties.get(key);
				BasicProperty property = new BasicProperty((String)key, value.getClass());
				propertySet.set(property, value);
			}
			StepDefinition stepDefinition = new StepDefinition(UUID.randomUUID(), propertySet);
			Class stepClass = Class.forName(stepClassName);
			java.lang.reflect.Constructor ctor = null;
			for(java.lang.reflect.Constructor dctor: stepClass.getDeclaredConstructors()) {
				if(dctor.getDeclaringClass() == stepClass) {
					ctor = dctor;
				}
			}
			IlsAbstractChartStep step = (IlsAbstractChartStep)ctor.newInstance(chartContext, stepDefinition);
			step.activateStep();
		}
		catch(Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;  // all is well...		
	}

	public static Object s88Get(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier) {
		PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
		Object value = pathGet(resolvedScope, path);
		return value;
	}
	
	public static void s88Set(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		PyChartScope resolvedScope = resolveScope(chartScope, stepScope, scopeIdentifier);
		pathSet(resolvedScope, path, value);
	}
		
	public static PyChartScope resolveScope(PyChartScope chartScope, 
			PyChartScope stepScope, String scopeIdentifier) {
		if(scopeIdentifier.equals(IlsSfcNames.LOCAL)) {
			return stepScope;
		}
		else if(scopeIdentifier.equals(IlsSfcNames.PREVIOUS)) {
			return stepScope.getSubScope(ScopeContext.PREVIOUS);
		}
		else if(scopeIdentifier.equals(IlsSfcNames.SUPERIOR)) {
			return (PyChartScope) chartScope.get(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY);
		}
		else {  // search for a named scope
			while(chartScope != null) {
				if(scopeIdentifier.equals(getEnclosingStepScope(chartScope))) {
					return chartScope.getSubScope(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY);
				}
				else {  // look up the hierarchy
					chartScope = chartScope.getSubScope("parent");
				}
			}
			return null;  // couldn't find it
		}
	}

	/** Return the scope of the enclosing step. Return "global" if the chart scope
	 *  has no parent. Returns null if no level is available.
	 */
	static String getEnclosingStepScope(PyChartScope chartScope) {
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
	static String[] splitPath(String path) {
		return path.split("\\.");
	}
	
	/** Get an object from a nested dictionary given a dot-separated
	 *  path
	 */
	static Object pathGet(PyChartScope scope, String path) {
		String[] keys = splitPath(path);
		String lastKey = keys[keys.length-1];
		return getLastScope(scope, keys, path).get(lastKey);
	}
	
	/** Set an object in a nested dictionary given a dot-separated
	 *  path. All levels must already exist.
	 */
	static void pathSet(PyChartScope scope, String path, Object value) {
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
	static PyChartScope getLastScope(PyChartScope scope, String[] keys, String path) {
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
		
}
