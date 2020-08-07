package com.ils.sfc.common.chartStructure;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.client.gateway_interface.GatewayException;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.IgnitionDesigner;
import com.inductiveautomation.ignition.designer.gateway.DTGatewayInterface;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class RecipeDataConverter {
	private final Project project;
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String CLSS="RecipeDataConverter";
	private final LoggerEx log;
	private final DesignerContext context;

	public RecipeDataConverter(Project proj, DesignerContext contxt){
		project = proj;
		context = contxt;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	/**
	 * 	Query the database for recipe data and embed it into the chart using the associated-data property
	 */
	public void internalize(){
		List<ProjectResource> resources = project.getResources();
		boolean success = true;
		PythonCall pCall = new PythonCall("ils.sfc.recipeData.internalize.internalize", String.class, new String[]{"path", "xml"});

		// Create a project to collect the updated resources - it will all be saved at the end
		Project diff = context.getGlobalProject().getProject().getEmptyCopy();

		try {
			log.infof("%s.internalize: Internalizing resources...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = project.getFolderPath(res.getResourceId());
					long resourceId = res.getResourceId();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
					}
					String stringXML = bldr.toString();
					log.tracef("Chart XML: %s %s \n%s", path, res.getName(), stringXML);

					Object[] args = {path, stringXML};

					// Call the Python that embeds the recipe data into the chart XML using the associated-data slot 
					String newStringXML = (String) pCall.exec( args );
					log.tracef("XML with embedded: %s", newStringXML);
					

					// Update the chart resource to make it permanent
					byte[] chartBytes = newStringXML.getBytes();
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					OutputStream zipper=new GZIPOutputStream(baos);
					zipper.write(chartBytes);
					zipper.close();
					byte[] newChartResourceData = baos.toByteArray();
					baos.close();

					log.tracef("internalize: Fetching lock for resource (%d)",resourceId); 
					if( context.requestLock(resourceId) ) {
						try
						{
							res.setData(newChartResourceData);
							context.updateLock(resourceId);

							diff.putResource(res, false);    // Mark as clean
						}
						finally {
							log.tracef("%s.internalize: Releasing lock for resource (%d)",CLSS,resourceId); 
							context.releaseLock(resourceId);
						}
					}
					else {
						log.warnf("%s.internalize: Unable to acquire a lock for resource (%d-%s) - resourse was not internalized",CLSS,resourceId, res.getName()); 
					}
				}
			}

			project.applyDiff(diff,false);
			project.clearAllFlags();          // Don't know what this does ...

			log.infof("%s.internalize: Saving resources",CLSS);
			DTGatewayInterface.getInstance().saveProject(IgnitionDesigner.getFrame(), project, true, "Committing ...");  // Not publish
			//log.infof("%s.commitEdit: Publishing resources",CLSS);
			//DTGatewayInterface.getInstance().publishGlobalProject(IgnitionDesigner.getFrame());
		}
		catch(GatewayException ge) {
			log.errorf("%s.internalize:GatewayException: Unable to save project update (%s)",CLSS,ge.getLocalizedMessage());
		} 
		catch(IOException ioe) {
			log.errorf("%s.internalize:IOException: Unable to save project update (%s)",CLSS,ioe.getMessage());
		} 
		catch(JythonExecException je) {
			log.errorf("%s.internalize:JythonExecException: Unable to save project update (%s)",CLSS,je.getMessage());
		} 
	}


	/**
	 * Find the recipe data that is embedded in the associated-data property of a chart step and insert it into the database.
	 */
	public void storeToDatabase(){
		List<ProjectResource> resources = project.getResources();
		boolean success = true;
		PythonCall pCall = new PythonCall("ils.sfc.recipeData.save.storeToDatabase", String.class, new String[]{"path", "xml"});

		// Create a project to collect the updated resources - it will all be saved at the end.  I think we will clear the associated data as it is saved!
		Project diff = context.getGlobalProject().getProject().getEmptyCopy();

		try {
			log.infof("%s.storeToDatabase: Storing internalized recipe data to the database...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = project.getFolderPath(res.getResourceId());
					long resourceId = res.getResourceId();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
					}
					String stringXML = bldr.toString();
					log.tracef("%s %s \n%s", path, res.getName(), stringXML);
					
					Object[] args = {path, stringXML};

					// Call the Python that embeds the recipe data into the chart XML using the associated-data slot 
					String newStringXML = (String) pCall.exec( args );
					log.infof("...back in Java...");

					// Update the chart resource to make it permanent
					byte[] chartBytes = newStringXML.getBytes();
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					OutputStream zipper=new GZIPOutputStream(baos);
					zipper.write(chartBytes);
					zipper.close();
					byte[] newChartResourceData = baos.toByteArray();
					baos.close();
					log.infof("... totally all done...");
					
				}
			}
		}
		catch(IOException ioe) {
			log.errorf("%storeToDatabase: Unable to save project update (%s)",CLSS,ioe.getMessage());
		} 
		catch(JythonExecException je) {
			log.errorf("%s.storeToDatabase:JythonExecException: Unable to save project update (%s)",CLSS,je.getMessage());
		} 
		log.infof("... leaving javaland...");
	}
	
	/**
	 * Initialize the associated-data property of every step.
	 * I should be able to do this entirely in Java without the help (and overhead) of calling Python - there are no database transactions involved here.
	 */
	public void initialize(){
		List<ProjectResource> resources = project.getResources();
		boolean success = true;
		PythonCall pCall = new PythonCall("ils.sfc.recipeData.initialize.initialize", String.class, new String[]{"path", "xml"});
		
		// Create a project to collect the updated resources - it will all be saved at the end.  I think we will clear the associated data as it is saved!
		Project diff = context.getGlobalProject().getProject().getEmptyCopy();

		try {
			log.infof("%s.initialize: Clearing the associated data property for every step...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = project.getFolderPath(res.getResourceId());
					long resourceId = res.getResourceId();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
					}
					String stringXML = bldr.toString();
					log.tracef("%s %s \n%s", path, res.getName(), stringXML);
					
					Object[] args = {path, stringXML};

					// Call the Python that embeds the recipe data into the chart XML using the associated-data slot 
					String newStringXML = (String) pCall.exec( args );
					log.infof("...back in Java...");
					log.tracef("New XML \n%s", newStringXML);

					// Update the chart resource to make it permanent
					byte[] chartBytes = newStringXML.getBytes();
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					OutputStream zipper=new GZIPOutputStream(baos);
					zipper.write(chartBytes);
					zipper.close();
					byte[] newChartResourceData = baos.toByteArray();
					baos.close();

					log.infof("... totally all done...");
					
				}
			}
			
			project.applyDiff(diff,false);
			project.clearAllFlags();          // Don't know what this does ...

			log.infof("%s.internalize: Saving resources",CLSS);
			DTGatewayInterface.getInstance().saveProject(IgnitionDesigner.getFrame(), project, true, "Committing ...");  // Not publish
			//log.infof("%s.commitEdit: Publishing resources",CLSS);
			//DTGatewayInterface.getInstance().publishGlobalProject(IgnitionDesigner.getFrame());
			
		}
		catch(GatewayException ge) {
			log.errorf("%s.internalize:GatewayException: Unable to save project update (%s)",CLSS,ge.getLocalizedMessage());
		} 
		catch(IOException ioe) {
			log.errorf("%initialize: Unable to save project update (%s)",CLSS,ioe.getMessage());
		} 
		catch(JythonExecException je) {
			log.errorf("%s.storeToDatabase:JythonExecException: Unable to save project update (%s)",CLSS,je.getMessage());
		} 
	}
	
	
}
