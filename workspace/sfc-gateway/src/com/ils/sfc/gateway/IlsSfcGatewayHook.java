/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.ils.sfc.step.*;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
//import com.inductiveautomation.sfc.ChartManager;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.ScopeLocator;
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
	private IlsScopeLocator scopeLocator = new IlsScopeLocator();
	
	private static StepFactory[] stepFactories = {
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

	// an index of step property names by the factory id:
	private static Map<String, List<String>> propertyNamesById = new HashMap<String, List<String>>();
	static {
		for(StepFactory stepFactory: stepFactories) {
			if(!(stepFactory instanceof AbstractIlsStepDelegate)) continue;
			List<String> propertyNames = new ArrayList<String>();
			AbstractIlsStepDelegate delegate = (AbstractIlsStepDelegate)stepFactory;
			propertyNamesById.put(delegate.getId(), propertyNames);
			for(PropertyValue<?> pval: delegate.getPropertySet()) {
				propertyNames.add(pval.getProperty().getName());
			}
		}		
	}

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
		//SfcGatewayHook iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);
	    log.infof("%s: Startup complete.",TAG);
	}
	
	@Override
	public void shutdown() {
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == ChartManagerService.class) {
            chartManager = context.getModuleServicesManager().getService(ChartManagerService.class);
    		chartManager.registerScopeLocator(scopeLocator);
            for(StepFactory stepFactory: stepFactories) {
    			chartManager.register(stepFactory);
    		}
        }
	}

	@Override
	public void serviceShutdown(Class<?> arg0) {
		chartManager.unregisterScopeLocator(scopeLocator);
		for(StepFactory stepFactory: stepFactories) {
			chartManager.unregister(stepFactory);
		}
		chartManager = null;
	}

	public static Map<String, List<String>> getPropertyNamesById() {
		return propertyNamesById;
	}

}
