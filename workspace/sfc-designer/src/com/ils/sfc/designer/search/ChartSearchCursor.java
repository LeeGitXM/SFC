package com.ils.sfc.designer.search;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
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
	private ChartDefinition chart = null; 
	private String chartPath = null;
	private String folderPath = null; 
	private ElementDefinition element = null;
	private final LoggerEx log;
	private final ProjectResource res;
	private final int searchKey;
	private int index = 0;
	private int subindex = 0;
	private final List<UUID> visited;
	
	public ChartSearchCursor(DesignerContext ctx,ProjectResource resource,int key) {
		this.context = ctx;
		this.res = resource;
		this.searchKey = key;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		this.visited = new ArrayList<>();
		log.infof("%s.new - res=%d",TAG,res.getResourceId());
	}
	@Override
	public Object next() {
		log.infof("%s.next - res=%d index=%d",TAG,res.getResourceId(),index);
		Object so = null;   // Search Object
		boolean browseChart = (searchKey&IlsSfcSearchProvider.SEARCH_CHART)!=0;
		// Deserialize here - first time through only - return next block cursor
		if( index==0 ) {
			ChartStructureManager structureManager = ((IlsSfcDesignerHook)context.getModule(IlsSfcModule.MODULE_ID)).getChartStructureManager();
			chart = structureManager.getChartDefinition(res.getResourceId());
			if( chart==null ) {
				log.infof("%s.next Failed to deserialize chart resource %s(%d)",TAG,res.getName(),res.getResourceId());
				return null;
			}
			chartPath = structureManager.getChartPath(res.getResourceId());
			folderPath = structureManager.getParentPath(res.getResourceId());
		}
		
		if( index==0 && (searchKey&IlsSfcSearchProvider.SEARCH_CHART)!=0 ) {
			
			so = new ChartNameSearchObject(context,folderPath,res);
			log.infof("%s.next %s",TAG,res.getName());
		}
		else if(index==(browseChart?1:0) ) {
			element = chart.getBeginElement();
			so = new StepSearchCursor(context,chartPath,res.getResourceId(),element,searchKey);
		}
		else {
			element = chart.getBeginElement();
			subindex = (browseChart?1:0);
			ElementDefinition chosenElement = visitChildren(element); 
			if( chosenElement!=null ) {
				so = new StepSearchCursor(context,chartPath,res.getResourceId(),chosenElement,searchKey);
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
