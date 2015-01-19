/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.sfc.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.python.core.PyDictionary;

import com.ils.sfc.common.recipe.RecipeDataManager;
import com.ils.sfc.step.AbortStepFactory;
import com.ils.sfc.step.ClearQueueStepFactory;
import com.ils.sfc.step.CloseWindowStepFactory;
import com.ils.sfc.step.CollectDataStepFactory;
import com.ils.sfc.step.ControlPanelMessageStepFactory;
import com.ils.sfc.step.DeleteDelayNotificationStepFactory;
import com.ils.sfc.step.DialogMessageStepFactory;
import com.ils.sfc.step.EnableDisableStepFactory;
import com.ils.sfc.step.IlsEnclosingStepFactory;
import com.ils.sfc.step.InputStepFactory;
import com.ils.sfc.step.LimitedInputStepFactory;
import com.ils.sfc.step.PauseStepFactory;
import com.ils.sfc.step.PostDelayNotificationStepFactory;
import com.ils.sfc.step.PrintFileStepFactory;
import com.ils.sfc.step.PrintWindowStepFactory;
import com.ils.sfc.step.QueueMessageStepFactory;
import com.ils.sfc.step.RawQueryStepFactory;
import com.ils.sfc.step.ReviewDataStepFactory;
import com.ils.sfc.step.SaveDataStepFactory;
import com.ils.sfc.step.SelectInputStepFactory;
import com.ils.sfc.step.SetQueueStepFactory;
import com.ils.sfc.step.ShowQueueStepFactory;
import com.ils.sfc.step.ShowWindowStepFactory;
import com.ils.sfc.step.SimpleQueryStepFactory;
import com.ils.sfc.step.TimedDelayStepFactory;
import com.ils.sfc.step.YesNoStepFactory;
import com.ils.sfc.util.PythonCall;
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
				System.out.println("project changed " + project.getName());
				if(project.getId() == -1) { // global project					
					RecipeDataManager.loadData();
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
/*		
		long timerPeriodMillis = 30 * 1000;
 	    Timer gatewayTimer = new Timer();
 	    gatewayTimer.schedule(new TimerTask() {
			public void run() {
				doTimer();				
			}
 	    }, 0, timerPeriodMillis);
		// register the step factories:
		SfcGatewayHook sfcHook = (SfcGatewayHook) context.getModule(SFCModule.MODULE_ID);
		sfcHook.getStepRegistry().register(new QueueMessageStepFactory());
		sfcHook.getStepRegistry().register(new SetQueueStepFactory());
		sfcHook.getStepRegistry().register(new ShowQueueStepFactory());
		sfcHook.getStepRegistry().register(new ClearQueueStepFactory());
		sfcHook.getStepRegistry().register(new YesNoStepFactory());
		sfcHook.getStepRegistry().register(new AbortStepFactory());
		sfcHook.getStepRegistry().register(new PauseStepFactory());
		sfcHook.getStepRegistry().register(new ControlPanelMessageStepFactory());
		sfcHook.getStepRegistry().register(new TimedDelayStepFactory());
		sfcHook.getStepRegistry().register(new DeleteDelayNotificationStepFactory());
		sfcHook.getStepRegistry().register(new PostDelayNotificationStepFactory());
		sfcHook.getStepRegistry().register(new EnableDisableStepFactory());
		sfcHook.getStepRegistry().register(new SelectInputStepFactory());
		sfcHook.getStepRegistry().register(new LimitedInputStepFactory());
		sfcHook.getStepRegistry().register(new DialogMessageStepFactory());
		sfcHook.getStepRegistry().register(new CollectDataStepFactory());
		sfcHook.getStepRegistry().register(new InputStepFactory());
		sfcHook.getStepRegistry().register(new SimpleQueryStepFactory());
		sfcHook.getStepRegistry().register(new SaveDataStepFactory());
		sfcHook.getStepRegistry().register(new PrintFileStepFactory());
		sfcHook.getStepRegistry().register(new PrintWindowStepFactory());
		sfcHook.getStepRegistry().register(new IlsEnclosingStepFactory());
		sfcHook.getStepRegistry().register(new ShowWindowStepFactory());
		sfcHook.getStepRegistry().register(new CloseWindowStepFactory());
		sfcHook.getStepRegistry().register(new ProcedureStepFactory());
		sfcHook.getStepRegistry().register(new OperationStepFactory());
		sfcHook.getStepRegistry().register(new PhaseStepFactory());
		sfcHook.getStepRegistry().register(new ReviewDataStepFactory());
*/		
	    log.infof("%s: Startup complete.",TAG);
	}
/*
	private void doTimer() {
		if(IlsGatewayScripts.getSfcProjectName() == null) {
			return; // no context yet
		}
		ChartManager chartManager = ChartManager.get();
		List<ChartInfo> runningCharts = chartManager.getRunningCharts();
		PyDictionary payload = new PyDictionary();
		PyDictionary status = new PyDictionary();
		payload.put("status", status);
		Set<UUID> runningIds = new HashSet<UUID>();
		for(ChartInfo chartInfo: runningCharts) {
			runningIds.add(chartInfo.getInstanceId());
			PyDictionary pyChartInfo = new PyDictionary();
			statusesById.put(chartInfo.getInstanceId(), pyChartInfo);
			String chartId = chartInfo.getInstanceId().toString();
			status.put(chartId, pyChartInfo);
			pyChartInfo.put("instanceId", chartId);
			pyChartInfo.put("user", chartInfo.getStartedBy());
			pyChartInfo.put("status", chartInfo.getChartState().toString());			
			pyChartInfo.put("chartName", chartInfo.getChartPath());			
			pyChartInfo.put("startTime", chartInfo.getStartDate().toString());			
			pyChartInfo.put("project", IlsGatewayScripts.getSfcProjectName());
			pyChartInfo.put("database", IlsGatewayScripts.getSfcDatabaseName());
		}
		for(UUID chartId: statusesById.keySet()) {
			if(!runningIds.contains(chartId)) {
				// the chart is no longer running--see if we can get a status...
				Optional<ChartStatus> chartStatus = chartManager.getChartStatus(chartId, false);
				if(chartStatus.isPresent()) {
					PyDictionary lastInfo = statusesById.get(chartId);
					String state = chartStatus.get().getChartState().toString();
					lastInfo.put("status", state);
					status.put(chartId, lastInfo);
					// TODO: remove these debug messages
					System.out.println("Updated saved status for " + chartId);
				}
				else {
					System.out.println("No status for " + chartId);
				}
				// remove records for charts that are no longer running:
				statusesById.remove(chartId);
			}
		}

		try {
			PythonCall.SEND_CHART_STATUS.exec(IlsGatewayScripts.getSfcProjectName(), payload);
		} catch (JythonExecException e) {
			log.error("Could not send chart status", e);
		}
	} 	    	
*/
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
