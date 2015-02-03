package com.ils.sfc.client;

import com.ils.sfc.client.step.*;
import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.client.util.EDTUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectChangeListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.vision.api.client.AbstractClientModuleHook;
import com.inductiveautomation.vision.api.client.ClientModuleHook;

public class IlsSfcClientHook extends AbstractClientModuleHook implements ClientModuleHook{
	private ClientContext context;
	private final LoggerEx log;

	public IlsSfcClientHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());		
	}
	
    @Override
    public void startup(final ClientContext context, LicenseState activationState) throws Exception {
        this.context = context;
        context.getGlobalProject().addProjectChangeListener(new ProjectChangeListener() {
			public void projectResourceModified(ProjectResource arg0,ResourceModification arg1) {
				System.out.println("projectResourceModified");
			}

			public void projectUpdated(Project arg0) {
				System.out.println("projectUpdated");				
			}        	
        });
        ProjectResource recipeDataResource = context.getGlobalProject().getProject().getResourceOfType(
    			IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE);
        System.out.println("recipe resource: " + recipeDataResource);
        log.debug("starting up...");
		registerSteps();
    }

    private void registerSteps() {
    	// register step factories. this is duplicated in IlsSfcDesignerHook.
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		for(ClientStepFactory clientStepFactory: AbstractIlsStepUI.clientStepFactories) {
			stepRegistry.register(clientStepFactory);
		}
    }
    
    @Override
    public void initializeScriptManager(ScriptManager manager) {
		super.initializeScriptManager(manager);
		PythonCall.setScriptMgr(manager);
		manager.addStaticFields("system.ils.sfc", IlsSfcNames.class);
    }

}

