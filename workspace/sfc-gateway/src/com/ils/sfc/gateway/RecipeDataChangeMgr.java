package com.ils.sfc.gateway;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.client.gateway_interface.ResourceLockManager;
import com.inductiveautomation.ignition.client.gateway_interface.ResourceLockManagerFactory;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.user.AuthenticatedUser;
import com.inductiveautomation.ignition.common.user.BasicAuthChallenge;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.project.ProjectManager;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfile;
import com.inductiveautomation.sfc.ChartObserver;
import com.inductiveautomation.sfc.ChartStateEnum;
import com.inductiveautomation.sfc.ElementStateEnum;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.SfcGatewayHook;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.api.elements.GatewayStepRegistry;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/**
 * Keep track of changes in recipe data, and save them for later inspection
 * --maybe listen for completion of charts and save any changes for that chart...
 */
public class RecipeDataChangeMgr implements ChartObserver {
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	private static LoggerEx logger = LogUtil.getLogger(ChartObserver.class.getName());
	private Map<String, Map<String,PyChartScope>> changedStepScopesByRunId = new HashMap<String,  Map<String,PyChartScope>>();
	private Project globalProject;
	private ProjectManager projectManager;
	private GatewayContext context;
	//private IlsSfcChartStructureCompiler chartCompiler;
		
	public RecipeDataChangeMgr(GatewayContext context) {
		//GatewayStepRegistry stepRegistry = ((SfcGatewayHook)context.getModule(SFCModule.MODULE_ID)).getStepRegistry();
		this.context = context;
		projectManager = context.getProjectManager();
		globalProject = projectManager.getGlobalProject(ApplicationScope.GATEWAY);
	}

	public synchronized void addChangedScope(PyChartScope stepScope, String chartRunId) {
		Map<String,PyChartScope> changedStepScopes = changedStepScopesByRunId.get(chartRunId);
		if(changedStepScopes == null) {
			changedStepScopes = new HashMap<String, PyChartScope>();
			changedStepScopesByRunId.put(chartRunId, changedStepScopes);
		}				
		changedStepScopes.put((String)stepScope.get("id"), stepScope);
	}
	
	/** Write all changed step scopes for the given run back into the associated data
	 *  properties of the chart definitions. */
	public synchronized void flushChanges(String chartRunId) throws Exception {
		Map<String,PyChartScope> changedStepScopes = changedStepScopesByRunId.get(chartRunId);
		if(changedStepScopes != null) {
			Map<String, String> updatedDataByStepId = buildChangeMap(changedStepScopes);
			List<ProjectResource> chartResources = getChartResourcesForChangedScopes(changedStepScopes);
			for(ProjectResource chartResource: chartResources) {
				byte[] resourceData = chartResource.getData();
				GZIPInputStream in = new GZIPInputStream(
					new ByteArrayInputStream(resourceData));
				ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
				GZIPOutputStream out = new GZIPOutputStream(outBytes, true);			
				int numReplacements = replaceAssociatedData(in, updatedDataByStepId, out);
				out.flush();
				// TODO: remove check on numReplacements when we're not using all charts
				if(numReplacements > 0) {
					byte[] newBytes = outBytes.toByteArray();
					chartResource.setData(newBytes);
					Project diff = globalProject.getDiff();
					diff.putResource(chartResource);
					UserSourceProfile defaultProfile = context.getUserSourceManager().getProfile("default");
					BasicAuthChallenge authChallenge = new BasicAuthChallenge("admin", "password");
					AuthenticatedUser authUser = defaultProfile.authenticate(authChallenge);
					String host = InetAddress.getLocalHost().getHostName();
					projectManager.saveProject(diff, authUser, host, "recipe data change", true);
				}
			}
			changedStepScopesByRunId.remove(chartRunId);
		}
	}

	/** Get all the resources that have a changed step scope. */
	private List<ProjectResource> getChartResourcesForChangedScopes(Map<String,PyChartScope> asdf) {
		// TODO: be more efficient here--now we're just getting all chart resources
		List<ProjectResource> chartResources = new ArrayList<ProjectResource>();
		for(ProjectResource res: globalProject.getResources()) {
			if( res.getResourceType().equals(CHART_RESOURCE_TYPE)) {
				chartResources.add(res);
			}
		}
		return chartResources;
	}

	/** Build a map of changed associated data by step id */
	private Map<String, String> buildChangeMap(
			Map<String, PyChartScope> changedStepScopes) throws JSONException {
		Map<String, String> updatedDataByStepId = new HashMap<String, String>();
		for(java.util.Map.Entry<String,PyChartScope> entry: changedStepScopes.entrySet()) {
			String stepId = entry.getKey();
			PyChartScope scope = entry.getValue();
			JSONObject jsonObject = Data.fromStepScope(scope);
			updatedDataByStepId.put(stepId, jsonObject.toString());
		}
		return updatedDataByStepId;
	}

	/** Given in/out streams representing chart xml, replace associated data content for
	 *  the steps given in the map. Returns count of replaced elements.
	 */
	public int replaceAssociatedData(InputStream in, 
			Map<String,String> replacementByStepId, OutputStream out) throws Exception {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(in);
		NodeList stepNodes = doc.getDocumentElement().getChildNodes();
		int numReplacements = 0;
		for(int i = 0; i < stepNodes.getLength(); i++) {
			Node stepNode = stepNodes.item(i);
			if(!stepNode.getNodeName().equals("step")) continue;
			String stepId = stepNode.getAttributes().getNamedItem("id").getTextContent();
			String replacement = replacementByStepId.get(stepId);
			if(replacement != null) {
				++numReplacements;
				NodeList childNodes = stepNode.getChildNodes();
				for(int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if(childNode.getNodeName().equals("associated-data")) {
						childNode.setTextContent(replacement);
					}
				}
			}
		}
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(doc), new StreamResult(out));
		return numReplacements;
	}

	@Override
	public void onChartStateChange(UUID chartId, ChartStateEnum oldChartState,
			ChartStateEnum newChartState) {
		if(newChartState.isTerminal()) {
			try {
				flushChanges(chartId.toString());
			} catch (Exception e) {
				logger.error("error saving recipe data changes", e);
			}
		}
	}

	@Override
	public void onElementStateChange(UUID arg0, UUID arg1,ElementStateEnum arg2, ElementStateEnum arg3) {
	}

}
