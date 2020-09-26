package com.ils.sfc.designer.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.python.core.PyDictionary;
import org.python.core.PyList;
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

/**
 * The chart search cursor iterates over component types
 * within a single chart.
 */
public class ChartSearchCursor extends SearchObjectCursor {
	private final String TAG = "ChartSearchCursor";
	private final DesignerContext context;
	private final boolean searchChart;
	private final boolean searchStep;
	private final boolean searchRecipe;
	private final boolean searchTransition;
	
	private String chartPath = null;
	private final LoggerEx log;
	private final ProjectResource res;
	private Document xmlDocument = null;
	private final int searchKey;
	private int chartIndex = 0;
	private int stepIndex = 0;
	private int transitionIndex = 0;
	private int recipeIndex = 0;
	private NodeList stepList = null;
	private NodeList transitionList = null;
	private PyList recipeList = new PyList();
	private PropertySearchCursor propertyCursor = null;

	public ChartSearchCursor(DesignerContext ctx, ProjectResource resource, Project project, int key) {
		this.context = ctx;
		this.res = resource;
		this.searchKey = key;
		searchChart  = (searchKey & IlsSfcSearchProvider.SEARCH_CHART)!=0;
		searchStep   = (searchKey & IlsSfcSearchProvider.SEARCH_STEP)!=0;
		searchRecipe = (searchKey & IlsSfcSearchProvider.SEARCH_RECIPE)!=0;
		searchTransition = (searchKey & IlsSfcSearchProvider.SEARCH_TRANSITION)!=0;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.chartIndex = 0;
		this.stepIndex = 0;
		this.transitionIndex = 0;
		this.recipeIndex = 0;
		this.propertyCursor = null;
		
		byte[] chartResourceData = res.getData();					
		chartPath = project.getFolderPath(res.getResourceId());
		log.tracef("%s.new - initializing a search cursor %s (%d)", TAG, chartPath,res.getResourceId());
		try {
			GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xmlDocument = dBuilder.parse(xmlInput);
			
			Element documentElement = xmlDocument.getDocumentElement();
			
			stepList = documentElement.getElementsByTagName("step");
			transitionList = documentElement.getElementsByTagName("transition");
			
			try {
				recipeList = (PyList)PythonCall.GET_RECIPE_SEARCH_RESULTS.exec(chartPath);
			}
			catch(JythonExecException jee) {
				log.warnf("%s.next: JythonExecException executing %s:(%s)",TAG,PythonCall.GET_RECIPE_SEARCH_RESULTS,jee.getLocalizedMessage());
			}
		
			// Only leave this in during debugging
			//IlsSfcCommonUtils.printResource(chartResourceData);
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
	@Override
	public Object next() {
		Object so = null;   // Search Object

		// First step is chart name
		if( searchChart && chartIndex==0 ) {
			//log.infof("%s.next %s", TAG, res.getName());
			so = new ChartNameSearchObject(context, chartPath, res.getResourceId());
			chartIndex++;
		}
		// The number of properties in a step is indeterminate
		// Create a property cursor for each new step. The cursor is guaranteed
		// to return at least one property, the name.
		else if(searchStep && stepIndex < stepList.getLength() ) {
			//log.infof("%s.next: Searching step %d...",TAG,stepIndex);
			Element element = (Element) stepList.item(stepIndex);
			if( propertyCursor==null ) propertyCursor = new PropertySearchCursor(context, chartPath, res.getResourceId(), element);
			so = propertyCursor.next();
			if( !propertyCursor.hasNext()) {
				propertyCursor = null;
				stepIndex++;    // Go to next step
			}
		}
		else if(searchTransition && transitionIndex < transitionList.getLength() ) {
			log.info("Searching a transition...");
			Element element = (Element) transitionList.item(transitionIndex);
			so = new TransitionSearchObject(context, chartPath, res.getResourceId(), element);
			transitionIndex++;
		}
		else if(searchRecipe && recipeIndex<recipeList.size()) {
			PyDictionary recipe = (PyDictionary)recipeList.get(recipeIndex);
			if( recipe!=null ) {
				so = new RecipeSearchObject(context, res.getResourceId(), recipe);
			} 
			recipeIndex++;
		}
		return so;
	}

}
