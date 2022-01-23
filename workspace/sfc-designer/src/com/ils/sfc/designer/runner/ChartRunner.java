package com.ils.sfc.designer.runner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.OperationStepProperties;
import com.ils.sfc.common.step.PhaseStepProperties;
import com.ils.sfc.common.step.ProcedureStepProperties;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayConnectionManager;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.user.AuthenticatedUser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.client.scripting.ClientScriptingFunctions;
import com.inductiveautomation.sfc.designer.workspace.SfcDesignableContainer;
import com.inductiveautomation.sfc.designer.workspace.SfcWorkspace;

import system.ils.sfc.common.Constants;
public class ChartRunner implements Runnable {
	private final static String CLSS = "ChartRunner";
	private final String projectName;
	private final DesignerContext context;
	private final boolean isolationMode;
	private final LoggerEx log;
	private final SfcBrowserRequestHandler requestHandler;
	private final SfcWorkspace workspace;
	private Map <String, Object> parameters;
	
	/**
	 * Constructor
	 */
	public ChartRunner(DesignerContext ctx, SfcWorkspace wksp, String pName, boolean isIsolation) {
		this.context = ctx;
		this.projectName = pName;
		this.isolationMode = isIsolation;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.requestHandler = new SfcBrowserRequestHandler();
		this.workspace = wksp;
		this.parameters = new HashMap<>();
	}
		
	
	// ============================= Runnable ========================
	public void run() {
		SfcDesignableContainer tab = workspace.getSelectedContainer();
		if( tab!=null ) {
			String chartPath = workspace.getSelectedContainer().getResourcePath().getFolderPath();
			log.infof("%s:running %s in project %s (Isolation: %s)...", CLSS, chartPath, projectName, isolationMode);
			
			ClientScriptingFunctions scriptingFunctions = new ClientScriptingFunctions();
			
			log.infof("Chart parameters (before): %s", parameters.toString());
			getInitialChartParams(chartPath);
			log.infof("Chart parameters (after): %s", parameters.toString());
			
			String username = "UNDEFINED";
			try {
				AuthenticatedUser authUser = (AuthenticatedUser)GatewayConnectionManager.getInstance().getGatewayInterface().invoke("Users.getCurrentUser", new Serializable[0]);
				username = authUser.getUsername();
			}
			catch(Exception ex) {
				log.infof("%s.run: Failed to obtain user name (%s)",CLSS,ex.getMessage());
			}
			try {
				log.infof("%s.run: Starting the chart...", CLSS);
				
				UUID instanceId = scriptingFunctions.startChart(chartPath, parameters);
				//UUID instanceId = requestHandler.startChart(chartPath, clientProject, username, isolationMode);
				tab.startMonitoring(instanceId);
			}
			catch(Exception ex) {
				log.infof("%s.run: Exception starting chart %s (%s)",CLSS,chartPath,ex.getMessage());
			}
		}
	}

	
	public void getInitialChartParams(String chartPath) {
		log.infof("%s:getInitialChartParams()...", CLSS);
				
		String parentChartPath;
		String parentStepName;
		String parentStepUUID;
		String stepType;
		String factoryId;
		Dataset ds;
		String tagProvider = "";
		String database = "";
		float timeFactor = Float.parseFloat("1.0");
		

		// add the normal expected properties at the top level
		Map<String,Object> initialParameters = new HashMap<String,Object>();
		initialParameters.put(Constants.PROJECT, projectName);
		initialParameters.put(Constants.ISOLATION_MODE, isolationMode);
		initialParameters.put(Constants.CONTROL_PANEL_ID, Constants.CONTROL_PANEL_SCRATCH_ID);    // For the "scratch" panel
		initialParameters.put(Constants.USER, "AdminUser");
		
		initialParameters.put("startTime", new java.util.Date());
		
		PyChartScope childScope = new PyChartScope();
		PyChartScope lowestChildScope = childScope;
		PyChartScope parentScope = null;
		
		try {
			log.infof("%s:Calling Python to get the system interfaces...", CLSS);
			String[] interfaces = PythonCall.toArray(PythonCall.GET_PROJECT_INTERFACES.exec(projectName, isolationMode));
			log.infof("%s:...back from Python with %s!", CLSS, interfaces.toString());

			tagProvider = interfaces[0];
			database = interfaces[1];
			timeFactor = Float.parseFloat(interfaces[2]);
			
			log.infof("   Tag Provider: %s", tagProvider);
			log.infof("   Database:     %s", database);
			log.infof("   Time Factor:  %f", timeFactor);
		} 
		catch (JythonExecException jee) {
			log.errorf("%s: Error getting the enclosing charts for %s (%s)", CLSS, chartPath, jee.getMessage());
		}
		catch (Exception ex) {
			log.errorf("%s: Exception getting the enclosing charts for %s (%s)", CLSS, chartPath, ex.getMessage());
		}
		
		initialParameters.put(Constants.TAG_PROVIDER, tagProvider);
		initialParameters.put(Constants.DATABASE, database);
		initialParameters.put(Constants.TIME_FACTOR, timeFactor);
		
		try {
			log.infof("%s:Calling Python...", CLSS);
			ds = (Dataset)PythonCall.GET_ENCLOSING_CHARTS.exec(chartPath, isolationMode);
			log.infof("%s:...back from Python!", CLSS);

			int row=0;
			while(row < ds.getRowCount()) {
				// First column in the dataset is the name
				parentChartPath = ds.getValueAt(row, 0).toString();
				parentStepName = ds.getValueAt(row, 1).toString();
				parentStepUUID = ds.getValueAt(row, 2).toString();
				stepType = ds.getValueAt(row, 3).toString();
				factoryId = ds.getValueAt(row, 4).toString();
				log.infof("%s:Enclosing Chart: %s - %s - %s - %s - %s)...", CLSS, parentChartPath, parentStepName, parentStepUUID, stepType, factoryId);
					
				parentScope = new PyChartScope();
				childScope.put("parent", parentScope);
				String runId = UUID.randomUUID().toString();
				parentScope.put(Constants.INSTANCE_ID, runId);
				parentScope.put("chartPath", parentChartPath);
				parentScope.put("startTime", "mock");
				
				// Technically these are only needed at the top scope, which becomes the innermost dictionary, but it doesn't hurt to have it everywhere
				parentScope.put(Constants.PROJECT, projectName);
				parentScope.put(Constants.ISOLATION_MODE, isolationMode);
				parentScope.put(Constants.CONTROL_PANEL_ID, Constants.CONTROL_PANEL_SCRATCH_ID);    // For the "scratch" panel
				parentScope.put(Constants.USER, "AdminUser");
				parentScope.put(Constants.TAG_PROVIDER, tagProvider);
				parentScope.put(Constants.DATABASE, database);
				parentScope.put(Constants.TIME_FACTOR, timeFactor);
				
				PyChartScope enclosingStepScope = new PyChartScope();
				enclosingStepScope.put("name", parentStepName);
				// Pete added step UUID  
				enclosingStepScope.put("id", parentStepUUID);
				if(factoryId.equals(ProcedureStepProperties.FACTORY_ID)) {
					enclosingStepScope.put(Constants.S88_LEVEL, Constants.GLOBAL);
				}
				else if(factoryId.equals(OperationStepProperties.FACTORY_ID)) {
					enclosingStepScope.put(Constants.S88_LEVEL, Constants.OPERATION);
				}
				else if(factoryId.equals(PhaseStepProperties.FACTORY_ID)) {
					enclosingStepScope.put(Constants.S88_LEVEL, Constants.PHASE);
				}
				childScope.put(Constants.ENCLOSING_STEP_SCOPE_KEY, enclosingStepScope);
				childScope = parentScope;
					
				row++;
			}
				
			if( parentScope!=null ) {
				// copy expected top-level params to top-level mock parent:
				parentScope.put(Constants.PROJECT, initialParameters.get(Constants.PROJECT));
				parentScope.put(Constants.USER, initialParameters.get(Constants.USER));
				parentScope.put(Constants.ISOLATION_MODE, initialParameters.get(Constants.ISOLATION_MODE));
				parentScope.put(Constants.MESSAGE_QUEUE, initialParameters.get(Constants.DEFAULT_MESSAGE_QUEUE));
			}
				
			// copy lowest child scope info to initial Params
			initialParameters.putAll(lowestChildScope);	
			parameters = initialParameters;
		} 
		catch (JythonExecException jee) {
			log.errorf("%s: Error getting the enclosing charts for %s (%s)", CLSS, chartPath, jee.getMessage());
		}
		catch (Exception ex) {
			log.errorf("%s: Exception getting the enclosing charts for %s (%s)", CLSS, chartPath, ex.getMessage());
		}
	}
		/*
		 * 
		 
		String[] pyPaths = null;
		pyPaths = PythonCall.toArray(PythonCall.GET_ENCLOSING_CHARTS.exec(chartPath, isolationMode));
		
		PyChartScope childScope = new PyChartScope();
		PyChartScope lowestChildScope = childScope;
		PyChartScope parentScope = null;
		for(EnclosureInfo parentInfo: enclosureHierarchyBottomUp) {
			parentScope = new PyChartScope();
			childScope.put("parent", parentScope);
			String runId = UUID.randomUUID().toString();
			parentScope.put(Constants.INSTANCE_ID, runId);
			parentScope.put("chartPath", parentInfo.parentChartPath);
			parentScope.put("startTime", "mock");
			PyChartScope enclosingStepScope = new PyChartScope();
			enclosingStepScope.put("name", parentInfo.parentStepName);
			// Pete added step UUID
			enclosingStepScope.put("id", parentInfo.parentElement.getId().toString());
			if(parentInfo.parentStepFactoryId.equals(ProcedureStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.GLOBAL);
			}
			else if(parentInfo.parentStepFactoryId.equals(OperationStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.OPERATION);
			}
			else if(parentInfo.parentStepFactoryId.equals(PhaseStepProperties.FACTORY_ID)) {
				enclosingStepScope.put(Constants.S88_LEVEL, Constants.PHASE);
			}
			childScope.put(Constants.ENCLOSING_STEP_SCOPE_KEY, enclosingStepScope);
			childScope = parentScope;
		}
		
		if( parentScope!=null ) {
			// copy expected top-level params to top-level mock parent:
			parentScope.put(Constants.PROJECT, initialParams.get(Constants.PROJECT));
			parentScope.put(Constants.USER, initialParams.get(Constants.USER));
			parentScope.put(Constants.ISOLATION_MODE, initialParams.get(Constants.ISOLATION_MODE));
			parentScope.put(Constants.MESSAGE_QUEUE, initialParams.get(Constants.DEFAULT_MESSAGE_QUEUE));
		}
		
		// copy lowest child scope info to initial Params
		initialParams.putAll(lowestChildScope);
		return initialParams;
	}
	*/
	
	
	
	
	
	
	
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
			log.infof("%s.initializeMap: Failed to obtain user name (%s)",CLSS,ex.getMessage());
		}
		return map;
	}
	*/
}
