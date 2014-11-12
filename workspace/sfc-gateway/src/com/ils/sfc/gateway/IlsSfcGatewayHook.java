/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import com.ils.sfc.step.*;
import com.ils.sfc.util.IlsResponseManager;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
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
	}

	@Override
	public void initializeScriptManager(ScriptManager manager) {
		PythonCall.setScriptMgr(manager);
		manager.addScriptModule("system.ils.sfc", IlsResponseManager.class);	
		//manager.addStaticFields("system.ils.sfc", IlsSfcNames.class);
	};
	
	@Override
	public void startup(LicenseState licenseState) {
		ScriptManager.asynchInit("C:/Program Files/Inductive Automation/Ignition/user-lib/pylib");		
 	    log.infof("%s: Startup complete.",TAG);
		// register the step factories:
		SfcGatewayHook sfcHook = (SfcGatewayHook) context.getModule(SFCModule.MODULE_ID);
		sfcHook.getStepRegistry().register(new QueueMessageStepFactory());
		sfcHook.getStepRegistry().register(new SetQueueStepFactory());
		sfcHook.getStepRegistry().register(new ShowQueueStepFactory());
		sfcHook.getStepRegistry().register(new ClearQueueStepFactory());
		sfcHook.getStepRegistry().register(new YesNoStepFactory());
		sfcHook.getStepRegistry().register(new AbortStepFactory());
		sfcHook.getStepRegistry().register(new PauseStepFactory());
		sfcHook.getStepRegistry().register(new ControlPanelMessageStepFactory());
		sfcHook.getStepRegistry().register(new TimedDelayStepFactory());
		sfcHook.getStepRegistry().register(new DeleteDelayNotificationStepFactory());
		sfcHook.getStepRegistry().register(new PostDelayNotificationStepFactory());
		sfcHook.getStepRegistry().register(new EnableDisableStepFactory());
		sfcHook.getStepRegistry().register(new SelectInputStepFactory());
		sfcHook.getStepRegistry().register(new LimitedInputStepFactory());
		sfcHook.getStepRegistry().register(new DialogMessageStepFactory());
		sfcHook.getStepRegistry().register(new CollectDataStepFactory());
		sfcHook.getStepRegistry().register(new InputStepFactory());
		sfcHook.getStepRegistry().register(new SimpleQueryStepFactory());
		sfcHook.getStepRegistry().register(new SaveDataStepFactory());
		sfcHook.getStepRegistry().register(new PrintFileStepFactory());
		sfcHook.getStepRegistry().register(new PrintWindowStepFactory());
		sfcHook.getStepRegistry().register(new IlsEnclosingStepFactory());
		sfcHook.getStepRegistry().register(new CloseWindowStepFactory());
	}

	@Override
	public void shutdown() {
	}


}
