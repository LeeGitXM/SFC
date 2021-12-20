package com.ils.sfc.common.chartStructure;

import java.io.IOException;
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
		log.infof("Consolidated Compile");
		
		JavaToPython jToP = new JavaToPython();  
		
		HashMap<String, String> addedResources = new HashMap <String, String>();
		HashMap<String, Map<String,String>> changedResources = new HashMap <>();
		
		for (ProjectResource res:changedResourceMap.values()){
				Map<String, String> map = new HashMap<>();
				// This might change in Ignition 8 to be rres.getFolderPath()
				map.put(PATH_KEY, res.getFolderPath());
				
				byte[] chartResourceData = res.getData();
				String chartResourceAsXML;
				try {
					chartResourceAsXML = IlsSfcCommonUtils.returnResource(chartResourceData);
					map.put(CHART_XML_KEY, chartResourceAsXML);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.errorf("Caught an error in syncDatabase()");
					e.printStackTrace();
				}
				changedResources.put(String.valueOf(res.getResourceId()), map);
		}
		
		for (ProjectResource res:addedResourceMap.values()){
			String chartPath = res.getFolderPath();
			log.infof("Adding resource %d - %s", res.getResourceId(), chartPath);
			addedResources.put(String.valueOf(res.getResourceId()), chartPath);
		}

		Object[] args = {jToP.objectToPy(deletedResourceList), jToP.objectToPy(addedResources), jToP.objectToPy(changedResources), database};
		PythonCall.COMPILE_CHARTS.exec(args);
	}
	
	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public void compileResource (ProjectResource res)  throws IOException, JythonExecException, XmlParseException  {
		String chartPath;
		ProjectResourceId resourceId;		

		if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
			chartPath = res.getFolderPath();
			resourceId = res.getResourceId();
			
			log.infof("Compiling a SFC chart resource. Path: %s, Name: %s, id: %d", chartPath, res.getResourceName(), resourceId);

			byte[] chartResourceData = res.getData();
			String chartResourceAsXML=IlsSfcCommonUtils.returnResource(chartResourceData);

			//Object[] args = {chartPath, resourceId, chartResourceAsXML};
			//PythonCall.SAVE_PROJECT_START.exec( args );
		}
	}


}
