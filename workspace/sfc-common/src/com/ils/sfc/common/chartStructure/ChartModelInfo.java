package com.ils.sfc.common.chartStructure;

import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

// A helper class that holds useful information about a chart. 
public class ChartModelInfo {
	public final ChartUIModel uiModel;
	public final ProjectResource resource;
	public final String chartPath;
	public ChartDefinition chartDefinition;
	public ChartStructure chartStructure   = null;
	
	public ChartModelInfo(ChartUIModel uiModel, ProjectResource resource,String path) {
		super();
		this.uiModel = uiModel;
		this.resource = resource;
		this.chartPath = path;
		this.chartDefinition = null;
	}
}