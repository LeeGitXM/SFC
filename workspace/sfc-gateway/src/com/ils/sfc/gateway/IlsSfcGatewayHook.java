/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import com.ils.sfc.step.*;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.SfcGatewayHook;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook  {
	public static String TAG = "SFCGatewayHook";
	private final LoggerEx log;
	private GatewayContext context = null;
	
	public IlsSfcGatewayHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	// NOTE: During this period, the module status is LOADED, not RUNNING

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		PythonCall.setScriptMgr(ctxt.getScriptManager());
		
		// register the step factories:
		SfcGatewayHook sfcHook = (SfcGatewayHook) context.getModule(SFCModule.MODULE_ID);
		sfcHook.getStepRegistry().register(new QueueMessageStepFactory());
		sfcHook.getStepRegistry().register(new SetQueueStepFactory());
		sfcHook.getStepRegistry().register(new ShowQueueStepFactory());
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
		return new IlsGatewayScripts(context);
	}

}
