/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer;


import java.util.HashMap;
import java.util.Map;

import com.ils.sfc.client.TestStepUI;
import com.ils.sfc.common.IlsSfcProperties;
import com.ils.sfc.common.TestStepProperties;
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
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.designer.api.StepConfigRegistry;

public class IlsSfcDesignerHook extends AbstractDesignerModuleHook implements DesignerModuleHook {
	private static final String TAG = "IlsSfcDesignerHook";
	public static String HOOK_BUNDLE_NAME   = "designer";      // Properties file is designer.properties
	public static String PREFIX = IlsSfcProperties.BUNDLE_PREFIX; // Properties are accessed by this prefix

	private DesignerContext context = null;
	private final LoggerEx log;
	private Map<String,StepConfigFactory> configFactoriesById = new HashMap<String,StepConfigFactory>();
	
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
		Object iaSfcGatewayHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcGatewayHook).getStepRegistry();
		stepRegistry.register(TestStepUI.FACTORY);
    	
    	StepConfigRegistry configRegistry = (StepConfigRegistry) context.getModule(SFCModule.MODULE_ID);
    	configRegistry.register(TestStepProperties.FACTORY_ID, new TestStepEditor.DesignerStepEditorFactory(context));
	}
		
	@Override
	public void shutdown() {	
	}


}
