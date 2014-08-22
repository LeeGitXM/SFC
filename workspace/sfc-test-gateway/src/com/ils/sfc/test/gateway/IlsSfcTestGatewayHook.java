/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.test.gateway;

import com.ils.sfc.test.common.IlsSfcTestProperties;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;


/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 * 
 * At present this code does nothing.
 */
public class IlsSfcTestGatewayHook extends AbstractGatewayModuleHook  {
	public static String TAG = "IlsSfcTestGatewayHook";
	private transient IlsSfcTestGatewayRpcDispatcher dispatcher = null;
	private transient GatewayContext context = null;
	private final LoggerEx log;
	
	public IlsSfcTestGatewayHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		log.info(TAG+"Initializing IlsSfc Gateway hook (for test)");
	}
		
	// NOTE: During this period, the module status is LOADED, not RUNNING

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
	}

	@Override
	public void startup(LicenseState licenseState) {
		// Look for all block resources and inform the execution controller

	    
	    MockChartRequestHandler requestHandler = new MockChartRequestHandler(context);
	    dispatcher = new IlsSfcTestGatewayRpcDispatcher(context,requestHandler);
		GatewayMockChartScriptFunctions.requestHandler = requestHandler;
		log.infof("%s.startup: complete.",TAG);
	}

	@Override
	public void shutdown() {
	}

	@Override
	public Object getRPCHandler(ClientReqSession session, Long projectId) {
		log.debugf("%s.getRPCHandler - request for project %s",TAG,projectId.toString());
		return dispatcher;
	}
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		mgr.addScriptModule(IlsSfcTestProperties.MOCK_SCRIPT_PACKAGE,GatewayMockChartScriptFunctions.class);
	}

}
