/**
 *   (c) 2015  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.browser.gateway;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.MockEnclosingScopeFactory;
import com.ils.sfc.common.MockInfo;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
import com.inductiveautomation.ignition.common.user.AuthenticatedUser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.SfcGatewayHookImpl.SfcRpcHandler;
import com.inductiveautomation.sfc.api.SfcGatewayHook;

/**
 *  This handler provides is a common class for handling requests
 *  from the designer or client (there is only one).
 */
public class GatewayRequestHandler {
	private final static String TAG = "GatewayRequestHandler";
	private final LoggerEx log;
	private final GatewayContext context;
	private final ClientReqSession session;
	private final Long projectId;

	/**
	 * Constructor.
	 */
	public GatewayRequestHandler(GatewayContext ctx,ClientReqSession sess,Long proj) {
		this.context = ctx;
		this.session = sess;
		this.projectId = proj;
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/**
	 * Start the chart after assembling the chart and recipe data in its lineage.
	 * 
	 * @param chartPath path to the chart as shown in the Designer Nav tree.
	 * @return unique id for the new chart instance
	 */
	public UUID startChart(String chartPath) {
		SfcGatewayHook sfcHook = (SfcGatewayHook)(context.getModule(SFCModule.MODULE_ID));
		SfcRpcHandler rpcHandler = (SfcRpcHandler)sfcHook.getRPCHandler(session, projectId);
		UUID instance = null;
		try {
			Map<String,Object> parameters = new HashMap<>();
			Map<String,Object> initialParameters = createInitialParameters();
			// Create a mock enclosing scope
			MockEnclosingScopeFactory factory = new MockEnclosingScopeFactory(initialParameters);
			ChartStructureManager structureManager = ((SfcBrowserGatewayHook)(context.getModule(GatewayBrowserConstants.MODULE_ID))).getChartStructureManager();
			Stack<MockInfo> stack = structureManager.getCompiler().getAncestors(chartPath);
			// Pop the stack and enhance the map
			while( stack!=null && !stack.isEmpty() ) {
				factory.addLevelBottomUp(stack.pop());
			}
						
			instance =  rpcHandler.startChart(chartPath, factory.getInitialChartParams());
		}
		catch( Exception ex ) {
			log.warnf("%s.startChart: Failed to start %s (%s)",TAG,chartPath,ex.getMessage());
		}
		return instance;
	}
	
	private Map<String,Object> createInitialParameters() {
		Map<String,Object> parameters = new HashMap<>();
		Map<String,Object> map = new HashMap<>();
		map.put(Constants.ISOLATION_MODE,Boolean.FALSE);
		map.put(Constants.PROJECT,context.getProjectManager().getProjectName(projectId, ProjectVersion.Published));
		try {
			AuthenticatedUser user = (AuthenticatedUser)GatewayConnectionManager.getInstance().getGatewayInterface().invoke("Users.getCurrentUser", new Serializable[0]);
			map.put(Constants.USER,user.getUsername());
		}
		catch(Exception ex) {
			log.infof("%s.initializeMap: Failed to obtain user name (%s)",TAG,ex.getMessage());
		}
		return parameters;
	}
	
}

