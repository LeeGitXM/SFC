package com.ils.sfc.common.chartStructure;

import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/**
 *  A helper class that holds useful information about a chart. 
 *  When this class is first instantiated it creates a logger
 *  associated with the chart.
 */
public class ChartModelInfo {
	private static final LoggerEx log = LogUtil.getLogger(ChartModelInfo.class.getPackage().getName());
	private static final String TAG = "ChartModelInfo";
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
		initializeLogger(chartPath);
	}
	
	/**
	 * Use the chart path as the logger name, except replace "/"
	 * with "." to make it more "pythony".
	 * @param path
	 */
	private void initializeLogger(String path) {
		// Use the chart-specific logger to announce its creation
		String logpath = path.replaceAll("/", ".");
		LoggerEx logger = LogUtil.getLogger(logpath);
		logger.debugf("%s.initilize: Created chart resource and logger  %s",TAG,path);
		log.debugf("%s.initilize: Created chart resource  %s",TAG,path);
	}
}