package com.ils.sfc.designer.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.python.core.PyDictionary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;

/**
 * The chart search cursor iterates over steps in a chart
 */
public class ChartSearchCursor extends SearchObjectCursor {
	private final String TAG = "ChartSearchCursor";
	private final DesignerContext context;
	private final Project project;
	private final StepRegistry stepRegistry;
	private ChartDefinition chart = null;
	private String chartPath = null;
	private ElementDefinition element = null;
	private final LoggerEx log;
	private final ProjectResource res;
	private Document xmlDocument = null;
	private final int searchKey;
	private int chartIndex = 0;
	private int stepIndex = 0;
	private int transitionIndex = 0;
	private int recipeIndex = 0;
	private List<PyDictionary> recipeList = null;
	private NodeList stepList = null;
	private NodeList transitionList = null;

	public ChartSearchCursor(DesignerContext ctx, StepRegistry stepRegistry, ProjectResource resource, Project project, int key) {
		this.context = ctx;
		this.res = resource;
		this.searchKey = key;
		this.stepRegistry = stepRegistry;
		this.project = project;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.chartIndex = 0;
		this.stepIndex = 0;
		this.transitionIndex = 0;
		this.recipeIndex = 0;
		log.infof("%s.new - initializing a search cursor for res=%d", TAG, res.getResourceId());
	}
	@Override
	public Object next() {
		log.infof("%s.next - searching res=%d", TAG, res.getResourceId());
		Object so = null;   // Search Object
		boolean searchChart  = (searchKey & IlsSfcSearchProvider.SEARCH_CHART)!=0;
		boolean searchStep   = (searchKey & IlsSfcSearchProvider.SEARCH_STEP)!=0;
		boolean searchRecipe = (searchKey & IlsSfcSearchProvider.SEARCH_RECIPE)!=0;
		boolean searchTransition = (searchKey & IlsSfcSearchProvider.SEARCH_TRANSITION)!=0;
		
		// Deserialize here - first time through only - return next block cursor
		if( chartIndex==0 ) {
			byte[] chartResourceData = res.getData();					
			chartPath = project.getFolderPath(res.getResourceId());
			try {
				GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
				
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				xmlDocument = dBuilder.parse(xmlInput);
				
				Element documentElement = xmlDocument.getDocumentElement();
				
				stepList = documentElement.getElementsByTagName("step");
				transitionList = documentElement.getElementsByTagName("transition");
			
				// Only leave this in during debugging
				//IlsSfcCommonUtils.printResource(chartResourceData);
				
				if(searchRecipe) {
					recipeList = (List<PyDictionary>) PythonCall.GET_RECIPE_SEARCH_RESULTS.exec();
				}
			}
			catch(JythonExecException jee) {
				log.errorf("%s.next: Exception executing Python %s",TAG,PythonCall.GET_RECIPE_SEARCH_RESULTS);
			}
			catch(IOException ioe) {
				log.errorf("%s.next: Exception reading %s:%d (%s)",TAG,chartPath, res.getResourceId(),ioe.getLocalizedMessage());
			}
			catch (SAXException saxe) {
				log.errorf("%s.next: SAXException reading %s:%d (%s)",TAG,chartPath, res.getResourceId(),saxe.getLocalizedMessage());
			}
			catch (ParserConfigurationException pce) {
				log.errorf("%s.next: ParserConfigException reading %s:%d (%s)",TAG,chartPath, res.getResourceId(),pce.getLocalizedMessage());
			}
		}
		
		// First step is chart name
		if( searchChart && chartIndex==0 ) {
			//log.infof("%s.next %s", TAG, res.getName());
			so = new ChartNameSearchObject(context, chartPath, res.getResourceId());
		}
		else if(searchStep && stepIndex < stepList.getLength() ) {
			//log.info("Searching a step...");
			Element element = (Element) stepList.item(stepIndex);
			so = new StepSearchObject(context, chartPath, res.getResourceId(), element);
			stepIndex++;
		}
		else if(searchTransition && transitionIndex < transitionList.getLength() ) {
			//log.info("Searching a transition...");
			Element element = (Element) transitionList.item(transitionIndex);
			so = new TransitionSearchObject(context, chartPath, res.getResourceId(), element);
			transitionIndex++;
		}
		else if(searchRecipe && recipeList!=null && recipeIndex < recipeList.size() ) {
			so = new RecipeSearchObject(context,recipeList.get(recipeIndex));
			recipeIndex++;
		}

		chartIndex++;
		return so;
	}

}
