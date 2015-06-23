package com.ils.sfc.gateway;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.project.ProjectVersion;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectListener;
import com.inductiveautomation.ignition.gateway.project.ProjectManager;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.api.PyChartScope;

/**
 * A manager that keeps track of changes to recipe data as a chart runs.
 * Changes are saved back to the chart definition. Un-modified recipe
 * data is also available through this class.
 * 
 * Implementation concerns:
 *    is there any possibility of uncommitted changes building up over time (memory leak)?
 */
public class RecipeDataChangeMgr implements ChartObserver, ProjectListener {
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataChangeMgr.class.getName());
	private Map<String,Set<String>> changedStepScopeIdsByRunId = new HashMap<String,Set<String>>(); 
	private Map<String,String> staticRecipeDataByStepId = new HashMap<String,String>();
	private Map<String,String> changedRecipeDataByStepId = new HashMap<String,String>();
	private Project globalProject;
	private ProjectManager projectManager;
	private Map<String,Long> resourceIdsByStepId = new HashMap<String,Long>();
	private boolean flushing = false;
	private static long changeLatencyMillis = 5000;
	
	public RecipeDataChangeMgr(GatewayContext context) {
		projectManager = context.getProjectManager();
		globalProject = projectManager.getGlobalProject(ApplicationScope.GATEWAY);
		projectManager.addProjectListener(this);
		try {
			initializeAllStaticRecipeData();
		} catch (Exception e) {
			logger.error("Error initializing recipe data", e);
		}
	}

	/** Record the changed recipe data for a particular step. The run id is the for the
	 *  TOP level chart, though the step may be in a different, enclosed chart. */
	public synchronized void addChangedScope(PyChartScope stepScope, String chartRunId)  {
		String stepId = (String)stepScope.get("id");
		Set<String> changedStepScopeIds = getChangedStepScopeIdsForRun(chartRunId);
		changedStepScopeIds.add(stepId);				
		try {
			changedRecipeDataByStepId.put(stepId, getAssociatedDataTextForStepScope(stepScope));
		} catch (JSONException e) {
			logger.error("Error saving step scope change as JSON", e);
		}
	}
	
	/** Get the recipe data for a particular step. This data will reflect any recent
	 *  changes to it. */
	public synchronized String getRecipeData(String stepId) {
		String changedData =  changedRecipeDataByStepId.get(stepId);
		return changedData != null ? changedData : staticRecipeDataByStepId.get(stepId);
	}
	
	/** Pull the static recipe data out of all the chart definitions and store it here. */
	private synchronized void initializeAllStaticRecipeData() {
		logger.info("Initializing recipe data in RecipeDataChangeMgr");
		staticRecipeDataByStepId.clear();
		for(ProjectResource chartResource: getChartResources()) {
			initializeStaticRecipeData(chartResource);
		}
	}
	
	/** Write all changed recipe data for the given run back into the associated data
	 *  properties of the chart definitions. */
	private synchronized void flushChanges(String chartRunId) throws Exception {
		if(!changesExistForRun(chartRunId)) return;
		logger.debug("Saving recipe data changes for run" + chartRunId);
		Map<Long, Set<String>> changedStepScopeIdsByResourceId  = getChangedStepScopeIdsByResourceId(chartRunId);
		for(ProjectResource chartResource: getChartResources()) {
			// see if there are changes for this resource
			Set<String> changedStepScopeIdsForResource = changedStepScopeIdsByResourceId.get(
				Long.valueOf(chartResource.getResourceId()));
			if(changedStepScopeIdsForResource == null) continue;
			// replace the changed associated-data elements in the chart definition xml
			if(logger.isDebugEnabled()) {
				logger.debug("updating recipe data for resource " + chartResource.getName());
			}
			Document doc = getDocumentForChartResource(chartResource);
			ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(outBytes, true);			
			replaceAssociatedData(doc, changedStepScopeIdsForResource, out);
			out.flush();
			byte[] newBytes = outBytes.toByteArray();
			out.close();
			// put the new chart definition xml back in the resource
			chartResource.setData(newBytes);
			Project diff = globalProject.getDiff();
			diff.putResource(chartResource);
			String host = InetAddress.getLocalHost().getHostName();
			projectManager.saveProject(diff, null, host, "recipe data change", true);
		}
		changedStepScopeIdsByRunId.remove(chartRunId);
	}
	
	/** Pull the static recipe data out of one chart definition and store it here. */
	private void initializeStaticRecipeData(ProjectResource chartResource)  {
		try {
			Document doc = getDocumentForChartResource(chartResource);
			Map<String, Node> associatedDataNodesByStepId = getAssociatedDataNodesByStepId(doc);
			for(Entry<String,Node> entry: associatedDataNodesByStepId.entrySet()) {
				String stepId = entry.getKey();
				Node associatedDataNode = entry.getValue();
				staticRecipeDataByStepId.put(stepId, associatedDataNode.getTextContent());
				resourceIdsByStepId.put(stepId, Long.valueOf(chartResource.getResourceId()));
			}
		}
		catch(Exception e) {
			logger.error("Error initializing recipe data for resource " + chartResource.getName(), e );
		}
	}
	
	/** Translate a chart definition in resource form to an xml DOM document. */
	private Document getDocumentForChartResource(ProjectResource resource) throws Exception {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		byte[] resourceData = resource.getData();
		GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(resourceData));
		Document doc = docBuilder.parse(in);
		in.close();
		return doc;
	}	

	/** Get the ids of all steps whose recipe data has unsaved changes for the given run. 
	 *  If no changes exist yet, a set is created to hold them. */
	private Set<String> getChangedStepScopeIdsForRun(String runId) {
		Set<String> changedStepScopeIds = changedStepScopeIdsByRunId.get(runId);
		if(changedStepScopeIds == null) {
			changedStepScopeIds = new HashSet<String>();
			changedStepScopeIdsByRunId.put(runId, changedStepScopeIds);
		}
		return changedStepScopeIds;
	}
	
	/** Answer if any changes have been registered for the given run. */
	private boolean changesExistForRun(String runId) {
		return changedStepScopeIdsByRunId.get(runId) != null;
	}
	
	/** Partition the changed step ids according to what resource the step is in. */
	private Map<Long, Set<String>> getChangedStepScopeIdsByResourceId(String chartRunId) {
		Map<Long, Set<String>> changedStepScopeIdsByResourceId = new HashMap<Long, Set<String>>();
		for(String changedStepId: getChangedStepScopeIdsForRun(chartRunId)) {
			Long resourceId = resourceIdsByStepId.get(changedStepId);
			if(resourceId == null) {
				logger.error("Couldn't find resource id for step id " + changedStepId);
				continue;
			}
			Set<String> stepIds = changedStepScopeIdsByResourceId.get(resourceId);
			if(stepIds == null) {
				stepIds = new HashSet<String>();
				changedStepScopeIdsByResourceId.put(resourceId, stepIds);
			}
			stepIds.add(changedStepId);
		}
		return changedStepScopeIdsByResourceId;
	}

	/** Get all the chart resources. */
	private List<ProjectResource> getChartResources() {
		List<ProjectResource> chartResources = new ArrayList<ProjectResource>();
		for(ProjectResource res: globalProject.getResources()) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				chartResources.add(res);
			}
		}
		return chartResources;
	}
	
	/** Transform the run-time step scope into the static JSON representation for recipe data */
	private String getAssociatedDataTextForStepScope(PyChartScope stepScope) throws JSONException {
		JSONObject jsonObject = Data.fromStepScope(stepScope);
		return jsonObject.toString();
	}

	/** Pull out the nodes in a DOM chart definition that correspond to associated-data
	 *  step properties, i.e., recipe data. */
	private Map<String, Node> getAssociatedDataNodesByStepId(Document doc) {
		Map<String, Node> associatedDataNodesByStepId = new HashMap<String, Node>();
		NodeList stepNodes = doc.getDocumentElement().getChildNodes();
		for(int i = 0; i < stepNodes.getLength(); i++) {
			Node stepNode = stepNodes.item(i);
			if(!stepNode.getNodeName().equals("step")) continue;
			String stepId = stepNode.getAttributes().getNamedItem("id").getTextContent();
				NodeList childNodes = stepNode.getChildNodes();
				for(int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if(childNode.getNodeName().equals("associated-data")) {
						associatedDataNodesByStepId.put(stepId, childNode);
					}
				}
		}
		return associatedDataNodesByStepId;
	}
	
	/** Given a chart definition DOM, replace associated data content for
	 *  the steps given in the map. The changed definition is output as XML.
	 *  Returns count of replaced elements.
	 */
	private void replaceAssociatedData(Document doc, 
			Set<String> changedStepIds, OutputStream out) throws Exception {
		Map<String, Node> associatedDataNodesByStepId = getAssociatedDataNodesByStepId(doc);
		// Make the changes in the DOM:
		for(String changedStepId: changedStepIds) {
			String replacementText = changedRecipeDataByStepId.get(changedStepId);
			changedRecipeDataByStepId.remove(changedStepId);
			Node associatedDataNode = associatedDataNodesByStepId.get(changedStepId);
			if(logger.isDebugEnabled()) {
				logger.debug("updating recipe data for step " + changedStepId + " to " + replacementText);
			}
			if(associatedDataNode != null) {  
				associatedDataNode.setTextContent(replacementText);
			}	
			else {
				logger.error("Couldn't find associated data node for step " + changedStepId);
			}
		}
		// Transform the DOM into XML:
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(out));
	}

	/** A chart listener that we use to know when a top-level chart run ends so we
	 *  can save any changed recipe data it might have. */
	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		if(newChartState.isTerminal()) {
			try {
				flushing = true;
				flushChanges(chartId.toString());
			} catch (Exception e) {
				logger.error("error saving recipe data changes", e);
			}
			finally {
				flushing = false;
			}
		}
	}

	/** We don't use the element state change notification. */
	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,ElementStateEnum arg2, ElementStateEnum arg3) {
	}

	@Override
	public void projectAdded(Project arg0, Project arg1) {}

	@Override
	public void projectDeleted(long arg0) {}

	@Override
	public void projectUpdated(Project project, ProjectVersion version) {
		/** If charts may have changed, rebuild the recipe data
		 *  (unless we are the ones who did the changing).
		 *  this isn't too selective right now--if any change is
		 *  made to the global project we will rebuild all recipe data */
		
		// To avoid deadlocks, if we are currently writing back to the project ignore this notification.
		// When the flush finishes and commits the project changes, this will provoke another update
		// notification so we will eventually do the reload
		if(flushing) return;
		
		if(project.getId() == -1) {
			logger.debug("Rebuilding static recipe data in response to global project change");
			initializeAllStaticRecipeData();
		}			
	}

}
