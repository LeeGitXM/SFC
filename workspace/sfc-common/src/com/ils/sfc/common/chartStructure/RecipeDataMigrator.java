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
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;
import com.inductiveautomation.sfc.uimodel.ElementType;

public class RecipeDataMigrator {
	private final LoggerEx log;
	private final StepRegistry stepRegistry;
	private final Project globalProject;
	String database = null;
	
	// Constants
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String PARALLEL_STEP="Parallel";
	
	// For the beginners in the crowd, this is the constructor
	public RecipeDataMigrator(Project globalProject, StepRegistry stepRegistry) {
		this.stepRegistry = stepRegistry;
		this.globalProject = globalProject;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.database = "XOM";
		log.info("Initializing my very own *** Recipe Data Migrator ***");
	}

	/** Compile Ignition charts and create an ILS model of the structure. We try to do an error-tolerant
	 *  compile so some useful information is available even if there are errors. A null return indicates
	 *  the errors were severe enough we couldn't get any useful info.
	 */
	public void migrateResource(ProjectResource res) {
		String chartPath;
		long resourceId;
		

		if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
			chartPath = globalProject.getFolderPath(res.getResourceId());
			resourceId = res.getResourceId();
			
			log.infof("Migrating a SFC Recipe Data. Path: %s, Name: %s, id: %d", chartPath, res.getName(), resourceId);
			try {
				byte[] chartResourceData = res.getData();

				// This prints out nicely formatted XML of the resource.
				String chartResourceAsXML=IlsSfcCommonUtils.returnResource(chartResourceData);
				log.infof("Resource: %s", chartResourceAsXML);
				
				try {				
					Object[] args = {chartPath, resourceId, chartResourceAsXML, database};
					PythonCall.MIGRATE_RECIPE_DATA.exec( args );
				} catch (JythonExecException e) {
					log.errorf("Caught an error (%s)", e.getMessage());
				
				}
			}
			catch(IOException ioe) {
				log.errorf("loadModels: Exception reading %s:%d (%s)",chartPath,res.getResourceId(),ioe.getLocalizedMessage());
			}
	
		}
	}
	
}

