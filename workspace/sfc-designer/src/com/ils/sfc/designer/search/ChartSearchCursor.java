package com.ils.sfc.designer.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ils.sfc.common.IlsSfcCommonUtils;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;

/**
 * The chart search cursor iterates over steps in a chart
 * @author chuckc
 *
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
	private int index = 0;
	private int subindex = 0;
	private final List<UUID> visited;
	private NodeList stepList = null;

	public ChartSearchCursor(DesignerContext ctx, StepRegistry stepRegistry, ProjectResource resource, Project project, int key) {
		this.context = ctx;
		this.res = resource;
		this.searchKey = key;
		this.stepRegistry = stepRegistry;
		this.project = project;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		this.visited = new ArrayList<>();
		log.infof("%s.new - initializing a search cursor for res=%d", TAG, res.getResourceId());
	}
	@Override
	public Object next() {
		log.tracef("%s.next - searching res=%d index=%d", TAG, res.getResourceId(),index);
		Object so = null;   // Search Object
		boolean searchChart = (searchKey & IlsSfcSearchProvider.SEARCH_CHART)!=0;
		
		// Deserialize here - first time through only - return next block cursor
		if( index==0 ) {
			byte[] chartResourceData = res.getData();					
			
			chartPath = project.getFolderPath(res.getResourceId());
			try {
				GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(chartResourceData));
				
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				xmlDocument = dBuilder.parse(xmlInput);
				
				Element documentElement = xmlDocument.getDocumentElement();
				
				stepList = documentElement.getElementsByTagName("step");
				
				
				// Only leave this in during debugging
				IlsSfcCommonUtils.printResource(chartResourceData);
			}
			catch(IOException ioe) {
				log.errorf("loadModels: Exception reading %s:%d (%s)",chartPath, res.getResourceId(),ioe.getLocalizedMessage());
//				success = false;
			}
			catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		
		if( index==0 && (searchKey&IlsSfcSearchProvider.SEARCH_CHART)!=0 ) {
			log.infof("%s.next %s", TAG, res.getName());
			so = new ChartNameSearchObject(context, chartPath, res.getResourceId());
		}
//		else if(index > (searchChart?1:0) && (searchKey&IlsSfcSearchProvider.SEARCH_STEP)!=0 ) {
		else if( (searchKey&IlsSfcSearchProvider.SEARCH_STEP)!=0 ) {
			log.info("Searching a step...");
			int stepIndex = index - (searchChart?1:0);
			if (stepIndex < stepList.getLength()){
				Element element = (Element) stepList.item(stepIndex);
				so = new StepSearchObject(context, chartPath, res.getResourceId(), element);
			}
		}

		index++;
		return so;
	}
	
	private ElementDefinition visitChildren(ElementDefinition ed) {
		if(subindex==index) return ed;
		if( visited.contains(ed.getElementId())) return null;
		visited.add(ed.getElementId());
		subindex++;
		for(ElementDefinition def:ed.getNextElements() ) {
			ElementDefinition child = visitChildren(def);
			if(child!=null ) return child;
		}
		return null;
	}
}
