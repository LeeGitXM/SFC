package com.ils.sfc.browser.execute;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.json.JSONObject;

import prefuse.data.Table;

import com.ils.sfc.browser.BrowserConstants;
import com.ils.sfc.browser.ChartTreeDataModel;
import com.ils.sfc.browser.SfcBrowserRequestHandler;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.user.AuthenticatedUser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.designer.workspace.SFCDesignableContainer;
import com.inductiveautomation.sfc.designer.workspace.SFCWorkspace;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.scripting.SfcScriptingFunctions;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ChartRunner implements Runnable {
	private final static String TAG = "ChartRunner";
	private final DesignerContext context;
	private final LoggerEx log;
	private final ChartTreeDataModel dataModel;
	private final SfcBrowserRequestHandler requestHandler;
	private final SFCWorkspace workspace;
	
	/**
	 * Constructor
	 */
	public ChartRunner(DesignerContext ctx,SFCWorkspace wksp,ChartTreeDataModel data) {
		this.context = ctx;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.dataModel = data;
		this.requestHandler = new SfcBrowserRequestHandler();
		this.workspace = wksp;
	}
	
	// ============================= Runnable ========================
	public void run() {
		SFCDesignableContainer tab = workspace.getSelectedContainer();
		if( tab!=null ) {
			long resourceId = workspace.getSelectedContainer().getResourceId();
			String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
			
			try {
				UUID instance = requestHandler.startChart(chartPath);
				tab.startMonitoring(instance);
			}
			catch(Exception ex) {
				log.infof("%s.run: Failed to start chart %s (%s)",TAG,chartPath,ex.getMessage());
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
