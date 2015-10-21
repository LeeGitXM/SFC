/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.python.core.PyDictionary;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.script.message.MessageDispatchManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;


/**
 *  The RPC Dispatcher is the point of entry for incoming RPC requests.
 *  Its purpose is simply to parse out a request and send it to the
 *  handler. Use of the GatewayRequestHandler as a delegate provides
 *  a common handler for both the RPC and scripting interfaces.
 */
public class GatewayRpcDispatcher   {
	private static String TAG = "GatewayRpcDispatcher";
	private final LoggerEx log;
	private final GatewayContext context;
	private final GatewayRequestHandler requestHandler;

	/**
	 * Constructor. There is a separate dispatcher for each project.
	 */
	public GatewayRpcDispatcher(GatewayContext ctx) {
		this.context = ctx;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.requestHandler = GatewayRequestHandler.getInstance();
	}
	
	public List<String> getDatasourceNames() {
		return requestHandler.getDatasourceNames();
	}
	
	public String getToolkitProperty(String propertyName) {
		return requestHandler.getToolkitProperty(propertyName);
	}
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down the clock.
	 */
	public void setTimeFactor(Double factor) {
		requestHandler.setTimeFactor(factor.doubleValue());
	}
	public void setToolkitProperty(String propertyName,String value) {
		//log.infof("%s.setToolkitProperty: %s: %s", TAG, propertyName, value);
		requestHandler.setToolkitProperty(propertyName,value);
	}
	
	public UUID startChart(String chartPath,String clientProject, String user,Boolean isolation) {
		return IlsGatewayScripts.debugChart(chartPath, clientProject, user, isolation);
	}
}
