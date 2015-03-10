package com.ils.sfc.gateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyList;

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
import com.inductiveautomation.sfc.api.ScopeLocator;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/** java utilities exposed to Python. */
public class IlsGatewayScripts {	
	private static LoggerEx logger = LogUtil.getLogger(IlsGatewayScripts.class.getName());
	private static IlsSfcGatewayHook ilsSfcGatewayHook;
	private static GatewayRequestHandler requestHandler = GatewayRequestHandler.getInstance();

	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public static String getDatabaseName(boolean isIsolation)  {
		String dbName = requestHandler.getDatabaseName(isIsolation);
		return dbName;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public static String getProviderName(boolean isIsolation)  {
		String providerName = requestHandler.getProviderName(isIsolation);
		return providerName;
	}
	
	public static PyDictionary getResponse(String id) {
		return ilsSfcGatewayHook.getRequestResponseManager().getResponse(id);
	}
	
	public static void setResponse(String id, PyDictionary payload) {
		ilsSfcGatewayHook.getRequestResponseManager().setResponse(id, payload);
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

	public static Object s88BasicGet(PyChartScope chartScope, PyChartScope stepScope,
		String path, String scopeIdentifier) {
		return ilsSfcGatewayHook.getScopeLocator().s88Get(chartScope, stepScope, path, scopeIdentifier);
	}
	
	public static void s88BasicSet(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		ilsSfcGatewayHook.getScopeLocator().s88Set(chartScope, stepScope, path, scopeIdentifier, value);
	}
		
	public static void setHook(IlsSfcGatewayHook hook) {
		ilsSfcGatewayHook = hook;		
	}
	/**
	 * Get the clock rate factor. For non-isolation mode the value is fixed at 1.0. 
	 * @param isIsolated. True if the system is currently in ISOLATION mode.
	 * @return the amount to speed up or slow down the clock. Values larger than
	 *         1.0 imply that the system is to run faster.
	 */
	public static double setTimeFactor(boolean isIsolation) {
		return requestHandler.getTimeFactor(isIsolation);
	}
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down the clock.
	 */
	public static void setTimeFactor(double factor) {
		requestHandler.setTimeFactor(factor);
	}
		
	public static void registerSfcProject(String projectName) {
		ilsSfcGatewayHook.getChartObserver().registerSfcProject(projectName);
	}

}
