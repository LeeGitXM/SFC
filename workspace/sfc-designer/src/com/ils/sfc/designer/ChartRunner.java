package com.ils.sfc.designer;

import java.io.Serializable;
import java.util.UUID;

import com.ils.sfc.designer.browser.SfcBrowserRequestHandler;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.user.AuthenticatedUser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.workspace.SFCDesignableContainer;
import com.inductiveautomation.sfc.designer.workspace.SFCWorkspace;

public class ChartRunner implements Runnable {
	private final static String TAG = "ChartRunner";
	private final DesignerContext context;
	private final boolean isolationMode;
	private final LoggerEx log;
	private final SfcBrowserRequestHandler requestHandler;
	private final SFCWorkspace workspace;
	
	/**
	 * Constructor
	 */
	public ChartRunner(DesignerContext ctx,SFCWorkspace wksp,boolean isIsolation) {
		this.context = ctx;
		this.isolationMode = isIsolation;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.requestHandler = new SfcBrowserRequestHandler();
		this.workspace = wksp;
	}
	
	// ============================= Runnable ========================
	public void run() {
		SFCDesignableContainer tab = workspace.getSelectedContainer();
		if( tab!=null ) {
			long resourceId = workspace.getSelectedContainer().getResourceId();
			String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
			String clientProject = context.getProject().getName();
			String username = "UNDEFINED";
			try {
				AuthenticatedUser authUser = (AuthenticatedUser)GatewayConnectionManager.getInstance().getGatewayInterface().invoke("Users.getCurrentUser", new Serializable[0]);
				username = authUser.getUsername();
			}
			catch(Exception ex) {
				log.infof("%s.initializeMap: Failed to obtain user name (%s)",TAG,ex.getMessage());
			}
			try {
				UUID instanceId = requestHandler.startChart(chartPath,clientProject,username,isolationMode);  // True for isolation mode
				tab.startMonitoring(instanceId);
			}
			catch(Exception ex) {
				log.infof("%s.run: Exception starting chart %s (%s)",TAG,chartPath,ex.getMessage());
			}
		}
	}
	
	
	/**
	 * Loop through the nodelist looking for the indicated resource. Once found, return
	 * the index. If not found, return -1;
	 * @param res
	 * @param nodes
	 * @return

	private int getIndexForResource(long resid,Table nodes) {
		int rows = nodes.getRowCount();
		int row = -1;
		int index = 0;
		while( index<rows ) {
			long id = nodes.getLong(index, BrowserConstants.RESOURCE);
			if( id==resid) {
				row = index;
				break;
			}
			index++;
		}
		return row;
	}
	
	/**
	 * Set attributes in the initial parameters.
	 * @return an initial version of the chart parameters.

	private Map<String,Object> initializeMap() {
		Map<String,Object> map = new HashMap<>();
		map.put("isolationMode",new Boolean(Boolean.FALSE));
		map.put("project",context.getProject().getName());
		try {
			AuthenticatedUser user = (AuthenticatedUser)GatewayConnectionManager.getInstance().getGatewayInterface().invoke("Users.getCurrentUser", new Serializable[0]);
			map.put("user",user.getUsername());
		}
		catch(Exception ex) {
			log.infof("%s.initializeMap: Failed to obtain user name (%s)",TAG,ex.getMessage());
		}
		return map;
	}
	*/
}
