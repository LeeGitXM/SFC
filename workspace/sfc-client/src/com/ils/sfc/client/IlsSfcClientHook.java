package com.ils.sfc.client;

import com.ils.sfc.common.IlsSfcProperties;
import com.ils.sfc.util.IlsGatewayScriptsIF;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;

public class IlsSfcClientHook extends AbstractClientModuleHook {

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        //ClientStepRegistry.getInstance(context).register(QueueMessageStepUI.FACTORY);
        //ClientStepRegistry.getInstance(context).register(ClearQueueStepUI.FACTORY);
        //ClientStepRegistry.getInstance(context).register(SetQueueStepUI.FACTORY);
        //ClientStepRegistry.getInstance(context).register(ShowQueueStepUI.FACTORY);
    	IlsSfcClientContext.getInstance().setClientContext(context);
    	GatewayConnectionManager.getInstance().addPushNotificationListener(IlsSfcClientContext.getInstance());
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
    	manager.addScriptModule("ils.sfc", ModuleRPCFactory.create(IlsSfcProperties.MODULE_ID, IlsGatewayScriptsIF.class));
    }
}

