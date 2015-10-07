/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.gateway;

import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.SfcGatewayHook;



/**
 * The sole purpose for the gateway component of the browser is to pass scripted
 * commands to the ILS-SFC module's common code. The ils-sfc-comman jar is 
 * included in this module in order to avoid a module dependency.
 * @author chuckc
 *
 */
public class SfcBrowserGatewayHook extends AbstractGatewayModuleHook  {
	private transient GatewayContext context = null;
	private ChartStructureManager structureManager = null;

	@Override
	public Object getRPCHandler(ClientReqSession session, Long projectId) {
		return new BrowserRpcDispatcher(context,session,projectId);
	}


	@Override
	public void setup(GatewayContext ctxt) {
		this.context = ctxt;
		SfcGatewayHook iaSfcHook = (SfcGatewayHook)context.getModule(SFCModule.MODULE_ID);
		// Provide a central repository for the structure of the charts
    	structureManager = new ChartStructureManager(context.getProjectManager().getGlobalProject(ApplicationScope.GATEWAY),iaSfcHook.getStepRegistry());
    	context.getProjectManager().addProjectListener(structureManager);
	}


	@Override
	public void shutdown() {	
	}


	@Override
	public void startup(LicenseState state) {
	}
}
