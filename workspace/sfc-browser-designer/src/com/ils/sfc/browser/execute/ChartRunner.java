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
			
			SfcScriptingFunctions fncs = workspace.getRPC();
			//Map<String,Object> map = createMapFromLineage(resourceId);
			try {
				UUID instance = requestHandler.startChart(chartPath);
				tab.startMonitoring(instance);
			}
			catch(Exception ex) {
				log.infof("%s.run: Failed to start chart %s (%s)",TAG,chartPath,ex.getMessage());
			}
		}
	}
	/*
	private Map<String,Object> createMapFromLineage(long resourceId) {
		Map<String,Object> map = initializeMap();
		Map<Long,ChartDefinition> definitions = dataModel.getDefinitions();
		Map<Integer,Integer> lineage = dataModel.getLineage();
		Table nodes = dataModel.getNodes();
		int row = getIndexForResource(resourceId,nodes);
		if( row>=0 ) {
			// Create a stack of our lineage
			Integer parent = lineage.get(new Integer(row));
			Stack<Integer> stack = new Stack<>();
			stack.push(new Integer(row));  // Self is bottom of stack
			map.put("chartName",nodes.getString(row, BrowserConstants.PATH));
			while( parent!=null ) {
				stack.push(parent);
				parent = lineage.get(parent);
			}
			// Pop the stack and enhance the map
			while( !stack.isEmpty() ) {
				Integer nodeIndex = stack.pop();
				enhanceMapFromNode(map,nodeIndex.intValue(),nodes,definitions);
			}
		}
		else {
			log.infof("%s.createMapFromLineage: Chart resource %d not found in nodelist", TAG,resourceId);
		}
		
		return map;
	}
	
	private void enhanceMapFromNode(Map<String,Object>map,int row,Table nodes,Map<Long,ChartDefinition> definitions) {
		// Skip nodes that are enclosures, not charts
		long resid = nodes.getLong(row, BrowserConstants.RESOURCE);
		if( resid<0 ) return;
		String chartPath = nodes.getString(row, BrowserConstants.PATH);
		log.infof("%s.enhanceMapFromNode: Operating on %s", TAG,chartPath);
		ChartDefinition chartDef = definitions.get(new Long(resid));
		if( chartDef!=null ) {
			Iterator<ChartUIElement> walker =  chartDef.getModel().getChartElements().iterator();
			while(walker.hasNext()) {
				ChartUIElement element = walker.next();
				for(PropertyValue<?> pv: element.getValues() ) {
					if(pv.getProperty().equals(ChartStepProperties.AssociatedData)) {
						JSONObject associatedData = element.getOrDefault(ChartStepProperties.AssociatedData);
						try {
							log.infof("%s.enhanceMapFromNode: Associated data for %s = %s", TAG,chartPath,associatedData.toString());
							List<Data> recipeData = Data.fromAssociatedData(associatedData);
							String stepPath = chartPath+"/"+ "??";
							for(Data data: recipeData) {
								data.setStepPath(stepPath);
							}
							//setRecipeData(recipeData);
						} 
						catch (Exception ex) {
							log.infof("%s.enhanceMapFromNode: Exception processing recipe data for %s (%s)", TAG,chartPath,ex.getMessage());
						}
					}
					else {
						if( pv.getValue()!=null ) {
							log.infof("%s.enhanceMapFromNode: Add %s = %s", TAG,pv.getProperty().getName(),pv.getValue().toString());
							map.put(pv.getProperty().getName(), pv.getValue());
						}
						else {
							log.infof("%s.enhanceMapFromNode: Skip %s (null)", TAG,pv.getProperty().getName());
						}
					}
				}
			}
		}
		else {
			log.infof("%s.enhanceMapFromNode: Chart definition for %s (%d) not found", TAG,chartPath,resid);
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
