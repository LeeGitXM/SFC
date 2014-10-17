package com.ils.sfc.client;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.util.IlsResponseManager;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import com.inductiveautomation.vision.api.client.ClientModuleHook;

public class IlsSfcClientHook extends AbstractClientModuleHook implements ClientModuleHook{
	private ClientContext context;
	
    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        this.context = context;
    	IlsSfcClientContext.getInstance().setClientContext(context);
    	GatewayConnectionManager.getInstance().addPushNotificationListener(IlsSfcClientContext.getInstance());
    	
    	// register step factories. this is duplicated in IlsSfcDesignerHook.
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		System.out.println("iaSfcHook " + iaSfcHook);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		stepRegistry.register(QueueMessageStepUI.FACTORY);
		stepRegistry.register(SetQueueStepUI.FACTORY);
		stepRegistry.register(ShowQueueStepUI.FACTORY);
		stepRegistry.register(ClearQueueStepUI.FACTORY);
		stepRegistry.register(YesNoStepUI.FACTORY);
		stepRegistry.register(AbortStepUI.FACTORY);
		stepRegistry.register(ControlPanelMessageStepUI.FACTORY);
		stepRegistry.register(TimedDelayStepUI.FACTORY);
		stepRegistry.register(DeleteDelayNotificationStepUI.FACTORY);
		stepRegistry.register(PostDelayNotificationStepUI.FACTORY);
		stepRegistry.register(EnableDisableStepUI.FACTORY);
		stepRegistry.register(SelectInputStepUI.FACTORY);
		stepRegistry.register(LimitedInputStepUI.FACTORY);
		stepRegistry.register(DialogMessageStepUI.FACTORY);
		stepRegistry.register(CollectDataStepUI.FACTORY);
		stepRegistry.register(InputStepUI.FACTORY);
		stepRegistry.register(RawQueryStepUI.FACTORY);
		stepRegistry.register(SimpleQueryStepUI.FACTORY);
		      }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
		super.initializeScriptManager(manager);
		PythonCall.setScriptMgr(manager);
		manager.addScriptModule("system.ils.sfc", IlsResponseManager.class);		
		manager.addStaticFields("system.ils.sfc", IlsSfcNames.class);
    }

}

