package com.ils.sfc.designer.search;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.ils.sfc.common.chartStructure.ChartStructureCompilerV2;
import com.inductiveautomation.factorypmi.designer.search.VisionSearchProvider;
import com.inductiveautomation.factorypmi.designer.search.WhichWindows;
import com.inductiveautomation.ignition.client.util.gui.PopupWrapper;
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
	public final static int SEARCH_TRANSITION = 8;

	private final LoggerEx log;
	private final DesignerContext context;
	private final StepRegistry stepRegistry;
	private PopupWrapper popup = new PopupWrapper();
	private final Project project;
	private int chartCount = 0;

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
		cts.add("Transition");
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
		if( selectedCategories.contains("Transition"))         searchKey += SEARCH_TRANSITION;

		// Get a list of resources that we will consider for the search and pass it to the search engine.
		String resourceType = ChartStructureCompilerV2.CHART_RESOURCE_TYPE;
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResourcesOfType(SFCModule.MODULE_ID,resourceType);
		for(ProjectResource res:resources) {
			log.infof("%s.retrieveSearchableObjects adding resId = %d to the search list", TAG, res.getResourceId());
			agg.add(new ChartSearchCursor(context, stepRegistry, res, project, searchKey));
			chartCount = chartCount+1;
		}
		return agg;
	}

	@Override
	public void selectObjects(SelectedObjectsHandler handler) {
		if (!this.popup.hidePopup()) {
			SelectionPanel contents = new SelectionPanel(handler);
			this.popup.showPopup(handler.getSelectionComponent(), contents);
		}
	}

	/** 
	 * This is the hypertexted string that appears next to the category in the search/replace dialog
	 * @param arg0 is null
	 * @return string with chart count
	 */
	@Override
	public String selectedObjectsToString(List<Object> objects) {
		 ChosenCharts which = chosen(objects);
		 StringBuilder sb = new StringBuilder();
		 sb.append(which.name() + " Charts");
		 sb.append(" (").append(Integer.toString(getIds(which).size())).append(")");
		 return sb.toString();
	}
	
	private ChosenCharts chosen(List<Object> selection) {
		ChosenCharts which = null;
		if ((selection == null) || (selection.isEmpty()))
			which = ChosenCharts.Open;
		else {
			which = (ChosenCharts)selection.get(0);
		}
		return which;
	}
	// Create a list of resourceIds for the charts of interest
	private List<Long> getIds(ChosenCharts which) {
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResourcesOfType(SFCModule.MODULE_ID,ChartStructureCompilerV2.CHART_RESOURCE_TYPE);
		List<Long> results = new ArrayList<>();
		for(ProjectResource res:resources) {
			if( which.equals(ChosenCharts.All) ) {
				results.add(Long.valueOf(res.getResourceId()));
			}
		}
		return results;
	}

	// This is the popup window.
	public class SelectionPanel extends JPanel {
		SearchProvider.SelectedObjectsHandler handler;

		SelectionPanel(SearchProvider.SelectedObjectsHandler handler) {
			super();
			this.handler = handler;
			setBorder(BorderFactory.createBevelBorder(0));

			ChosenCharts current = IlsSfcSearchProvider.this.chosen(handler.getPreviousSelection());

			JRadioButton all = new SelectionRadio(ChosenCharts.All, current);
			JRadioButton selected = new SelectionRadio(ChosenCharts.Selected, current);
			JRadioButton open = new SelectionRadio(ChosenCharts.Open, current);
			ButtonGroup bg = new ButtonGroup();
			bg.add(all);
			bg.add(selected);
			bg.add(open);

			add(all, "wrap");
			add(selected, "wrap");
			add(open);
		}
		// Doubly nested
		public class SelectionRadio extends JRadioButton {
			private static final long serialVersionUID = -5779382592379733297L;
			ChosenCharts whichCharts;

			public SelectionRadio(ChosenCharts which, ChosenCharts current) {
				super(which.name());
				setSelected(which == current);
				this.whichCharts = which;

				addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == 1)
							SelectionRadio.this.choose();
					}
				});
				addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent e) {
						if (SelectionRadio.this.isSelected())
							SelectionRadio.this.choose();
					}
				});
			}

			public void choose()
			{
				List selection = new ArrayList(1);
				selection.add(this.whichCharts);
				IlsSfcSearchProvider.SelectionPanel.this.handler.setSelectedObjects(selection);
				IlsSfcSearchProvider.this.popup.hidePopup();
			}
		}
	}

}
