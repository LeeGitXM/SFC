/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.gateway;

import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectListener;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.SfcGatewayHook;



/**
 * The sole purpose for the gateway component of the browser is to pass scripted
 * commands to the ILS-SFC module's common code. The ils-sfc-comman jar is 
 * included in this module in order to avoid a module dependency.
 * @author chuckc
 *
 */
public class SfcBrowserGatewayHook extends AbstractGatewayModuleHook implements ProjectListener  {
	private transient GatewayContext context = null;
	private ChartStructureManager structureManager = null;

	@Override
	public Object getRPCHandler(ClientReqSession session, Long projectId) {
		return new BrowserRpcDispatcher(context,session,projectId);
	}


	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
	}


	@Override
	public void shutdown() {
		context.getProjectManager().removeProjectListener(this);
	}


	@Override
	public void startup(LicenseState state) {
		SfcGatewayHook iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);
		// Provide a central repository for the structure of the charts
    	structureManager = new ChartStructureManager(context.getProjectManager().getGlobalProject(ApplicationScope.GATEWAY),iaSfcHook.getStepRegistry());
    	context.getProjectManager().addProjectListener(this);
	}
	
	public ChartStructureManager getChartStructureManager() { return structureManager; }


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
