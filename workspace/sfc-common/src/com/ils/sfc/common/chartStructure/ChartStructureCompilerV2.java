package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XmlParseException;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;
import com.inductiveautomation.sfc.uimodel.ElementType;

public class ChartStructureCompilerV2 {
	private static final String CLSS = "ChartStructureCompilerV2";
	private final LoggerEx log;
	private final StepRegistry stepRegistry;
	private final Project globalProject;
	String database = null;
	
	// Constants
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String PARALLEL_STEP="Parallel";
	
	// For the beginners in the crowd, this is the constructor
	public ChartStructureCompilerV2(Project globalProject, StepRegistry stepRegistry) {
		this.stepRegistry = stepRegistry;
		this.globalProject = globalProject;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.database = "XOM";
		log.info("Initializing my very own chart structure compiler");
	}

	
	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public void compileResource (ProjectResource res)  throws IOException, JythonExecException, XmlParseException  {
		String chartPath;
		long resourceId;		

		if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
			chartPath = globalProject.getFolderPath(res.getResourceId());
			resourceId = res.getResourceId();
			
			log.infof("Compiling a SFC chart resource. Path: %s, Name: %s, id: %d", chartPath, res.getName(), resourceId);

			byte[] chartResourceData = res.getData();
			String chartResourceAsXML=IlsSfcCommonUtils.returnResource(chartResourceData);

			Object[] args = {chartPath, resourceId, chartResourceAsXML, database};
			PythonCall.UPDATE_CHART_HIERARCHY.exec( args );
		}
	}
	
		
	/** This is called when the chart is saved.  The hook in designer is called as soon as a chart is deleted, but the deleted resource is added to a list 
	 * of deleted resources to be dealt with when the project is saved.
	 */
	public void deleteChart(ProjectResource res) {
		log.infof("Deleting the chart");
		
		String chartPath = globalProject.getFolderPath(res.getResourceId());
		long resourceId = res.getResourceId();
		
		Object[] args = {resourceId, chartPath, database};
		try {
			PythonCall.DELETE_CHART.exec(args);
		} 
		catch (JythonExecException e) {
			log.errorf("%s: Error in python (delete chart): %s",CLSS,e.getLocalizedMessage());
		}
	}

}
