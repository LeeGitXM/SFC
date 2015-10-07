/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.gateway;

import java.util.UUID;

import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;


/**
 *  The RPC Dispatcher is the point of entry for incoming RPC requests.
 *  Its purpose is simply to parse out a request and send it to the
 *  handler. Use of the GatewayRequestHandler as a delegate provides
 *  a common handler for both the RPC and scripting interfaces.
 */
public class BrowserRpcDispatcher   {
	private final GatewayRequestHandler requestHandler;

	/**
	 * Constructor. There is a separate dispatcher for each project.
	 */
	public BrowserRpcDispatcher(GatewayContext context,ClientReqSession session,Long projectId) {
		this.requestHandler = new GatewayRequestHandler(context,session,projectId);
	}
	
	public UUID startChart(String chartName) {
		return requestHandler.startChart(chartName);
	}
}
