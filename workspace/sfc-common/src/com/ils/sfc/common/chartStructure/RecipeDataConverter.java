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
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.common.project.resource.ResourceType;
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

		
		try {
			log.infof("%s.internalize: Internalizing resources...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = res.getFolderPath();
					ProjectResourceId resourceId = res.getResourceId();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
						bldr.append('\n');
					}
					String stringXML = bldr.toString();
					log.tracef("Chart XML: %s %s \n%s", path, res.getResourceName(), stringXML);

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
					
					res = ProjectResource.newBuilder().setProjectName(context.getProject().getName())
							.setResourcePath(res.getResourceType().rootPath())
							.setApplicationScope(res.getApplicationScope())
							.putData(newChartResourceData).build();
					
					context.getProject().createOrModify(res);
				}
			}
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

		try {
			log.infof("%s.storeToDatabase: Storing internalized recipe data to the database...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = res.getFolderPath();
					long resourceId = res.getResourceId().hashCode();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
						bldr.append('\n');
					}
					String stringXML = bldr.toString();
					log.tracef("%s %s \n%s", path, res.getResourceName(), stringXML);
					
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
					
					res = ProjectResource.newBuilder().setProjectName(context.getProject().getName())
							.setResourcePath(res.getResourceType().rootPath())
							.setApplicationScope(res.getApplicationScope())
							.putData(newChartResourceData).build();
					
					context.getProject().createOrModify(res);
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
		

		try {
			log.infof("%s.initialize: Clearing the associated data property for every step...",CLSS);
			for(ProjectResource res:resources) {
				if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
					String path = res.getFolderPath();
					long resourceId = res.getResourceId().hashCode();

					byte[] chartResourceData = res.getData();					
					BufferedReader rdr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(chartResourceData))));
					StringBuilder bldr = new StringBuilder();
					String line = null;
					while((line = rdr.readLine()) != null) {
						bldr.append(line);
						bldr.append('\n');
					}
					String stringXML = bldr.toString();
					log.tracef("%s %s \n%s", path, res.getResourceName(), stringXML);
					
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
					
					res = ProjectResource.newBuilder().setProjectName(context.getProject().getName())
							.setResourcePath(res.getResourceType().rootPath())
							.setApplicationScope(res.getApplicationScope())
							.putData(newChartResourceData).build();
					
					context.getProject().createOrModify(res);
				}
			}
			
		}
		catch(IOException ioe) {
			log.errorf("%initialize: Unable to save project update (%s)",CLSS,ioe.getMessage());
		} 
		catch(JythonExecException je) {
			log.errorf("%s.storeToDatabase:JythonExecException: Unable to save project update (%s)",CLSS,je.getMessage());
		} 
	}
}
