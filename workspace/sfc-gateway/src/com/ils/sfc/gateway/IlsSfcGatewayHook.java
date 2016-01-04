/**
 *   (c) 2014-2015  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import system.ils.sfc.common.Constants;

import com.ils.common.persistence.ToolkitRecord;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.ils.sfc.gateway.monitor.IlsStepMonitor;
import com.ils.sfc.gateway.recipe.IlsScopeLocator;
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
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectListener;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.ChartManagerService;
import com.inductiveautomation.sfc.api.SfcGatewayHook;
import com.inductiveautomation.sfc.api.elements.StepFactory;

//import com.inductiveautomation.sfc.ChartManager;

//import com.ils.sfc.step.IlsSfcIO;

/**
 * This is root node for specialty code dealing with the gateway. On startup
 * we obtain the gateway context. It serves as our entry point into the
 * Ignition core.
 */
public class IlsSfcGatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer,ProjectListener {
	public static String TAG = "SFCGatewayHook";
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
	//private IlsSfcSessionMgr sessionMgr;
	private ChartDebugger chartDebugger = null;
	
	private static StepFactory[] stepFactories = {
		new QueueMessageStepFactory(),
		new SaveQueueStepFactory(),
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
		new ConfirmControllersStepFactory(),
		new WriteOutputStepFactory(),
		new PVMonitorStepFactory(),
		new MonitorDownloadStepFactory(),
		new ManualDataEntryStepFactory(),
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

	public ChartDebugger getChartDebugger() {
		return chartDebugger;
	}

	public IlsDropBox getDropBox() {
		return dropBox;
	}

	public IlsStepMonitor getStepMonitor() {
		return stepMonitor;
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
		
		//sessionMgr = new IlsSfcSessionMgr(context.getMessageDispatchManager());
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
		manager.addStaticFields("system.ils.sfc.common.Constants", Constants.class);	
	};
	
	private void initializeUnits() {
		// Because there are apparently a lot of different script managers in the gateway,
		// we have to ensure that the one we use for calling into Python has the units
		// initialized. There is potentially a conflict between the units in the production
		// database and the isolation database, but since we only have one PythonCall singleton
		// we aren't set up to handle that anyway. We choose to use the units from the 
		// production database.
		String databaseName =  requestHandler.getDatabaseName(false);
		log.infof("%s:initializeUnits: Database name is %s", TAG,databaseName);
		Object[] args = {databaseName};
		try {
			PythonCall.INITIALIZE_UNITS.exec(args);
		} 
		catch (JythonExecException jee) {
			log.error("IlsSfcGatewayHook.initializeUnits: Error in PythonCall script manager", jee);
		}
		catch (Exception ex) {
			log.error("IlsSfcGatewayHook.initializeUnits: Error executing PythonCall script manager", ex);
		}
	}

	@Override
	public void startup(LicenseState licenseState) {			
		iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);				
		chartManager.addChartObserver(dropBox);

 		structureManager = new ChartStructureManager(context.getProjectManager().getGlobalProject(ApplicationScope.GATEWAY),iaSfcHook.getStepRegistry());
 		requestHandler = new GatewayRequestHandler(context,structureManager,requestResponseManager);
		dispatcher = new GatewayRpcDispatcher(context,requestHandler);
		chartDebugger = new ChartDebugger(
			context.getProjectManager().getGlobalProject(ApplicationScope.GATEWAY), 
			iaSfcHook.getStepRegistry());
		initializeUnits();
		IlsGatewayScripts.setRequestHandler(requestHandler);
		RecipeDataAccess.setRequestHandler(requestHandler);
		stepMonitor = new IlsStepMonitor(structureManager,chartManager);
    	context.getProjectManager().addProjectListener(this);
		log.infof("%s: Startup complete.",TAG);
	}
	
	@Override
	public void shutdown() {
		context.getProjectManager().removeProjectListener(this);
		stepMonitor.shutdown();
	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == ChartManagerService.class) {
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
		chartManager.unregisterScopeLocator(scopeLocator);
		for(StepFactory stepFactory: stepFactories) {
			chartManager.unregister(stepFactory);
		}
		chartManager.removeChartObserver(chartObserver);
		//chartManager.removeChartObserver(sessionMgr);
		chartManager = null;
		stepMonitor.shutdown();
	}

	public static Map<String, List<String>> getPropertyNamesById() {
		return propertyNamesById;
	}
	// =================================== Project Listener ========================
	@Override
	public void projectAdded(Project proj1, Project proj2) {
	}

	@Override
	public void projectDeleted(long projectId) {
	}

	@Override
	public void projectUpdated(Project proj, ProjectVersion vers) {
		structureManager.getCompiler().compile();
	}
}
