package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;
import org.python.core.PyList;

import com.ils.sfc.common.recipe.RecipeData;
import com.ils.sfc.common.recipe.RecipeDataException;
import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.common.recipe.RecipeScope;
import com.ils.sfc.common.recipe.ReviewDataConfig;
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
	//private static ChartManager chartManager = ChartManager.get();
	private static String sfcProjectName = null;
	private static String sfcDatabaseName = null;
	
	public static void setSfcProjectInfo(String project, String database) {
		sfcProjectName = project;
		sfcDatabaseName = database;
	}
	
	public static String getSfcProjectName() {
		return sfcProjectName;
	}

	public static String getSfcDatabaseName() {
		return sfcDatabaseName;
	}
/*
	public static String getChartState(UUID uuid) {
		Optional<ChartStatus> opt = chartManager.getChartStatus(uuid, false);
		if(opt.get() != null) {
			ChartStatus chartStatus = opt.get();
			ChartStateEnum chartState = chartStatus.getChartState();
			return chartState.toString();
		}
		else {
			return null;
		}
	}
	
	public static List<ChartInfo> getRunningCharts() {
		return chartManager.getRunningCharts();
	}
*/	
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

	/** Get a working copy of the recipe data that can be modified. For instance, a SFC 
	 *  chart gets its own working copy of the static data that it can then modify.
	 */
	public static RecipeData getWorkingRecipeData() throws RecipeDataException {
		RecipeData recipeData = RecipeDataManager.getData();
		return recipeData.copy();
	}

	public static Object getRecipeData(RecipeData workingRecipeData, String scopeString, String stepId, String path) throws RecipeDataException {
		RecipeScope scope = RecipeScope.valueOf(scopeString);
		return workingRecipeData.get(scope,  stepId, path);
	}

	public static void setRecipeData(RecipeData workingRecipeData, String scopeString, String stepId, String path, Object value, boolean create) throws RecipeDataException {
		RecipeScope scope = RecipeScope.valueOf(scopeString);
		workingRecipeData.set(scope,  stepId, path, value, create);
	}
	
	public static PyList getReviewDataConfig(String stepId) {
		PyList result = new PyList();
	    RecipeData recipeData = RecipeDataManager.getData();
	    ReviewDataConfig dataConfig = recipeData.getReviewDataConfig(stepId);
	    for(ReviewDataConfig.Row row: dataConfig.getRows()) {
	    	PyList rowConfig = new PyList();
	        result.add(rowConfig);
	        rowConfig.add(row.prompt);
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
}
