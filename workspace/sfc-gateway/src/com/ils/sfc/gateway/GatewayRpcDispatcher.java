/**
 *   (c) 2015-2021  ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

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
	private final GatewayRequestHandler requestHandler;

	/**
	 * Constructor. There is a separate dispatcher for each project.
	 */
	public GatewayRpcDispatcher(GatewayRequestHandler rh) {
		this.requestHandler = rh;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	public String getChartPath(Long resid) {
		return requestHandler.getChartPath(resid.longValue());
	}
	
	public List<String> getDatasourceNames() {
		return requestHandler.getDatasourceNames();
	}
	
	public String getHostname() {
		String hostname = "localhost";
		try {
			InetAddress host = InetAddress.getLocalHost();
			hostname = host.getHostName();
		}
		catch(UnknownHostException ex) {
			log.warnf("%s: getHostname: unknown host exception (%s)", TAG, ex.getLocalizedMessage());
		}
		return hostname;
	}
	public String getUserLibPath() {
		return requestHandler.getUserLibPath();
	}
	
	public String getProjectToolkitProperty(String projectName,String propertyName) {
		return requestHandler.getProjectToolkitProperty(projectName,propertyName);
	}
	public String getWindowsBrowserPath() {
		return requestHandler.getWindowsBrowserPath();
	}
	/**
	 * Set a clock rate factor. This will change timing for isolation mode only.
	 * This method is provided as a hook for test frameworks.
	 * @param factor the amount to speed up or slow down the all times.
	 */
	public void setProjectTimeFactor(String projectName,Double factor) {
		requestHandler.setProjectTimeFactor(projectName,factor.doubleValue());
	}
	public void setProjectToolkitProperty(String projectName,String propertyName,String value) {
		//log.infof("%s.setToolkitProperty: %s: %s", TAG, propertyName, value);
		requestHandler.setProjectToolkitProperty(projectName,propertyName,value);
	}
	
	public UUID startChart(String chartPath,String clientProject, String user,Boolean isolation) {
		return IlsGatewayScripts.debugChart(chartPath, clientProject, user, isolation);
	}
}
