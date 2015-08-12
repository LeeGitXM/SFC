package com.ils.sfc.common.chartStructure;

import com.ils.sfc.common.chartStructure.ChartStructureCompiler.ChartModelInfo;
import com.ils.sfc.common.chartStructure.ChartStructureCompiler.StepStructure;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectChangeListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;

/** Basically a container to hold structure info for all the SFC charts, 
 *  plus any utility methods that need to go across charts. The compiler
 *  holds all the various maps.
 *
 */
public class ChartStructureManager implements ProjectChangeListener {
	private static String TAG = "ChartStructureManager";
	private final LoggerEx log;
	private final ChartStructureCompiler compiler;
	private final DesignerContext context;
	
	/**
	 * Create a new structure manager. The compiler contains all the various maps
	 * that we use to make useful methods.
	 * @param ctx
	 */
	public ChartStructureManager(DesignerContext ctx,ClientStepRegistry registry) {
		this.context = ctx;
		this.compiler = new ChartStructureCompiler(context,registry);
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		compiler.compile();
	}
	
	/**
	 * @param resid resource Id
	 * @return a compiled chart definition. If the lookup fails return NULL.
	 */
	public ChartDefinition getChartDefinition(long resourceId) {
		ChartDefinition def = null;
		ChartModelInfo info = compiler.getChartInformation(resourceId);
		if( info!=null ) def = info.chartDefinition;
		return def;
	}
	
	public String getChartPath(long resourceId) { return context.getGlobalProject().getProject().getFolderPath(resourceId); }
	/**
	 * Remove the final segment of a chart path to get its parent folder. If there is no parent
	 * return a "/". If the resource doesn't exist, return a null.
	 * @param resourceId
	 * @return the path to the chart's enclosing folder in the NavTree. 
	 */
	public String getParentPath(long resourceId) { 
		String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
		if( chartPath!=null ) {
			int pos = chartPath.lastIndexOf("/");
			if( pos>0 )  chartPath = chartPath.substring(0, pos);
			else chartPath = "/";
		}
		return chartPath;
	}
	public String getStepName(String stepId) {
		String name = "UNKNOWN";
		StepStructure stepStruct = compiler.getStepInformation(stepId);
		if( stepStruct!=null ) name = stepStruct.name;
		return name;
	}
	// =================================== Project Change Listener ========================
	// No matter what the change is, we re-compute the maps
	@Override
	public void projectResourceModified(ProjectResource arg0,ResourceModification arg1) {
		compiler.compile();
		
	}

	@Override
	public void projectUpdated(Project arg0) {
		compiler.compile();
	}
	
	
	
	
	/*
	// Looking across all charts, find the step with the given id.
	public IlsSfcStepStructure getStepWithId(String id) {
		IlsSfcStepStructure result = null;
		for(IlsSfcChartStructure chart: chartsByName.values()) {
			if((result = chart.findStepWithId(id)) != null) {
				return result;
			}
		}
		logger.error("Couldn't find step with id " + id);
		return null;
	}

	// Looking across all charts, find the step with the given id.
	public IlsSfcStepStructure getStepWithFactoryId(String factoryId) {
		IlsSfcStepStructure result = null;
		for(IlsSfcChartStructure chart: chartsByName.values()) {
			if((result = chart.findStepWithFactoryId(factoryId)) != null) {
				return result;
			}
		}
		return null;
	}
	*/
}
