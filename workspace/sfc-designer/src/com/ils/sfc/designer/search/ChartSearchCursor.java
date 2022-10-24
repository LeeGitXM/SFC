package com.ils.sfc.designer.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
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

	public ChartSearchCursor(DesignerContext ctx, ProjectResource resource, int key) {
		this.context = ctx;
		this.res = resource;
		this.searchKey = key;
		searchChart  = (searchKey & IlsSfcSearchProvider.SEARCH_CHART)!=0;
		searchStep   = (searchKey & IlsSfcSearchProvider.SEARCH_STEP)!=0;
		searchRecipe = (searchKey & IlsSfcSearchProvider.SEARCH_RECIPE)!=0;
		searchTransition = (searchKey & IlsSfcSearchProvider.SEARCH_TRANSITION)!=0;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		log.infof("Creating a ChartSearchCursor...");
		this.chartIndex = 0;
		this.stepIndex = 0;
		this.transitionIndex = 0;
		this.recipeIndex = 0;
		this.propertyCursor = null;
		
		/*
		 * This was a bit of magic that changed from Ignition 7.x to 8.x
		 * (Not sure how I was supposed to know that I needed to add "Sfc.xml") 
		 */
		byte[] chartResourceData = res.getData("sfc.xml");
		
		chartPath = res.getFolderPath();
		log.infof("%s.new - initializing a search cursor for %s (%s)", TAG, chartPath, res.getResourceId().toString());
		log.infof("Got %d bytes of resource data...", chartResourceData.length);
		
		try {
			log.tracef("Converting the resource to a string...");
			String xmlString = new String(chartResourceData, StandardCharsets.UTF_8);
			log.tracef("The string is: %s", xmlString);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			log.tracef("Creating an InputSource from a string...");
			InputSource is = new InputSource(new StringReader(xmlString));
			log.tracef("...done!");
			
			log.tracef("Building an XML document from a string...");
			xmlDocument = dBuilder.parse(is);
			log.tracef("...built an XML document!");
			
			log.tracef("Getting XML document elements...");
			Element documentElement = xmlDocument.getDocumentElement();
			log.tracef("...done getting XML elements!");
			
			log.tracef("Getting a step list...");
			stepList = documentElement.getElementsByTagName("step");
			log.tracef("Step List: %s", stepList.toString());
			
			log.tracef("Getting a transition list...");
			transitionList = documentElement.getElementsByTagName("transition");
			log.tracef("Transition List: %s", transitionList.toString());
			
			log.infof("Getting a list of recipe data from Python...");
			try {			
				recipeList = (PyList)PythonCall.GET_RECIPE_SEARCH_RESULTS.exec(chartPath);
			}
			catch(JythonExecException jee) {
				log.warnf("%s.next: JythonExecException executing %s:(%s)",TAG,PythonCall.GET_RECIPE_SEARCH_RESULTS,jee.getLocalizedMessage());
			}
		}
		catch(IOException ioe) {
			log.errorf("%s.next: Exception reading %s:%s (%s)", TAG, chartPath, res.getResourceId().toString(), ioe.getLocalizedMessage());
		}
		catch (SAXException saxe) {
			log.errorf("%s.next: SAXException reading %s:%s (%s)", TAG, chartPath, res.getResourceId().toString(), saxe.getLocalizedMessage());
		}
		catch (ParserConfigurationException pce) {
			log.errorf("%s.next: ParserConfigException reading %s:%s (%s)", TAG, chartPath, res.getResourceId().toString(), pce.getLocalizedMessage());
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