/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.test.gateway;

import com.ils.sfc.test.common.MockChartScriptingInterface;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 *  This class exposes python-callable functions used to report test blocks.
 *  These functions are designed for access from python scripts executing in the Gateway..
 *  
 *  Delegate most requests through the MockDiagramRequestHandler. This allows a single
 *  handler for both Gateway scripting and RPC requests.
 */
public class GatewayMockChartScriptFunctions implements MockChartScriptingInterface {
	private static final String TAG = "GatewayMockDiagramScriptFunctions: ";
	private static LoggerEx log = LogUtil.getLogger(GatewayMockChartScriptFunctions.class.getPackage().getName());
	public static MockChartRequestHandler requestHandler = null;   // Set by the hook
	
	
}