/**
 *   (c) 2014-2015  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.common.persistence.ToolkitProjectRecord;
import com.ils.common.watchdog.WatchdogTimer;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.ils.sfc.gateway.locator.IlsScopeLocator;
import com.ils.sfc.gateway.monitor.IlsStepMonitor;
import com.ils.sfc.gateway.recipe.RecipeDataAccess;
import com.ils.sfc.step.CancelStepFactory;
import com.ils.sfc.step.ClearQueueStepFactory;
import com.ils.sfc.step.CloseWindowStepFactory;
import com.ils.sfc.step.CollectDataStepFactory;
import com.ils.sfc.step.ConfirmControllersStepFactory;
import com.ils.sfc.step.ControlPanelMessageStepFactory;
import com.ils.sfc.step.DeleteDelayNotificationStepFactory;
import com.ils.sfc.step.DialogMessageStepFactory;
import com.ils.sfc.step.EnableDisableStepFactory;
import com.ils.sfc.step.InputStepFactory;
import com.ils.sfc.step.LimitedInputStepFactory;
import com.ils.sfc.step.ManualDataEntryStepFactory;
import com.ils.sfc.step.MonitorDownloadStepFactory;
import com.ils.sfc.step.OcAlertStepFactory;
import com.ils.sfc.step.OperationStepFactory;
import com.ils.sfc.step.PVMonitorStepFactory;
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
import com.ils.sfc.step.SaveQueueStepFactory;
import com.ils.sfc.step.SelectInputStepFactory;
import com.ils.sfc.step.SetQueueStepFactory;
import com.ils.sfc.step.ShowQueueStepFactory;
import com.ils.sfc.step.ShowWindowStepFactory;
import com.ils.sfc.step.SimpleQueryStepFactory;
import com.ils.sfc.step.TimedDelayStepFactory;
import com.ils.sfc.step.WriteOutputStepFactory;
import com.ils.sfc.step.YesNoStepFactory;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.ProjectListener;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.SfcGatewayHook;
import com.inductiveautomation.sfc.api.elements.StepFactory;

import system.ils.sfc.common.Constants;

//import com.inductiveautomation.sfc.ChartManager;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer,ProjectListener {

	public static String CLSS = "IlsSfcGatewayHook";
	private final LoggerEx log;
	private transient GatewayContext context = null;
	private transient GatewayRpcDispatcher dispatcher = null;
	private SfcGatewayHook iaSfcHook;
	private ChartManagerService chartManager;
	private IlsScopeLocator scopeLocator = new IlsScopeLocator(this);
	private IlsChartObserver chartObserver = new IlsChartObserver();
	private IlsStepMonitor stepMonitor = null;
	private ChartStructureManager structureManager = null;
	private GatewayRequestHandler requestHandler = null;
	private IlsRequestResponseManager requestResponseManager = new IlsRequestResponseManager();
	private TestMgr testMgr = new TestMgr();
	private IlsDropBox dropBox = new IlsDropBox();
	private WatchdogTimer timer = null;
	private ChartDebugger chartDebugger = null;
	private static StepFactory[] stepFactories = {
		new CancelStepFactory(),
		new ClearQueueStepFactory(),
		new CloseWindowStepFactory(),
		new CollectDataStepFactory(),
		new ConfirmControllersStepFactory(),
		new ControlPanelMessageStepFactory(),
		new DeleteDelayNotificationStepFactory(),
		new DialogMessageStepFactory(),
		new EnableDisableStepFactory(),
		new InputStepFactory(),
		new LimitedInputStepFactory(),
		new MonitorDownloadStepFactory(),
		new ManualDataEntryStepFactory(),
		new OcAlertStepFactory(),
		new OperationStepFactory(),
		new PauseStepFactory(),
		new PhaseStepFactory(),
		new PostDelayNotificationStepFactory(),
		new PrintFileStepFactory(),
		new PrintWindowStepFactory(),
		new ProcedureStepFactory(),
		new PVMonitorStepFactory(),
		new QueueMessageStepFactory(),
		new RawQueryStepFactory(),
		new ReviewDataStepFactory(),
		new ReviewDataWithAdviceStepFactory(),
		new ReviewFlowsStepFactory(),
		new SaveDataStepFactory(),
		new SaveQueueStepFactory(),
		new SelectInputStepFactory(),
		new SetQueueStepFactory(),
		new SimpleQueryStepFactory(),
		new ShowQueueStepFactory(),
		new ShowWindowStepFactory(),
		new TimedDelayStepFactory(),
		new WriteOutputStepFactory(),
		new YesNoStepFactory()
	};

	// an index of step property names by the factory id:
	private static Map<String, List<String>> propertyNamesById = new HashMap<String, List<String>>();
	static {
		for(StepFactory stepFactory: stepFactories) {
			if(!(stepFactory instanceof AbstractIlsStepDelegate)) continue;
			List<String> propertyNames = new ArrayList<String>();
			AbstractIlsStepDelegate delegate = (AbstractIlsStepDelegate)stepFactory;
			propertyNamesById.put(delegate.getId(), propertyNames);
			for(PropertyValue pval: delegate.getPropertySet()) {
				propertyNames.add(pval.getProperty().getName());
			}
		}		
	}

	public IlsSfcGatewayHook() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
		timer = new WatchdogTimer(CLSS);
	}
	
	// NOTE: During this period, the module status is LOADED, not RUNNING

	public TestMgr getTestMgr() {
		return testMgr;
	}

	public ChartDebugger getChartDebugger() {return chartDebugger;}
	public IlsDropBox getDropBox() {return dropBox;}	
	public LoggerEx getLogger() { return log; }
	public IlsStepMonitor getStepMonitor() {return stepMonitor;}
	public WatchdogTimer getTimer() { return this.timer; };



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
	public Object getRPCHandler(ClientReqSession session, String projectName) {
		return dispatcher;
	}
	
	// @Override  (Ignition-7.8.3 feature)
	public boolean isFreeModule() { return true; }

	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		PythonCall.setContext(context);

		log.tracef("EREIAM JH - Setup 1 ***************************************************************************************");
		
		context.getModuleServicesManager().subscribe(ChartManagerService.class, this);
		IlsGatewayScripts.setHook(this);
		
		//sessionMgr = new IlsSfcSessionMgr(context.getMessageDispatchManager());
		// Register the ToolkitRecord to make sure that the table exists
		try {
			context.getSchemaUpdater().updatePersistentRecords(ToolkitProjectRecord.META);
		}
		catch(SQLException sqle) {
			log.error("IlsSfcGatewayHook.setup: Error registering ToolkitRecord",sqle);
		}
	}

	@Override
	public void initializeScriptManager(ScriptManager manager) {
		manager.addScriptModule("system.ils.sfc", IlsGatewayScripts.class);	
		manager.addStaticFields("system.ils.sfc.common.Constants", Constants.class);	
	};
	

	@Override
	public void startup(LicenseState licenseState) {			
		iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);				
		chartManager.addChartObserver(dropBox);

 		//structureManager = new ChartStructureManager(context.getProjectManager().getProject("global").get(),iaSfcHook.getStepRegistry());
 		//AbstractIlsStepDelegate.setStructureManager(structureManager);
 		//requestHandler = new GatewayRequestHandler(context,structureManager,requestResponseManager);
		//dispatcher = new GatewayRpcDispatcher(requestHandler);
		//chartDebugger = new ChartDebugger(
		//	context.getProjectManager().getProject("global").get(),
		//	iaSfcHook.getStepRegistry());
		//IlsGatewayScripts.setRequestHandler(requestHandler);
		//RecipeDataAccess.setRequestHandler(requestHandler);
		//stepMonitor = new IlsStepMonitor(structureManager,chartManager);
		
		//Thread delayThread = new Thread(new StructureCompilerRunner());
		//delayThread.start();
    	
		context.getProjectManager().addProjectListener(this);
    	this.timer.start();
		log.infof("%s: Startup complete.",CLSS);
	}
	
	@Override
	public void shutdown() {
		log.infof("In Shutdown(), context is " + context.toString());

//		context.getProjectManager().removeProjectListener(this);  // disabled to see if it is preventing shutdown - cjl
		log.tracef("Shutdown 2");
		stepMonitor.shutdown();
		log.tracef("Shutdown 3");
		this.timer.stop();
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == ChartManagerService.class) {
			//initializeUnits();
            chartManager = context.getModuleServicesManager().getService(ChartManagerService.class);
             for(StepFactory stepFactory: stepFactories) {
    			chartManager.register(stepFactory);
    		}            
        	chartManager.registerScopeLocator(scopeLocator);
        	chartManager.addChartObserver(chartObserver);
        	//chartManager.addChartObserver(sessionMgr);
       }
		System.out.println("serviceReady end");
	}

	@Override
	public void serviceShutdown(Class<?> arg0) {
		log.infof("In serviceShutdown()");
		chartManager.unregisterScopeLocator(scopeLocator);
		log.tracef("Service Shutdown 2");
		for(StepFactory stepFactory: stepFactories) {
			chartManager.unregister(stepFactory);
		}
		log.tracef("Service Shutdown 3");
		chartManager.removeChartObserver(chartObserver);
		log.tracef("Service Shutdown 4");
		//chartManager.removeChartObserver(sessionMgr);
		log.tracef("Service Shutdown 5");
		chartManager = null;
		log.tracef("Service Shutdown 6");
		stepMonitor.shutdown();
		log.tracef("Service Shutdown 7");
	}

	public static Map<String, List<String>> getPropertyNamesById() {
		return propertyNamesById;
	}
	

	@Override
	public void projectAdded(String arg0) {
		if(arg0 == "global") {
			structureManager.getCompiler().compile();
			log.infof("%s.projectAdded: re-analyzing charts.",CLSS);
		}
	}

	@Override
	public void projectDeleted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectUpdated(String arg0) {
		if(arg0 == "global") {
			structureManager.getCompiler().compile();
			log.infof("%s.projectUpdated: re-analyzing charts.",CLSS);
		}
	}
	// =================================== Initial Chart Structure ========================
	/**
	 * Wait for the initial startup flurry to pass, then compile chart structure.
	 */
	private class StructureCompilerRunner implements Runnable {
		public void run() {
			try {
				Thread.sleep(5000l);    // 5 seconds
			}
			catch(InterruptedException ignore) {}
			log.infof("%s.StructureCompilerRunner: analyzing charts...",CLSS);
			structureManager.getCompiler().compile();
			log.infof("%s.StructureCompilerRunner: done analyzing!",CLSS);
		}
	}
}
