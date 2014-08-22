package com.inductiveautomation.sdk.examples.sfc;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sdk.examples.sfc.ExampleStepEditor.DesignerStepEditorFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;

public class SfcExampleDesignerHook extends AbstractDesignerModuleHook {

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
    	System.out.println("Registering the example step");
    	ClientStepRegistry.getInstance(context).register(ExampleStepUI.FACTORY);
        SFCDesignerHook.get(context).register(ExampleStepProperties.FACTORY_ID, new DesignerStepEditorFactory(context));
        System.out.println("...done");
    }

}
