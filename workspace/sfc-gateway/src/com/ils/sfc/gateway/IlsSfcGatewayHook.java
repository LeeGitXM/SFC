/**
 *   (c) 2014-2015  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.ils.sfc.gateway.persistence.ToolkitRecord;
import com.ils.sfc.step.CancelStepFactory;
import com.ils.sfc.step.ClearQueueStepFactory;
import com.ils.sfc.step.CloseWindowStepFactory;
import com.ils.sfc.step.CollectDataStepFactory;
import com.ils.sfc.step.ControlPanelMessageStepFactory;
import com.ils.sfc.step.DeleteDelayNotificationStepFactory;
import com.ils.sfc.step.DialogMessageStepFactory;
import com.ils.sfc.step.EnableDisableStepFactory;
import com.ils.sfc.step.InputStepFactory;
import com.ils.sfc.step.LimitedInputStepFactory;
import com.ils.sfc.step.OperationStepFactory;
import com.ils.sfc.step.PauseStepFactory;
import com.ils.sfc.step.PhaseStepFactory;
import com.ils.sfc.step.PostDelayNotificationStepFactory;
import com.ils.sfc.step.PrintFileStepFactory;
import com.ils.sfc.step.PrintWindowStepFactory;
import com.ils.sfc.step.ProcedureStepFactory;
import com.ils.sfc.step.QueueMessageStepFactory;
import com.ils.sfc.step.RawQueryStepFactory;
import com.ils.sfc.step.ReviewDataStepFactory;
import com.ils.sfc.step.ReviewDataWithAdviceStepFactory;
import com.ils.sfc.step.ReviewFlowsStepFactory;
import com.ils.sfc.step.SaveDataStepFactory;
import com.ils.sfc.step.SelectInputStepFactory;
import com.ils.sfc.step.SetQueueStepFactory;
import com.ils.sfc.step.ShowQueueStepFactory;
import com.ils.sfc.step.ShowWindowStepFactory;
import com.ils.sfc.step.SimpleQueryStepFactory;
import com.ils.sfc.step.TimedDelayStepFactory;
import com.ils.sfc.step.YesNoStepFactory;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.elements.StepFactory;
//import com.inductiveautomation.sfc.ChartManager;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer {
	public static String TAG = "SFCGatewayHook";
	private final LoggerEx log;
	private transient GatewayContext context = null;
	private transient GatewayRpcDispatcher dispatcher = null;
	private ChartManagerService chartManager;
	private IlsScopeLocator scopeLocator = new IlsScopeLocator();
	private IlsChartObserver chartObserver = new IlsChartObserver();
	private IlsRequestResponseManager requestResponseManager = new IlsRequestResponseManager();
	private TestMgr testMgr = new TestMgr();
	
	private static StepFactory[] stepFactories = {
		new QueueMessageStepFactory(),
		new SetQueueStepFactory(),
		new ShowQueueStepFactory(),
		new ClearQueueStepFactory(),
		new YesNoStepFactory(),
		new CancelStepFactory(),
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
		//new IlsEnclosingStepFactory(),
		new ProcedureStepFactory(),
		new OperationStepFactory(),
		new PhaseStepFactory(),
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

	public TestMgr getTestMgr() {
		return testMgr;
	}


	public GatewayContext getContext() {
		return context;
	}

	public ChartManagerService getChartManager() {
		return chartManager;
	}


	public IlsScopeLocator getScopeLocator() {
		return scopeLocator;
	}


	public IlsChartObserver getChartObserver() {
		return chartObserver;
	}

	public IlsRequestResponseManager getRequestResponseManager() {
		return requestResponseManager;
	}
	@Override
	public Object getRPCHandler(ClientReqSession session, Long projectId) {
		return dispatcher;
	}

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		context.getModuleServicesManager().subscribe(ChartManagerService.class, this);
		IlsGatewayScripts.setHook(this);
		GatewayRequestHandler.getInstance().setContext(context);
		dispatcher = new GatewayRpcDispatcher(context);
		// Register the ToolkitRecord to make sure that the table exists
		try {
			context.getSchemaUpdater().updatePersistentRecords(ToolkitRecord.META);
		}
		catch(SQLException sqle) {
			log.error("IlsSfcGatewayHook.setup: Error registering ToolkitRecord",sqle);
		}
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
		System.out.println("shutdown");
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == ChartManagerService.class) {
            chartManager = context.getModuleServicesManager().getService(ChartManagerService.class);
    		chartManager.registerScopeLocator(scopeLocator);
    		chartManager.addChartObserver(chartObserver);
            for(StepFactory stepFactory: stepFactories) {
    			chartManager.register(stepFactory);
    		}
        }
	}

	@Override
	public void serviceShutdown(Class<?> arg0) {
		System.out.println("serviceShutdown");
		chartManager.unregisterScopeLocator(scopeLocator);
		for(StepFactory stepFactory: stepFactories) {
			chartManager.unregister(stepFactory);
		}
		chartManager.removeChartObserver(chartObserver);
		chartManager = null;
	}

	public static Map<String, List<String>> getPropertyNamesById() {
		return propertyNamesById;
	}

}
