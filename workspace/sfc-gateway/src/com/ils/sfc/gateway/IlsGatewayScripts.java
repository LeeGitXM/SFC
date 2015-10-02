package com.ils.sfc.gateway;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.ils.sfc.common.rowconfig.WriteOutputConfig;
import com.ils.sfc.step.IlsAbstractChartStep;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.message.MessageDispatchManager;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.opcua.types.structs.MonitoringParameters;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ExecutionQueue;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeLocator;
import com.inductiveautomation.sfc.api.elements.ChartElement;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;

import static com.ils.sfc.common.IlsSfcCommonUtils.isEmpty;

/** Java utilities exposed to Python. The python module path is: "system.ils.sfc"
 */
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
	public static String getProviderName(boolean isolationMode)  {
		return RecipeDataAccess.getProviderName(isolationMode);
	}
	
	public static boolean getIsolationMode(PyChartScope chartScope) {
		return RecipeDataAccess.getIsolationMode(chartScope);		
	}
	
	public static PyDictionary getResponse(String id) {
		return ilsSfcGatewayHook.getRequestResponseManager().getResponse(id);
	}
	
	public static void setResponse(String id, PyDictionary payload) {
		ilsSfcGatewayHook.getRequestResponseManager().setResponse(id, payload);
	}
	
	public static Dataset getReviewData(PyChartScope chartScope, PyChartScope stepScope,
		String reviewDataConfigJson, boolean addAdvice) {
		try {
			ReviewDataConfig config = ReviewDataConfig.fromJSON(reviewDataConfigJson);
			DatasetBuilder builder = new DatasetBuilder();
			if(addAdvice) {
				builder.colNames("Prompt", "Advice", "Value", "Units");
				builder.colTypes(String.class, String.class, Double.class, String.class);
			}
			else {
				builder.colNames("Prompt", "Value", "Units");
				builder.colTypes(String.class, Double.class, String.class);
			}
			Object[] buffer = addAdvice ? new Object[4] : new Object[3];
		    for(ReviewDataConfig.Row row: config.getRows()) {
		    	if(!row.isBlank()) {
			    	int i = 0;
			    	// Get any config data that has been created programmatically
			    	ReviewDataConfig.Row scriptedConfig = new ReviewDataConfig.Row();
			    	if(!isEmpty(row.configKey)) {
			    		getScriptedConfig(chartScope, stepScope, row.configKey, row.recipeScope, scriptedConfig);
			    	}
			    	String prompt = !isEmpty(scriptedConfig.prompt) ? scriptedConfig.prompt : row.prompt;
			    	buffer[i++] = prompt;
			    	if(addAdvice) {
				    	String advice = !isEmpty(scriptedConfig.advice) ? scriptedConfig.advice : row.advice;
			    		if(isEmpty(advice)) {
			    			String adviceKey = changeValueKey(row.valueKey, "advice");
			    			try {
			    				advice = (String) RecipeDataAccess.s88Get(chartScope, stepScope, adviceKey, row.recipeScope);
			    			}
			    			catch(Exception e) {
			    				logger.error("Error getting advice from recipe data", e);
			    			}
			    		}
			    		buffer[i++] = advice;
			    	}
					Object value = getValueInDisplayUnits(chartScope, stepScope, row.valueKey,
						row.recipeScope, row.units);
			    	buffer[i++] = value;
			    	String units = !isEmpty(scriptedConfig.units) ? scriptedConfig.units : row.units;
			    	buffer[i++] = units;
		    	}
		    	builder.addRow(buffer);
		    }
			return builder.build();
		}
		catch(Exception e) {
			logger.error("Error building review data", e);
			return null;
		}	
	}

	public static Dataset getReviewFlows(PyChartScope chartScope, PyChartScope stepScope,
			String reviewFlowsConfigJson) {
			try {
				ReviewFlowsConfig config = ReviewFlowsConfig.fromJSON(reviewFlowsConfigJson);
				DatasetBuilder builder = new DatasetBuilder();

				builder.colNames("prompt", "advice", "flow1", "flow2", "flow3", "units", "sumFlows");
				builder.colTypes(String.class, String.class, Double.class, Double.class, Double.class, String.class, Boolean.class);
				Object[] buffer = new Object[7];
			    for(ReviewFlowsConfig.Row row: config.getRows()) {
			    	if(!row.isBlank()) {
				    	int i = 0;
				    	// Get any config data that has been created programmatically
				    	ReviewFlowsConfig.Row scriptedConfig = new ReviewFlowsConfig.Row();
				    	if(!isEmpty(row.configKey)) {
				    		getScriptedConfig(chartScope, stepScope, row.configKey, row.destination, scriptedConfig);
				    	}
				    	// Prompt
				    	String prompt = !isEmpty(scriptedConfig.prompt) ? scriptedConfig.prompt : row.prompt;
				    	buffer[i++] = prompt;
				    	// Advice
				    	String advice = !isEmpty(scriptedConfig.advice) ? scriptedConfig.advice : row.advice;
			    		if(isEmpty(advice)) {
			    			// TODO: should we check the other flow keys?
			    			String adviceKey = changeValueKey(row.flow1Key, "advice");
			    			try {
			    				advice = (String) RecipeDataAccess.s88Get(chartScope, stepScope, adviceKey, row.destination);
			    			}
			    			catch(Exception e) {
			    				logger.error("Error getting advice from recipe data", e);
			    			}
			    		}
			    		buffer[i++] = advice;
			    		// flow1
						double flow1 = getValueInDisplayUnits(chartScope, stepScope, 
							row.flow1Key, row.destination, row.units);
				    	buffer[i++] = flow1;
			    		// flow2
				    	double flow2 = getValueInDisplayUnits(chartScope, stepScope, 
							row.flow2Key, row.destination, row.units);
				    	buffer[i++] = flow2;
				    	// Total flow
				    	double totalFlow = 0;
				    	boolean sumFlows = row.flow3Key.toLowerCase().equals("sum");
				    	if(sumFlows) {
				    		totalFlow = flow1 + flow2;
				    	}
				    	else {
							totalFlow = getValueInDisplayUnits(chartScope, stepScope, 
								row.flow3Key, row.destination, row.units);
				    	}
				    	buffer[i++] = totalFlow;
				    	// Units
				    	String units = !isEmpty(scriptedConfig.units) ? scriptedConfig.units : row.units;
				    	buffer[i++] = units;
				    	// Sum Flows (hidden)
				    	buffer[i++] = sumFlows;
			    	}
			    	builder.addRow(buffer);
			    }
				return builder.build();
			}
			catch(Exception e) {
				logger.error("Error building review data", e);
				return null;
			}	
		}
	
	/** Change a recipe data key to get a "sibling" value, e.g. "advice" instead of "value" */
	private static String changeValueKey(String path, String newKey) {
		int lastDotIndex = path.lastIndexOf(".");
		if(lastDotIndex >= 0) {
			return path.substring(0, lastDotIndex + 1) + newKey;
		}
		else {
			logger.error("Could not replace key in recipe data path: " + path);
			return path;
		}
	}
	
	/** Get Review Data config info from recipe data. Tolerates nonexistent keys. */
	private static void getScriptedConfig(PyChartScope chartScope, PyChartScope stepScope, 
		String configKey, String configScope, ReviewDataConfig.Row scriptedConfig) {
		String adviceKey = configKey + ".advice";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, adviceKey, configScope)) {
			scriptedConfig.advice = (String)RecipeDataAccess.s88Get(chartScope, stepScope, adviceKey, configScope);
		}
		String unitsKey = configKey + ".units";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, unitsKey, configScope)) {
			scriptedConfig.units = (String)RecipeDataAccess.s88Get(chartScope, stepScope, unitsKey, configScope);		
		}
		String promptKey = configKey + ".label";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, promptKey, configScope)) {
			scriptedConfig.prompt = (String)RecipeDataAccess.s88Get(chartScope, stepScope, promptKey, configScope);	
		}
	}

	/** Get Review Flows config info from recipe data. Tolerates nonexistent keys. */
	private static void getScriptedConfig(PyChartScope chartScope, PyChartScope stepScope, 
		String configKey, String configScope, ReviewFlowsConfig.Row scriptedConfig) {
		String adviceKey = configKey + ".advice";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, adviceKey, configScope)) {
			scriptedConfig.advice = (String)RecipeDataAccess.s88Get(chartScope, stepScope, adviceKey, configScope);
		}
		String unitsKey = configKey + ".units";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, unitsKey, configScope)) {
			scriptedConfig.units = (String)RecipeDataAccess.s88Get(chartScope, stepScope, unitsKey, configScope);		
		}
		String promptKey = configKey + ".label";
		if(RecipeDataAccess.s88DataExists(chartScope, stepScope, promptKey, configScope)) {
			scriptedConfig.prompt = (String)RecipeDataAccess.s88Get(chartScope, stepScope, promptKey, configScope);	
		}
	}

	/** Convert the recipe data value to the display units given in the Review Data config. */
	private static double getValueInDisplayUnits(PyChartScope chartScope, PyChartScope stepScope, 
		String valueKey, String recipeScope, String toUnits) {
		Object oVal = RecipeDataAccess.s88Get(chartScope, stepScope, valueKey, recipeScope);
		double doubleVal = 0.;
		if(oVal instanceof Number) {
			doubleVal = ((Number)oVal).doubleValue();
		}
		// else error!
		String unitsKey = changeValueKey(valueKey,  "units");
		String fromUnits = (String)RecipeDataAccess.s88Get(chartScope, stepScope, unitsKey, recipeScope);
		if(IlsSfcCommonUtils.isEmpty(fromUnits) || IlsSfcCommonUtils.isEmpty(toUnits)) {
			throw new IllegalArgumentException("null units in display conversion " + fromUnits + "->" + toUnits);
		}
		Object[] params = {fromUnits, toUnits, Double.valueOf(doubleVal)};
		Double displayValue;
		try {
			displayValue = (Double)PythonCall.CONVERT_UNITS.exec(params);
		} catch (JythonExecException e) {
			e.printStackTrace();
			return doubleVal;
		}
		return displayValue;
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
				@Override
				public ChartDefinition getChartDefinition() {
					// TODO Auto-generated method stub
					return null;
				}
				@Override
				public UUID getInstanceId() {
					// TODO Auto-generated method stub
					return null;
				}

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

	
	public static void setHook(IlsSfcGatewayHook hook) {
		ilsSfcGatewayHook = hook;		
	}
	
	/**
	 * Get the clock rate factor. For non-isolation mode the value is fixed at 1.0. 
	 * @param isIsolated. True if the system is currently in ISOLATION mode.
	 * @return the amount to speed up or slow down the clock. Values larger than
	 *         1.0 imply that the system is to run faster.
	 */
	public static double getTimeFactor(boolean isIsolation) {
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
	
	/**
	 * Clear results from the step monitor. Since results are indexed
	 * by the run-id of the chart, the dictionary entries related to
	 * completed charts are not cleaned up without resorting to this
	 * call.
	 */
	public static void clearStepMonitor() {
		ilsSfcGatewayHook.getStepMonitor().clear();
	}

	/**
	 * @return the most recent state of the named block of a running chart.
	 */
	public static String stepState(String chartId,String stepName) {
		return ilsSfcGatewayHook.getStepMonitor().stepState(chartId,stepName);
	}
	/**
	 * On a stop, the step monitor removes itself as a chart observer,
	 * but retains the current state dictionary. Note that this is a 
	 * global operation affecting the monitoring of all running charts.
	 */
	public static void stopStepMonitor() {
		ilsSfcGatewayHook.getStepMonitor().stop();
	}
	/**
	 * If the step monitor is not currently observing step status,
	 * it will add itself as a ChartObserver. 
	 */
	public static void startStepMonitor() {
		ilsSfcGatewayHook.getStepMonitor().start();
	}
	/**
	 * Tell the step monitor to collect observations for a 
	 * particular chart and its steps. This call sets the
	 * mapping between chart name and chart execution id. 
	 */
	public static void watchChart(String id,String name) {
		ilsSfcGatewayHook.getStepMonitor().watch(id,name);
	}
		
	public static void registerSfcProject(String projectName) {
		ilsSfcGatewayHook.getChartObserver().registerSfcProject(projectName);
	}

	public static String getJSONForScope(PyChartScope scope) throws JSONException {
		JSONObject jsonObject = Data.fromStepScope(scope);
		return jsonObject.toString();
	}

	public static MonitorDownloadsConfig getMonitorDownloadsConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return MonitorDownloadsConfig.fromJSON(json);
	}

	public static PVMonitorConfig getPVMonitorConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return PVMonitorConfig.fromJSON(json);
	}
	
	public static WriteOutputConfig getWriteOutputConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return WriteOutputConfig.fromJSON(json);
	}

	public static ManualDataEntryConfig getManualDataEntryConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return ManualDataEntryConfig.fromJSON(json);
	}

	public static ReviewDataConfig getReviewDataConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return ReviewDataConfig.fromJSON(json);
	}

	public static ReviewFlowsConfig getReviewFlowsConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return ReviewFlowsConfig.fromJSON(json);
	}
	
	public static void dropboxPut(String chartRunId, String objectId, Object object) {
		ilsSfcGatewayHook.getDropBox().put(chartRunId, objectId, object);
	}
	
	public static Object dropboxGet(String chartRunId, String objectId) {
		return ilsSfcGatewayHook.getDropBox().get(chartRunId, objectId);
	}
	
	public static String getRecipeDataTagPath(PyChartScope chartScope, PyChartScope stepScope, String scope) {
		return RecipeDataAccess. getRecipeDataTagPath(chartScope, stepScope, scope);
	}
	
	public static Object parseValue(String strValue) {
		return IlsProperty.parseObjectValue(strValue, null);
	}

	public static String getTestName(PyChartScope chartScope) {
		PyChartScope topScope = IlsSfcCommonUtils.getTopScope(chartScope);
		return (String)topScope.get("chartPath");
	}
	
	public static void initializeTests(String reportFilePath) {
		ilsSfcGatewayHook.getTestMgr().initialize();
		ilsSfcGatewayHook.getTestMgr().setReportFilePath(reportFilePath);
	}

	public static void startTest(String testName) {
		ilsSfcGatewayHook.getTestMgr().startTest(testName);
	}

	public static void assertEqual(PyChartScope chartScope, PyChartScope stepScope, 
		PyObject expected, PyObject actual) {		
		String testName = getTestName(chartScope);
		String stepPath = RecipeDataAccess.getFullStepPath(chartScope, stepScope);
		ilsSfcGatewayHook.getTestMgr().assertEqual(testName, stepPath, expected, actual );
	}

	public static void assertTrue(PyChartScope chartScope, PyChartScope stepScope, 
		boolean condition, String msg) {
		String testName = getTestName(chartScope);
		String stepPath = RecipeDataAccess.getFullStepPath(chartScope, stepScope);
		ilsSfcGatewayHook.getTestMgr().assertTrue(testName, stepPath, condition,  msg);
	}
	
	public static void failTest(PyChartScope chartScope, String message) {
		String testName = getTestName(chartScope);
		ilsSfcGatewayHook.getTestMgr().fail(testName, message);
	}
	
	public static void passTest(PyChartScope chartScope) {
		String testName = getTestName(chartScope);
		ilsSfcGatewayHook.getTestMgr().pass(testName);
	}
	
	public static void reportTests() {
		ilsSfcGatewayHook.getTestMgr().report();
	}
}
