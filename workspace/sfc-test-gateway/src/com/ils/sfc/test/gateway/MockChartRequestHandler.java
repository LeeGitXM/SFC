/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.test.gateway;

import com.ils.sfc.test.common.MockChartScriptingInterface;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

/**
 *  This handler provides is a common class for handling requests dealing with mock diagrams.
 *  The requests can be expected to arrive both through the scripting interface
 *  and the RPC dispatcher.  Handle those requests which are more than simple passthrus 
 *  to the BlockExecutionController
 *  
 *  
 *  This class is a singleton for easy access throughout the application.
 */
public class MockChartRequestHandler implements MockChartScriptingInterface  {
	private final static String TAG = "MockChartRequestHandler";
	private final LoggerEx log;
	private GatewayContext context = null;

	
	/**
	 * Initialize with a Gateway context.
	 */
	public MockChartRequestHandler(GatewayContext cntx) {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		this.context = cntx;

	}
	
}
