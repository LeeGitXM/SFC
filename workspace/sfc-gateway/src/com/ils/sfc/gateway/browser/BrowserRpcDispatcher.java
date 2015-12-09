/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.gateway.browser;

import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;


/**
 *  The RPC Dispatcher is the point of entry for incoming RPC requests.
 *  Its purpose is simply to parse out a request and send it to the
 *  handler. Use of the GatewayRequestHandler as a delegate provides
 *  a common handler for both the RPC and scripting interfaces.
 */
public class BrowserRpcDispatcher   {
	private final GatewayContext context;
	private final GatewayRequestHandler requestHandler;

	/**
	 * Constructor. There is a separate dispatcher for each project.
	 */
	public BrowserRpcDispatcher(GatewayContext context,ClientReqSession session,Long projectId) {
		this.context = context;
		this.requestHandler = new GatewayRequestHandler(context,session,projectId);
	}
	
}
