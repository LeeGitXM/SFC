package com.ils.sfc.gateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyList;

import com.ils.sfc.step.IlsAbstractChartStep;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
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

	/**
	 * Find the database associated with a specified project. This requires 
	 * that a Gateway context. NOTE: There is no default defined for the global project.
	 * 
	 * @param projectId identifier for the project
	 * @return name of the default database for the specified project
	 */
	public static String getDefaultDatabaseName(long projectId)  {
		String dbName = "";
		if( projectId!=-1) {
			dbName = ilsSfcGatewayHook.getContext().getProjectManager().getProps(projectId, ProjectVersion.Published).getDefaultDatasourceName();
		}
		return dbName;
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

	public static Object s88Get(PyChartScope chartScope, PyChartScope stepScope,
		String path, String scopeIdentifier) {
		return ilsSfcGatewayHook.getScopeLocator().s88Get(chartScope, stepScope, path, scopeIdentifier);
	}
	
	public static void s88Set(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		ilsSfcGatewayHook.getScopeLocator().s88Set(chartScope, stepScope, path, scopeIdentifier, value);
	}
		
	public static void setHook(IlsSfcGatewayHook hook) {
		ilsSfcGatewayHook = hook;		
	}
		
	public static void registerSfcProject(String projectName) {
		ilsSfcGatewayHook.getChartObserver().registerSfcProject(projectName);
	}

}
