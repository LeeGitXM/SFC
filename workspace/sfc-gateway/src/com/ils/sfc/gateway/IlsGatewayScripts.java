package com.ils.sfc.gateway;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.python.core.PyDictionary;
import org.python.core.PyObject;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig.Row;
import com.ils.sfc.step.IlsAbstractChartStep;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ExecutionQueue;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeLocator;
import com.inductiveautomation.sfc.api.elements.ChartElement;
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
			    	Row scriptedConfig = new Row();
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
			    				advice = (String) s88BasicGet(chartScope, stepScope, adviceKey, row.recipeScope);
			    			}
			    			catch(Exception e) {
			    				logger.error("Error getting advice from recipe data", e);
			    			}
			    		}
			    		buffer[i++] = advice;
			    	}
					Object value = getValueInDisplayUnits(chartScope, stepScope, row);
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
	private static void getScriptedConfig(PyChartScope chartScope, PyChartScope stepScope, String configKey, String configScope, Row scriptedConfig) {
		String adviceKey = configKey + ".advice";
		if(s88DataExists(chartScope, stepScope, adviceKey, configScope)) {
			scriptedConfig.advice = (String)s88BasicGet(chartScope, stepScope, adviceKey, configScope);
		}
		String unitsKey = configKey + ".units";
		if(s88DataExists(chartScope, stepScope, unitsKey, configScope)) {
			scriptedConfig.units = (String)s88BasicGet(chartScope, stepScope, unitsKey, configScope);		
		}
		String promptKey = configKey + ".label";
		if(s88DataExists(chartScope, stepScope, promptKey, configScope)) {
			scriptedConfig.prompt = (String)s88BasicGet(chartScope, stepScope, promptKey, configScope);	
		}
	}

	/** Convert the recipe data value to the display units given in the Review Data config. */
	private static double getValueInDisplayUnits(PyChartScope chartScope, PyChartScope stepScope, Row row) {
		Object oVal = s88BasicGet(chartScope, stepScope, row.valueKey, row.recipeScope);
		double doubleVal = 0.;
		if(oVal instanceof Number) {
			doubleVal = ((Number)oVal).doubleValue();
		}
		// else error!
		String unitsKey = changeValueKey(row.valueKey,  "units");
		String fromUnits = (String)s88BasicGet(chartScope, stepScope, unitsKey, row.recipeScope);
		String toUnits = row.units;
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

	/** Check if a particular piece of recipe data exists. */
	public static boolean s88DataExists(PyChartScope chartScope, PyChartScope stepScope,
		String path, String scopeIdentifier) {
		try {
			ilsSfcGatewayHook.getScopeLocator().s88Get(chartScope, stepScope, path, scopeIdentifier);
			return true;
		}
		catch(IllegalArgumentException e) {
			return false;
		}
	}

	public static Object s88BasicGet(PyChartScope chartScope, PyChartScope stepScope,
		String path, String scopeIdentifier) {
		return ilsSfcGatewayHook.getScopeLocator().s88Get(chartScope, stepScope, path, scopeIdentifier);
	}
	
	public static PyChartScope s88GetScope(PyChartScope chartScope, PyChartScope stepScope, String scopeIdentifier) {
		return ilsSfcGatewayHook.getScopeLocator().resolveScope(chartScope, stepScope, scopeIdentifier);
	}

	public static void s88ScopeChanged(PyChartScope chartScope, PyChartScope stepScope) {
		ilsSfcGatewayHook.getScopeLocator().s88ScopeChanged(chartScope, stepScope);
	}

	public static void s88BasicSet(PyChartScope chartScope, PyChartScope stepScope, 
		String path, String scopeIdentifier, Object value) {
		ilsSfcGatewayHook.getScopeLocator().s88Set(chartScope, stepScope, path, scopeIdentifier, value);
	}

	public static String getRecipeDataText(PyChartScope chartScope, PyChartScope stepScope,
		String scopeIdentifier) {
		try {
			return ilsSfcGatewayHook.getScopeLocator().getRecipeDataText(chartScope, stepScope, scopeIdentifier);
		} catch (JSONException e) {
			logger.error("Error getting recipe data text", e);
			return "";
		}	
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

	public static void initializeTests(String reportFilePath) {
		ilsSfcGatewayHook.getTestMgr().initialize();
		ilsSfcGatewayHook.getTestMgr().setReportFilePath(reportFilePath);
	}

	public static void startTest(String testName) {
		ilsSfcGatewayHook.getTestMgr().startTest(testName);
	}

	public static String getTestName(PyChartScope chartScope) {
		PyChartScope topScope = IlsSfcCommonUtils.getTopScope(chartScope);
		return (String)topScope.get("chartPath");
	}

	public static String getFullStepName(PyChartScope chartScope, PyChartScope stepScope) {
		String stepName = (String)stepScope.get("name");
		String chartName = (String)chartScope.get("chartPath");
		return chartName + ":" + stepName;
	}
	
	public static String getJSONForScope(PyChartScope scope) throws JSONException {
		JSONObject jsonObject = Data.fromStepScope(scope);
		return jsonObject.toString();
	}
	
	public static void assertEqual(String testName, String stepName, PyObject expected, PyObject actual) {		
		ilsSfcGatewayHook.getTestMgr().assertEqual(testName, stepName, expected, actual );
	}

	public static void assertTrue(String testName, String stepName, boolean condition, String msg) {
		ilsSfcGatewayHook.getTestMgr().assertTrue(testName, stepName, condition,  msg);
	}
	
	public static void failTest(String testName, String message) {
		ilsSfcGatewayHook.getTestMgr().fail(testName, message);
	}
	
	public static void passTest(String testName) {
		ilsSfcGatewayHook.getTestMgr().pass(testName);
	}
	
	public static void reportTests() {
		ilsSfcGatewayHook.getTestMgr().report();
	}
}
