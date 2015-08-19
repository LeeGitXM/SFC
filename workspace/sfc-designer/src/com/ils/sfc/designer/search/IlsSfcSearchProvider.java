package com.ils.sfc.designer.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.inductiveautomation.ignition.common.gui.progress.TaskProgressListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectAggregator;
import com.inductiveautomation.ignition.designer.findreplace.SearchProvider;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;

public class IlsSfcSearchProvider implements SearchProvider {
	private final String TAG = "IlsSfcSearchProvider";
	public final static int SEARCH_CHART      = 1;
	public final static int SEARCH_STEP       = 2;
	public final static int SEARCH_SCOPE      = 4;
	public final static int SEARCH_SCOPE_DATA = 8;
	public final static int SEARCH_KEY        = 16;
	public final static int SEARCH_DATA       = 32;
	public final static int SEARCH_EXPRESSION = 64;
	private final LoggerEx log;
	private final DesignerContext context;
	
	public IlsSfcSearchProvider(DesignerContext ctx) {
		this.context = ctx;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	@Override
	public List<Object> getCategories() {
		List<Object> cts = new ArrayList<>();
		cts.add("Chart");
		cts.add("Step");
		cts.add("Scope Key");
		cts.add("Scope Value");
		cts.add("Recipe Key");
		cts.add("Recipe Data Value");
		cts.add("Expression");
		return cts;
	}

	@Override
	public String getName() {
		return "SFC Charts";
	}

	@Override
	public boolean hasSelectableObjects() {
		return true;
	}

	@Override
	public void notifySearchClosed() {
		// Close any resource edit locks here.
		
	}

	@Override
	public Iterator<SearchObject> retrieveSearchableObjects(Collection<Object> selectedCategories, List<Object> arg1,TaskProgressListener progress) {
		SearchObjectAggregator agg = new SearchObjectAggregator(progress);
		int searchKey = 0;     // Describes what categories to search
		if( selectedCategories.contains("Chart"))              searchKey += SEARCH_CHART;
		if( selectedCategories.contains("Step"))               searchKey += SEARCH_STEP;
		if( selectedCategories.contains("Scope Key"))          searchKey += SEARCH_SCOPE;
		if( selectedCategories.contains("Scope Value"))        searchKey += SEARCH_SCOPE_DATA;
		if( selectedCategories.contains("Recipe Key"))         searchKey += SEARCH_KEY;
		if( selectedCategories.contains("Recipe Data Value"))  searchKey += SEARCH_DATA;
		if( selectedCategories.contains("Expression"))         searchKey += SEARCH_EXPRESSION;
		
		String resourceType = ChartStructureCompiler.CHART_RESOURCE_TYPE;
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResourcesOfType(SFCModule.MODULE_ID,resourceType);
		for(ProjectResource res:resources) {
			log.infof("%s.retrieveSearchableObjects resId = %d",TAG,res.getResourceId());
			agg.add(new ChartSearchCursor(context,res,searchKey));
		}
		return agg;
	}

	@Override
	public void selectObjects(SelectedObjectsHandler arg0) {
		// ignore
		
	}

	@Override
	public String selectedObjectsToString(List<Object> arg0) {
		// ignore
		return null;
	}
}
