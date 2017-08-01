package com.ils.sfc.common.chartStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.python.antlr.PythonParser.elif_clause_return;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XMLParseException;
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
	public void compileResource(ProjectResource res) {
		boolean success = true;
		String chartPath;
		long resourceId;
		
		// These lists are used for the chart Hierarchy
		ArrayList<String> childPaths = new ArrayList<String>();
		ArrayList<String> childNames = new ArrayList<String>();
		ArrayList<String> childUUIDs = new ArrayList<String>();
		ArrayList<String> childTypes = new ArrayList<String>();
		
		// These lists are used for the step catalog (There will be more rows here than in the above lists)
		ArrayList<String> stepNames = new ArrayList<String>();
		ArrayList<String> stepUUIDs = new ArrayList<String>();
		ArrayList<String> stepFactoryIds = new ArrayList<String>();
		

		if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
			chartPath = globalProject.getFolderPath(res.getResourceId());
			resourceId = res.getResourceId();

// Commented out on 7/31/17 PH
//			createChart(res);
			
			log.infof("Compiling a SFC chart resource. Path: %s, Name: %s, id: %d", chartPath, res.getName(), resourceId);
			try {
				byte[] chartResourceData = res.getData();

				// This prints out nicely formatted XML of the resource.
//				IlsSfcCommonUtils.printResource(chartResourceData);
				
				GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
				ChartUIModel uiModel = ChartUIModel.fromXML(xmlInput, stepRegistry );

				for(ChartUIElement el: uiModel.getChartElements()) {
					ElementType stepType = el.getType();
					log.tracef(" ----  ");
					log.tracef("Looking at a chart element");
					log.tracef("        Id: %s", el.getId());
					log.tracef("      Type: %s", el.getType());
					log.tracef("      Name: %s", el.getRawValueMap().get(IlsProperty.NAME));
					log.tracef(" Value Map: %s", el.getRawValueMap());
					
					// build the step catalog structures
					stepNames.add( (String) el.getRawValueMap().get(IlsProperty.NAME));
					stepUUIDs.add( (String) el.getRawValueMap().get(IlsProperty.ID).toString());
					stepFactoryIds.add( (String) el.getRawValueMap().get(IlsProperty.FACTORY_ID));
					
//					for(Property<?> obj: el.getRawValueMap().keySet()){
//						log.infof(" map type: %s",obj.getType());
//						log.infof(" key: %s", BasicProperty.getClass().getCanonicalName());
//					}

					if (stepType.equals(ElementType.Parallel)){
						log.tracef(">>> Processing a parallel transition");
						ChartUIModel parallelUiModel = (ChartUIModel) el.getRawValueMap().get(IlsProperty.PARALLEL_CHILDREN);
						for(ChartUIElement child: parallelUiModel.getChartElements()) {
							log.tracef("Looking at a EMBEDDED CHILD element");
							log.tracef("            Id: %s", child.getId());
							log.tracef("          Type: %s", child.getType());
							log.tracef("          Name: %s", child.getRawValueMap().get(IlsProperty.NAME));
							log.tracef("     Value Map: %s", child.getRawValueMap());
							if (child.getType().equals(ElementType.Step)){
								
								// Add to the step catalog
								stepNames.add( (String) child.getRawValueMap().get(IlsProperty.NAME));
								stepUUIDs.add( (String) child.getRawValueMap().get(IlsProperty.ID).toString());
								stepFactoryIds.add( (String) child.getRawValueMap().get(IlsProperty.FACTORY_ID));
								
								// If we find an encapsulation step inside of a parallel transition pair.
								String embeddedChildChartPath = (String) child.getRawValueMap().get(IlsProperty.CHART_PATH);
								if (embeddedChildChartPath != null){
									log.tracef("Found an encapsulation INSIDE the parallel translation that calls %s", embeddedChildChartPath);
									childPaths.add( (String) embeddedChildChartPath);
									childNames.add( (String) child.getRawValueMap().get(IlsProperty.NAME));
									childUUIDs.add( (String) child.getRawValueMap().get(IlsProperty.ID).toString());
									childTypes.add( (String) child.getRawValueMap().get(IlsProperty.FACTORY_ID));
								}
							}
						}
					}
					else if (stepType.equals(ElementType.Step)){
						String childChartPath = (String) el.getRawValueMap().get(IlsProperty.CHART_PATH);
						if (childChartPath != null){
							log.tracef("Found an encapsulation that calls %s", childChartPath);
							childPaths.add( (String) childChartPath);
							childNames.add( (String) el.getRawValueMap().get(IlsProperty.NAME));
							childUUIDs.add( (String) el.getRawValueMap().get(IlsProperty.ID).toString());
							childTypes.add( (String) el.getRawValueMap().get(IlsProperty.FACTORY_ID));
						}
					}	
				}
				log.infof("The children are: %s", childNames);

				// Update the database with the children

				try {
					Object[] args = {chartPath, resourceId, stepNames, stepUUIDs, stepFactoryIds, childPaths, childNames, childUUIDs, childTypes, database};
					PythonCall.UPDATE_CHART_HIERARCHY.exec( args );
				} 
				catch (JythonExecException e) {
					log.errorf("%s: Error in python (update chart hierarchy): %s",CLSS,e.getLocalizedMessage());
				}

			}
			catch(IOException ioe) {
				log.errorf("loadModels: Exception reading %s:%d (%s)",chartPath,res.getResourceId(),ioe.getLocalizedMessage());
				success = false;
			}
			catch(XMLParseException xpe) {
				log.errorf("loadModels: Error deserializing %s:%d (%s)",chartPath,res.getResourceId(),xpe.getLocalizedMessage());
				success = false;
			}
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
