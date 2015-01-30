/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.oldRecipe.RecipeDataManager;
import com.ils.sfc.step.*;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectListener;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
//import com.inductiveautomation.sfc.ChartManager;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.SfcGatewayHook;
import com.inductiveautomation.sfc.api.elements.StepFactory;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer {
	public static String TAG = "SFCGatewayHook";
	private final LoggerEx log;
	private GatewayContext context = null;
	private ChartManagerService chartManager;
	private Map<UUID, PyDictionary> statusesById = Collections.synchronizedMap(
		new HashMap<UUID, PyDictionary>());

	private StepFactory[] stepFactories = {
		new QueueMessageStepFactory(),
		new SetQueueStepFactory(),
		new ShowQueueStepFactory(),
		new ClearQueueStepFactory(),
		new YesNoStepFactory(),
		new AbortStepFactory(),
		new PauseStepFactory(),
		new ControlPanelMessageStepFactory(),
		new TimedDelayStepFactory(),
		new DeleteDelayNotificationStepFactory(),
		new PostDelayNotificationStepFactory(),
		new EnableDisableStepFactory(),
		new SelectInputStepFactory(),
		new LimitedInputStepFactory(),
		new DialogMessageStepFactory(),
		new CollectDataStepFactory(),
		new InputStepFactory(),
		new RawQueryStepFactory(),
		new SimpleQueryStepFactory(),
		new SaveDataStepFactory(),
		new PrintFileStepFactory(),
		new PrintWindowStepFactory(),
		new ShowWindowStepFactory(),
		new CloseWindowStepFactory(),
		new ReviewDataStepFactory(),
		new ReviewDataWithAdviceStepFactory(),
		new ReviewFlowsStepFactory(),
		new IlsEnclosingStepFactory(),
	};
	
	public IlsSfcGatewayHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
		
	// NOTE: During this period, the module status is LOADED, not RUNNING

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		context.getModuleServicesManager().subscribe(ChartManagerService.class, this);
	}

	@Override
	public void initializeScriptManager(ScriptManager manager) {
		PythonCall.setScriptMgr(manager);
		manager.addScriptModule("system.ils.sfc", IlsGatewayScripts.class);	
		//manager.addStaticFields("system.ils.sfc", IlsSfcNames.class);
	};
	
	@Override
	public void startup(LicenseState licenseState) {
		ScriptManager.asynchInit("C:/Program Files/Inductive Automation/Ignition/user-lib/pylib");		
		
		// Setup a listener so if recipe data gets modified by designer, 
		// RecipeDataManager reloads
		context.getProjectManager().addProjectListener(new ProjectListener() {
			public void projectAdded(Project arg0, Project arg1) {}
			public void projectDeleted(long arg0) {}

			public void projectUpdated(Project project, ProjectVersion arg1) {
				// this is overly broad, but if we see a change in the global project
				// we assume an SFC may have changed an set the recipe data stale:
				if(project.getId() == -1) { // global project					
					RecipeDataManager.setStale();
				}
			}
			
		});
		RecipeDataManager.setContext(new RecipeDataManager.Context() {
			public long createResourceId() {
				try {
					return context.getProjectManager().getNewResourceId();
				} catch (Exception e) {
					log.error("Error creating new resource id", e);
					return 0;
				}
			}
			
			public Project getGlobalProject() {
				return context.getProjectManager().getGlobalProject(-1);
			}

			public boolean isClient() {
				return false;
			}
			
		});
		SfcGatewayHook iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);
		RecipeDataManager.setStepRegistry(iaSfcHook.getStepRegistry());
	    log.infof("%s: Startup complete.",TAG);
	}
	
	@Override
	public void shutdown() {
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == ChartManagerService.class) {
            chartManager = context.getModuleServicesManager().getService(ChartManagerService.class);
    		for(StepFactory stepFactory: stepFactories) {
    			chartManager.register(stepFactory);
    		}
        }
	}

	@Override
	public void serviceShutdown(Class<?> arg0) {
		for(StepFactory stepFactory: stepFactories) {
			chartManager.unregister(stepFactory);
		}
		chartManager = null;
	}


}
