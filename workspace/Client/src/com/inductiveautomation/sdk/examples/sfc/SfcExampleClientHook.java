package com.inductiveautomation.sdk.examples.sfc;


import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;

public class SfcExampleClientHook extends AbstractClientModuleHook {

    @Override
    public void startup(ClientContext context, LicenseState activationState) throws Exception {
        ClientStepRegistry.getInstance(context).register(ExampleStepUI.FACTORY);
    }

}
