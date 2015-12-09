/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.designer.browser;

import java.util.UUID;

import com.ils.sfc.common.IlsSfcModule;
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
	 * @param user name of the user for launching client windows
	 * @param isolation true if the chart is to run in isloation mode
	 * @return the unique ID of the running instance.
	 */
	public UUID startChart(String path,String clientProject, String user,boolean isolation) {
		UUID result = null;
		try {
			result = GatewayConnectionManager.getInstance().getGatewayInterface().moduleInvoke(
				IlsSfcModule.MODULE_ID, "startChart",path,clientProject,user,new Boolean(isolation));
		}
		catch(Exception ge) {
			log.infof("%s.startChart: GatewayException (%s)",TAG,ge.getMessage());
		}
		return result;
	}
}
