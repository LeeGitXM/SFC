package com.ils.sfc.common.chartStructure;

import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;

/** Basically a container to hold structure info for all the SFC charts, 
 *  plus any utility methods that need to go across charts. The compiler
 *  holds all the various maps.
 *
 */

public class ChartStructureManager   {

	private final ChartStructureCompiler compiler;
	private final Project project;
	
	/**
	 * Create a new structure manager. The compiler contains all the various maps
	 * that we use to make useful methods.
	 * @param ctx
	 */
	public ChartStructureManager(Project proj,StepRegistry registry) {
		this.project = proj;
		this.compiler = new ChartStructureCompiler(project,registry);
		compiler.compile();
	}
	
	public ChartStructureCompiler getCompiler() {
		return compiler;
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
	
	public String getChartPath(long resourceId) { return project.getFolderPath(resourceId); }
	/**
	 * Remove the final segment of a chart path to get its parent folder. If there is no parent
	 * return a "/". If the resource doesn't exist, return a null.
	 * @param resourceId
	 * @return the path to the chart's enclosing folder in the NavTree. 
	 */
	public String getParentPath(long resourceId) { 
		String chartPath = project.getFolderPath(resourceId);
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
		if( stepStruct!=null ) name = stepStruct.getName();
		return name;
	}
	public ElementDefinition.ElementType getStepType(String stepId) {
		ElementDefinition.ElementType type = null;
		StepStructure stepStruct = compiler.getStepInformation(stepId);
		if( stepStruct!=null ) type = stepStruct.getElementType();
		return type;
	}

}
