/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import com.ils.sfc.common.IlsSfcIO;
import com.ils.sfc.common.IlsSfcIOIF;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 * 
 * At present this code does nothing.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook  {
	public static String TAG = "SFCGatewayHook";
	private final LoggerEx log;
	private GatewayContext context = null;
	private static IlsSfcIO ilsSfcIo;
	private static class MessageScriptClass {
		public static void enqueueMessage(String queueName, String message, IlsSfcIOIF.MessageStatus status) {
			ilsSfcIo.enqueueMessage(queueName, message, status);
		}
		
		public static void clearQueue(String queueName) {
			ilsSfcIo.clearMessageQueue(queueName);
		}
	};
	
	public IlsSfcGatewayHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	// NOTE: During this period, the module status is LOADED, not RUNNING

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		ilsSfcIo = new IlsSfcIO(ctxt);
	}

	@Override
	public void startup(LicenseState licenseState) {
	    log.infof("%s: Startup complete.",TAG);
	}

	@Override
	public void shutdown() {
	}

	@Override
	public Object getRPCHandler(ClientReqSession session, Long projectId) {
		return null;
	}
	
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
		mgr.addScriptModule("system.ils.message", MessageScriptClass.class);
	}

}
