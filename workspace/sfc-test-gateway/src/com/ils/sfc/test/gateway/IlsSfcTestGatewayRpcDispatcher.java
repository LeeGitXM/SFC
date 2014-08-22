/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.test.gateway;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;


/**
 *  The RPC Dispatcher is the point of entry for incoming RCP requests.
 *  Its purpose is simply to parse out a request and send it to the
 *  right handler. This class supports the aggregate of RPC interfaces.
 */
public class IlsSfcTestGatewayRpcDispatcher {
	private static String TAG = "IlsSfcTestGatewayRpcDispatcher";
	private final LoggerEx log;
	private final GatewayContext context;
	private final MockChartRequestHandler requestHandler;
	
	/**
	 * Constructor. On instantiation, the dispatcher creates instances
	 * of all required handlers.
	 */
	public IlsSfcTestGatewayRpcDispatcher(GatewayContext cntx,MockChartRequestHandler rh ) {
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.context = cntx;
		this.requestHandler = rh;
	}

	//=============================== Methods in the MockChartScriptingInterface ===================================
	
}
