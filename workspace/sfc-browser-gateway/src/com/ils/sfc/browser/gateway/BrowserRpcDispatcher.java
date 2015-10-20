/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.gateway;

import java.util.Properties;

import system.ils.sfc.common.Constants;
import org.python.core.PyDictionary;

import com.inductiveautomation.ignition.common.script.message.MessageDispatchManager;
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
	
	public void startChart(String chartPath,String clientProject, String user,Boolean isolation) {
		PyDictionary payload = new PyDictionary();
		payload.put(Constants.CHART_PATH, chartPath);
		payload.put(Constants.PROJECT, clientProject);
		payload.put(Constants.USER, user);
		payload.put(Constants.ISOLATION_MODE, isolation);
		Properties filterParams = new Properties();
		filterParams.setProperty(MessageDispatchManager.KEY_SCOPE, MessageDispatchManager.SCOPE_GATEWAY_ONLY); 
		context.getMessageDispatchManager().dispatch(clientProject, "sfcDebugChart", payload, filterParams);
	}
}
