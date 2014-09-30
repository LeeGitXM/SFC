/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;

import com.ils.sfc.client.*;
import com.ils.sfc.common.*;
import com.ils.sfc.util.IlsGatewayScriptsIF;
import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.DesignerModuleHook;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	public static String HOOK_BUNDLE_NAME   = "designer";      // Properties file is designer.properties
	public static String PREFIX = IlsSfcProperties.BUNDLE_PREFIX; // Properties are accessed by this prefix

	private DesignerContext context = null;
	private final LoggerEx log;
	
	public IlsSfcDesignerHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	@Override
	public void initializeScriptManager(ScriptManager mgr) {
		super.initializeScriptManager(mgr);
    	mgr.addScriptModule("ils.sfc", ModuleRPCFactory.create(IlsSfcProperties.MODULE_ID, IlsGatewayScriptsIF.class));
		//mgr.addScriptModule(SFCProperties.APPLICATION_SCRIPT_PACKAGE,ApplicationScriptFunctions.class);
	}
	
	@Override
	public void startup(DesignerContext ctx, LicenseState activationState) throws Exception {
		this.context = ctx;

    	// register step factories. this is duplicated in IlsSfcClientHook.
		Object iaSfcGatewayHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcGatewayHook).getStepRegistry();
		stepRegistry.register(QueueMessageStepUI.FACTORY);
		stepRegistry.register(SetQueueStepUI.FACTORY);
		stepRegistry.register(ShowQueueStepUI.FACTORY);
		stepRegistry.register(ClearQueueStepUI.FACTORY);
    	
		// register the config factories (ie the editors)
		IlsStepEditor.Factory editorFactory = new IlsStepEditor.Factory();
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	configRegistry.register(QueueMessageStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(SetQueueStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(ShowQueueStepProperties.FACTORY_ID, editorFactory);
    	configRegistry.register(ClearQueueStepProperties.FACTORY_ID, editorFactory);
	}
		
	@Override
	public void shutdown() {	
	}

}
