package com.ils.sfc.common.chartStructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.common.JavaToPython;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XmlParseException;

public class ChartStructureCompilerV2 {
	private static final String CLSS = "ChartStructureCompilerV2";
	private final LoggerEx log;
	private final StepRegistry stepRegistry;
	private final Project globalProject;
	String database = null;
	
	// Constants
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String PARALLEL_STEP="Parallel";
	public static final String PATH_KEY="chartPath";
	public static final String CHART_XML_KEY="chartXml";
	
	// For the beginners in the crowd, this is the constructor
	public ChartStructureCompilerV2(Project globalProject, StepRegistry stepRegistry) {
		this.stepRegistry = stepRegistry;
		this.globalProject = globalProject;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		log.info("Initializing my very own chart structure compiler");
	}
	
	
	/** This is called when the project is saved.  The hook in designer is called as soon as a chart is deleted, but the deleted resource is added to a list 
	 * of deleted resources to be dealt with when the project is saved.  I went to a fair amount of work here to make a hashmap that I could use over on the 
	 * Python side, Python doesn't have access to the Resource class, so a HashMap with a Resource is useless.  Specifically, I wanted to include the chartPath.  
	 * Unfortunately, since the chart has been deleted, it doesn't have a chartPath.  I tried capturing the chartPath at the time the resource was deleted but
	 * it wasn't available then either.  I'll leave everything in place to pass the chartPath in case someday it is available.  
	 */
	public void syncDatabase(List <String> deletedResourceList,
			Map <Long, ProjectResource> addedResourceMap,
			Map <Long, ProjectResource> changedResourceMap) throws JythonExecException {
		
		log.infof("In syncDatabase()");
		
		JavaToPython jToP = new JavaToPython();  
		
		HashMap<String, String> addedResources = new HashMap <String, String>();
		HashMap<String, Map<String,String>> changedResources = new HashMap <>();
		
		log.infof("...preparing changed resources...");
		for (ProjectResource res:changedResourceMap.values()){
			log.infof("...processing chart: %s...", res.getFolderPath());
			log.infof("    as a string: %s", res.toString());
			log.infof("    project: %s",  res.getProjectName());
			log.infof("    resource id: %s", res.getResourceId());
			log.infof("    data keys: %s", res.getDataKeys().toString());
			
			Map<String, String> map = new HashMap<>();
			map.put(PATH_KEY, res.getFolderPath());
			
			log.infof("Getting <sfc.xml> chart data...");
			byte[] chartResourceData = res.getData("sfc.xml");
			log.infof("Fetched %d bytes", chartResourceData.length);

			String chartResourceAsXML = new String(chartResourceData, StandardCharsets.UTF_8);
			log.infof("  as string (converted): %s", chartResourceAsXML);
			map.put(CHART_XML_KEY, chartResourceAsXML);
			
			changedResources.put(String.valueOf(res.getResourceId().hashCode()), map);
		}
		
		log.infof("...preparing added resources...");
		for (ProjectResource res:addedResourceMap.values()){
			String chartPath = res.getFolderPath();
			log.infof("Adding resource %d - %s", res.getResourceId().hashCode(), chartPath);
			addedResources.put(String.valueOf(res.getResourceId().hashCode()), chartPath);
		}

		log.infof("...converting arguments for Python...");
		Object[] args = {jToP.objectToPy(deletedResourceList), jToP.objectToPy(addedResources), jToP.objectToPy(changedResources), database};
		log.infof("...calling Python...");
		PythonCall.COMPILE_CHARTS.exec(args);
		log.infof("...done!");
	}

}
