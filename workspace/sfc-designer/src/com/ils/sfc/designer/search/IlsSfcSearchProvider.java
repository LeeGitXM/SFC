package com.ils.sfc.designer.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.inductiveautomation.ignition.common.gui.progress.TaskProgressListener;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectAggregator;
import com.inductiveautomation.ignition.designer.findreplace.SearchProvider;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.StepRegistry;

public class IlsSfcSearchProvider implements SearchProvider {
	private final String TAG = "IlsSfcSearchProvider";
	public final static int SEARCH_CHART      = 1;
	public final static int SEARCH_STEP       = 2;
	public final static int SEARCH_RECIPE     = 4;
	public final static int SEARCH_EXPRESSION = 8;

	private final LoggerEx log;
	private final DesignerContext context;
	private final StepRegistry stepRegistry;
	private final Project project;
	
	public IlsSfcSearchProvider(DesignerContext ctx, StepRegistry stepRegistry, Project project) {
		this.context = ctx;
		this.stepRegistry = stepRegistry;
		this.project = project;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		log.infof("Initializing an ILS Search provider");
	}

	@Override
	public List<Object> getCategories() {
		List<Object> cts = new ArrayList<>();
		cts.add("Chart");
		cts.add("Step");
		cts.add("Recipe Data");
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
		if( selectedCategories.contains("Recipe Data"))        searchKey += SEARCH_RECIPE;
		if( selectedCategories.contains("Expression"))         searchKey += SEARCH_EXPRESSION;
		
		// Get a list of resources that we will consider for the search and pass it to the search engine.
		String resourceType = ChartStructureCompiler.CHART_RESOURCE_TYPE;
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResourcesOfType(SFCModule.MODULE_ID,resourceType);
		for(ProjectResource res:resources) {
			log.tracef("%s.retrieveSearchableObjects adding resId = %d to the search list", TAG, res.getResourceId());
			agg.add(new ChartSearchCursor(context, stepRegistry, res, project, searchKey));
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
