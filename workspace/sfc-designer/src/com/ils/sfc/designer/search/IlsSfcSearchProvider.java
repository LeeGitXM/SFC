package com.ils.sfc.designer.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.inductiveautomation.ignition.common.gui.progress.TaskProgressListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectAggregator;
import com.inductiveautomation.ignition.designer.findreplace.SearchProvider;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;

public class IlsSfcSearchProvider implements SearchProvider {
	private final String TAG = "IlsSfcSearchProvider";
	private final LoggerEx log;
	private final DesignerContext context;
	private final ClientStepRegistry registry;
	
	public IlsSfcSearchProvider(DesignerContext ctx,ClientStepRegistry reg) {
		this.context = ctx;
		this.registry = reg;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
	}

	@Override
	public List<Object> getCategories() {
		List<Object> cts = new ArrayList<>();
		cts.add("Chart");
		cts.add("Step");
		cts.add("Recipe Key");
		cts.add("Recipe Data Value");
		return cts;
	}

	@Override
	public String getName() {
		return "SFC Charts";
	}

	@Override
	public boolean hasSelectableObjects() {
		return false;
	}

	@Override
	public void notifySearchClosed() {
		// Close any resource edit locks here.
		
	}

	@Override
	public Iterator<SearchObject> retrieveSearchableObjects(
			Collection<Object> selectedCategories, List<Object> arg1,
			TaskProgressListener progress) {
		SearchObjectAggregator agg = new SearchObjectAggregator(progress);
		String resourceType = IlsSfcChartStructureCompiler.CHART_RESOURCE_TYPE;
		List<ProjectResource> resources = context.getProject().getResourcesOfType(IlsSfcModule.MODULE_ID,resourceType);
		for(ProjectResource res:resources) {
			log.infof("%s.retrieveSearchableObjects resId = %d",TAG,res.getResourceId());
			agg.add(new ChartSearchCursor(context,registry,res));
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
