package com.ils.sfc.gateway;

import static com.ils.sfc.common.IlsSfcCommonUtils.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.ils.sfc.common.rowconfig.ConfirmControllersConfig;
import com.ils.sfc.common.rowconfig.ManualDataEntryConfig;
import com.ils.sfc.common.rowconfig.MonitorDownloadsConfig;
import com.ils.sfc.common.rowconfig.PVMonitorConfig;
import com.ils.sfc.common.rowconfig.ReviewDataConfig;
import com.ils.sfc.common.rowconfig.ReviewFlowsConfig;
import com.ils.sfc.common.rowconfig.WriteOutputConfig;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.DatasetBuilder;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.PyChartScope;

/** Java utilities exposed to Python. The python module path is: "system.ils.sfc"
 */
public class IlsGatewayScripts {	
	private static LoggerEx logger = LogUtil.getLogger(IlsGatewayScripts.class.getName());
	private static IlsSfcGatewayHook ilsSfcGatewayHook;
	private static GatewayRequestHandler requestHandler = null;

	/**
	 * We need the request handler before wee can do anything
	 */
	public static void setRequestHandler(GatewayRequestHandler rh) {
		requestHandler = rh;
	}
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public static String getProjectDatabaseName(String projectName,boolean isIsolation)  {
		String dbName = requestHandler.getProjectDatabaseName(projectName,isIsolation);
		return dbName;
	}
	
	/**
	 * Find the full path to user-lib under the current running Ignition instance.
	 * 
	 * @return the path to the user-lib directory.
	 */
	public static String getUserLibPath()  {
		String userLibPath = requestHandler.getUserLibPath();
		return userLibPath;
	}
	
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public static String getProjectProviderName(String projectName,boolean isolationMode)  {
		return requestHandler.getProjectProviderName(projectName,isolationMode);
	}
	
	public static boolean getIsolationMode(PyChartScope chartScope) {
		return IlsSfcCommonUtils.getIsolationMode(chartScope);		
	}
	
	public static PyDictionary getResponse(String id) {
		return ilsSfcGatewayHook.getRequestResponseManager().getResponse(id);
	}

	public static void addRequestId(String requestId, String stepId) {
		ilsSfcGatewayHook.getRequestResponseManager().addRequestId(requestId, stepId);
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
	public static double getProjectTimeFactor(String projectName,boolean isIsolation) {
		return requestHandler.getProjectTimeFactor(projectName,isIsolation);
	}
	
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount (for values over 1) to speed up the clock
	 */
	public static void setProjectTimeFactor(String projectName,double factor) {
		requestHandler.setProjectTimeFactor(projectName,factor);
	}


	// =====================================  =======================
	
	public static MonitorDownloadsConfig getMonitorDownloadsConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return MonitorDownloadsConfig.fromJSON(json);
	}

	public static ConfirmControllersConfig getConfirmControllersConfig(String json) throws JsonParseException, JsonMappingException, IOException {
		return ConfirmControllersConfig.fromJSON(json);
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
		

	public static Object parseValue(String strValue) {
		return IlsProperty.parseObjectValue(strValue, null);
	}

	public static String getTopChartPath(PyChartScope chartScope) {
		PyChartScope topScope = IlsSfcCommonUtils.getTopScope(chartScope);
		return (String)topScope.get("chartPath");
	}
		
	public static void initializeTests(String reportFilePath) {
		ilsSfcGatewayHook.getTestMgr().initialize();
		ilsSfcGatewayHook.getTestMgr().setReportFilePath(reportFilePath);
	}

	public static void startTest(PyChartScope chartScope) {
		String topChartPath = getTopChartPath(chartScope);
		ilsSfcGatewayHook.getTestMgr().startTest(topChartPath);
	}

	public static void assertEqual(PyChartScope chartScope, PyObject expected, PyObject actual) {		
		String topChartPath = getTopChartPath(chartScope);
		ilsSfcGatewayHook.getTestMgr().assertEqual(topChartPath, expected, actual );
	}

	public static void assertTrue(PyChartScope chartScope, boolean condition, String msg) {
		String topChartPath = getTopChartPath(chartScope);
		ilsSfcGatewayHook.getTestMgr().assertTrue(topChartPath, condition,  msg);
	}
	
	public static void failTest(PyChartScope chartScope, String message) {
		String topChartPath = getTopChartPath(chartScope);
		ilsSfcGatewayHook.getTestMgr().fail(topChartPath, message);
	}

	public static void failTestChart(String topChartPath, String message) {
		ilsSfcGatewayHook.getTestMgr().fail(topChartPath, message);
	}

	public static boolean testsAreRunning() {
		return ilsSfcGatewayHook.getTestMgr().testsAreRunning();
	}
	public static void passTest(PyChartScope chartScope) {
		String topChartPath = getTopChartPath(chartScope);
		ilsSfcGatewayHook.getTestMgr().pass(topChartPath);
	}

	public static void timeoutRunningTests() {
		ilsSfcGatewayHook.getTestMgr().timeoutRunningTests();
	}

	public static void reportTests() {
		ilsSfcGatewayHook.getTestMgr().report();
	}
	
	public static List<String> getMatchingCharts(String regex, String projectName) {
		Project project = ilsSfcGatewayHook.getContext().getProjectManager().getProject(projectName).get();
		List<String> matchingCharts = new ArrayList<String>();
		List<ProjectResource> resources = project.getResources();
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals("sfc-chart-ui-model")) {
				String path = res.getFolderPath();
				if(path.matches(regex)) {
					matchingCharts.add(path);
				}
			}
		}
		return matchingCharts;
	}	
	
}
