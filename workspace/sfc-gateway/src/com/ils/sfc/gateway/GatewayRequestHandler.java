/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.python.core.PyDictionary;

import com.ils.common.persistence.ToolkitProperties;
import com.ils.common.persistence.ToolkitRecordHandler;
import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.chartStructure.StepStructure;
import com.inductiveautomation.ignition.common.datasource.DatasourceStatus;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.datasource.Datasource;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import system.ils.sfc.common.Constants;

/**
 *  This handler provides is a common class for handling requests for block properties and control
 *  of the execution engine. The requests can be expected arrive both through the scripting interface
 *  and the RPC diispatcher.In general, the calls are made to update properties 
 *  in the block objects and to trigger their evaluation.
 *  
 *  
 *  This class is a singleton for easy access throughout the application.
 */
public class GatewayRequestHandler {
	private final static String TAG = "ControllerRequestHandler";
	private final LoggerEx log;
	private final GatewayContext context;
	private final ChartStructureManager structureManager;
	private final ToolkitRecordHandler recordHandler;
	private final IlsRequestResponseManager responseManager;

	/**
	 * Constructor: Created in the hook class.
	 */
	public GatewayRequestHandler(GatewayContext ctx,ChartStructureManager structMgr,IlsRequestResponseManager responseMgr) {
		this.context = ctx;
		this.recordHandler = new ToolkitRecordHandler(context);
		this.structureManager = structMgr;
		this.responseManager = responseMgr;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}


	/**
	 * Clear any pending requests/responses.
	 */
	public void clearRequestResponseMaps() {
		responseManager.clear();
	}
	
	public String getChartPath(long resourceId) {
		return structureManager.getChartPath(resourceId);
	}
	
	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the database for production or isolation mode, as appropriate.
	 */
	public String getDatabaseName(boolean isIsolated) {
		String dbName = "";
		if( isIsolated ) dbName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_DATABASE);
		else dbName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_DATABASE);
		return dbName;
	}
	
	/**
	 * Find the full path to user-lib under the current running Ignition instance.
	 * 
	 * @return the path to the user-lib directory.
	 */
	public String getUserLibPath()  {
		String userLibPath = context.getUserlibDir().getAbsolutePath();
		return userLibPath;
	}
	
	/**
	 * A list of data sources is not available in client scope.
	 * @return
	 */
	public List<String> getDatasourceNames() {
		List<Datasource> sources = context.getDatasourceManager().getDatasources();
		List<String> result = new ArrayList<>();
		for( Datasource source:sources) {
			if(source.getStatus().equals(DatasourceStatus.VALID)) {
				result.add(source.getName());
			}
		}
		return result;
	}
	
	/**
	 * Find the tag provider associated with the sequential function charts.
	 * 
	 * @param isIsolation true if this the chart is in an isolation (test) state.
	 * @return name of the tag provider for production or isolation mode, as appropriate.
	 */
	public String getProviderName(boolean isIsolated)  {
		String providerName = "";
		if( isIsolated ) providerName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_PROVIDER);
		else providerName = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_PROVIDER);
		return providerName;
	}
	/**
	 * Get the clock rate factor. For non-isolation mode the value is fixed at 1.0.
	 * This method is provided as a hook for test frameworks.
	 * @param isIsolated. True if the system is currently in ISOLATION mode.
	 * @return the amount to speed up or slow down the clock. A value less
	 *         than one represents a clock speedup.
	 */
	public double getTimeFactor(boolean isIsolated) {
		double factor = 1.0;
		if( isIsolated ) {
			String value = getToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_TIME);
			try {
				factor = Double.parseDouble(value);
			}
			catch( NumberFormatException nfe) {
				log.warnf("%s.getTimeFactor: stored value (%s), not a double. Using 1.0",TAG,value);
			}
		}
		if( factor<=0.0 ) factor = 1.0;
		return factor;
	}
	/**
	 * On a failure to find the property, an empty string is returned.
	 */
	public String getToolkitProperty(String propertyName) {
		return recordHandler.getToolkitProperty(propertyName);
	}
	/**
	 * If there is an outstanding request from the specified step,
	 * then post a response.
	 * @param diagramId UUID of the parent diagram as a String.
	 * @param stepName
	 * @return the count of outstanding requests for this step. 
	 */
	public int requestCount(String chartPath, String stepName) {
		Map<String,String> stepMap = responseManager.getStepIdsByRequestId();
		int count = 0;
		if( !stepMap.isEmpty() ) {
			// Convert step name into id
			ChartStructureCompiler compiler = structureManager.getCompiler();
			StepStructure stepInfo = compiler.getStepInformation(chartPath, stepName);
			if( stepInfo!=null ) {
				String sid = stepInfo.getId();
				// Look for the specified step in the list of pending responses.
				for( String stepId:stepMap.values()) {
					if( stepId.equals(sid) ) {
						count++;
					}
				}
			}
		}
		return count;
	}
	/**
	 * If there is an outstanding request from the specified step,
	 * then post a response.
	 * @param diagramId UUID of the parent diagram as a String.
	 * @param stepName
	 */
	public boolean postResponse(String chartPath, String stepName, String response) {
		boolean result = false;
		Map<String,String> stepMap = responseManager.getStepIdsByRequestId();
		// Convert step name into id
		ChartStructureCompiler compiler = structureManager.getCompiler();
		StepStructure stepInfo = compiler.getStepInformation(chartPath, stepName);
		if( stepInfo!=null ) {
			String sid = stepInfo.getId();
			// Look for the specified step in the list of pending responses.
			// Arbitrarily choose the first for this step (there should only be one)
			for( String rid:stepMap.keySet()) {
				if( sid.equals(stepMap.get(rid)) ) {
					// Assemble response
					PyDictionary payload = new PyDictionary();
					payload.put(Constants.MESSAGE_ID, rid);
				    payload.put(Constants.RESPONSE,response);
					responseManager.setResponse(rid,payload);
					result = true;
				}
			}
			if( !result ) {
				log.warnf("%s.postResponse: No pending request for %s:%s(%s)",TAG,chartPath,stepName,sid);
			}
		}
		else {
			log.warnf("%s.postResponse: No stepInfo for %s:%s",TAG,chartPath,stepName);
		}
		
		return result;
	}
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to factor all times.
	 */
	public void setTimeFactor(double factor) {
		if( factor<=0.0 ) factor = 1.0;
		String value = String.valueOf(factor);
		setToolkitProperty(ToolkitProperties.TOOLKIT_PROPERTY_ISOLATION_TIME,value);
	}
	/**
	 * We have two types of properties of interest here. The first set is found in ScriptConstants
	 * and represents scripts used for external Python interfaces to Application/Family.
	 * The second category represents database and tag interfaces for production and isolation
	 * modes.
	 */
	public void setToolkitProperty(String propertyName, String value) {
		recordHandler.setToolkitProperty(propertyName, value);
	}
}

