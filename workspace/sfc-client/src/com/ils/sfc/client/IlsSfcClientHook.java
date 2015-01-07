package com.ils.sfc.client;

import com.ils.sfc.client.step.AbortStepUI;
import com.ils.sfc.client.step.ClearQueueStepUI;
import com.ils.sfc.client.step.CloseWindowStepUI;
import com.ils.sfc.client.step.CollectDataStepUI;
import com.ils.sfc.client.step.ControlPanelMessageStepUI;
import com.ils.sfc.client.step.DeleteDelayNotificationStepUI;
import com.ils.sfc.client.step.DialogMessageStepUI;
import com.ils.sfc.client.step.EnableDisableStepUI;
import com.ils.sfc.client.step.IlsEnclosingStepUI;
import com.ils.sfc.client.step.InputStepUI;
import com.ils.sfc.client.step.LimitedInputStepUI;
import com.ils.sfc.client.step.OperationStepUI;
import com.ils.sfc.client.step.PauseStepUI;
import com.ils.sfc.client.step.PhaseStepUI;
import com.ils.sfc.client.step.PostDelayNotificationStepUI;
import com.ils.sfc.client.step.PrintFileStepUI;
import com.ils.sfc.client.step.PrintWindowStepUI;
import com.ils.sfc.client.step.ProcedureStepUI;
import com.ils.sfc.client.step.QueueMessageStepUI;
import com.ils.sfc.client.step.RawQueryStepUI;
import com.ils.sfc.client.step.SaveDataStepUI;
import com.ils.sfc.client.step.SelectInputStepUI;
import com.ils.sfc.client.step.SetQueueStepUI;
import com.ils.sfc.client.step.ShowQueueStepUI;
import com.ils.sfc.client.step.ShowWindowStepUI;
import com.ils.sfc.client.step.SimpleQueryStepUI;
import com.ils.sfc.client.step.TimedDelayStepUI;
import com.ils.sfc.client.step.YesNoStepUI;
import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.client.model.ClientContext;
import com.inductiveautomation.ignition.client.util.EDTUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.SFCModule;
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
        log.debug("starting up...");
    	IlsSfcClientContext.getInstance().setClientContext(context);
    	GatewayConnectionManager.getInstance().addPushNotificationListener(IlsSfcClientContext.getInstance());
		RecipeDataManager.setContext(new RecipeDataManager.Context() {
			public long createResourceId() {
				String errMsg = "Recipe Data not present in client! Must be created in Designer.";
				log.error(errMsg);
				throw new UnsupportedOperationException(errMsg);
			}
			
			public Project getGlobalProject() {
				return context.getGlobalProject().getProject();
			}

			public boolean isClient() {
				return true;
			}
			
		});
    	Runnable r = new Runnable() {
            @Override
            public void run() {
            	registerSteps();
            }
        };
        EDTUtil.invokeAfterJoin(r, Thread.currentThread());
    }

    private void registerSteps() {
    	// register step factories. this is duplicated in IlsSfcDesignerHook.
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry stepRegistry =  ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		RecipeDataManager.setStepRegistry(stepRegistry);
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
		stepRegistry.register(PauseStepUI.FACTORY);
		stepRegistry.register(RawQueryStepUI.FACTORY);
		stepRegistry.register(SimpleQueryStepUI.FACTORY);
		stepRegistry.register(SaveDataStepUI.FACTORY);
		stepRegistry.register(PrintFileStepUI.FACTORY);
		stepRegistry.register(IlsEnclosingStepUI.FACTORY);
		stepRegistry.register(PrintWindowStepUI.FACTORY);
		stepRegistry.register(CloseWindowStepUI.FACTORY);
		stepRegistry.register(ShowWindowStepUI.FACTORY);
		stepRegistry.register(ShowWindowStepUI.FACTORY);
		stepRegistry.register(ProcedureStepUI.FACTORY);
		stepRegistry.register(OperationStepUI.FACTORY);
		stepRegistry.register(PhaseStepUI.FACTORY);
    }
    
    @Override
    public void initializeScriptManager(ScriptManager manager) {
		super.initializeScriptManager(manager);
		PythonCall.setScriptMgr(manager);
		manager.addStaticFields("system.ils.sfc", IlsSfcNames.class);
    }

}

