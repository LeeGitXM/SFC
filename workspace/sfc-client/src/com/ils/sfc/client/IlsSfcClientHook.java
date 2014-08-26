package com.ils.sfc.client;

import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;

public class IlsSfcClientHook extends AbstractClientModuleHook {

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        ClientStepRegistry.getInstance(context).register(QueueMessageStepUI.FACTORY);
        ClientStepRegistry.getInstance(context).register(ClearQueueStepUI.FACTORY);
        ClientStepRegistry.getInstance(context).register(SetQueueStepUI.FACTORY);
        ClientStepRegistry.getInstance(context).register(ShowQueueStepUI.FACTORY);
    }

}

