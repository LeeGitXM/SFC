/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.browser;

import java.util.List;
import java.util.UUID;

import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



/**
 *  This class is a common point for managing RPC requests to the gateway.
 *  For the SFC Browser there is currently only one such request.
 */
public class SfcBrowserRequestHandler {
	private final static String TAG = "SfcBrowserRequestHandler";
	private final LoggerEx log;

	/**
	 * Constructor:
	 */
	public SfcBrowserRequestHandler()  {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/**
	 * Find the database associated with the sequential function charts.
	 * 
	 * @param path the chart path in the Designer navigation tree.
	 * @return the unique ID of the running instance.
	 */
	public UUID startChart(String path) {
		UUID result = null;
		try {
			result = (UUID)GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
					BrowserConstants.MODULE_ID, "startChart",path);
		}
		catch(Exception ge) {
			log.infof("%s.startChart: GatewayException (%s)",TAG,ge.getMessage());
		}
		return result;
	}
}
