
package com.ils.sfc.designer.search;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.tree.TreePath;

import com.ils.sfc.common.chartStructure.ChartStructureCompilerV2;
import com.inductiveautomation.ignition.client.util.gui.PopupWrapper;
import com.inductiveautomation.ignition.common.gui.progress.TaskProgressListener;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.resource.ProjectResource;
import com.inductiveautomation.ignition.common.project.resource.ResourceType;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObject;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectAggregator;
import com.inductiveautomation.ignition.designer.findreplace.SearchProvider;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.navtree.model.AbstractNavTreeNode;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;
import com.inductiveautomation.sfc.designer.tree.SfcFolderNode;
import com.inductiveautomation.sfc.designer.tree.SfcNode;
import com.inductiveautomation.sfc.designer.workspace.SfcDesignPanel;
import com.inductiveautomation.sfc.designer.workspace.SfcWorkspace;

public class IlsSfcSearchProvider implements SearchProvider {
	private final String TAG = "IlsSfcSearchProvider";
	public final static int SEARCH_CHART      = 1;
	public final static int SEARCH_STEP       = 2;
	public final static int SEARCH_RECIPE     = 4;
	public final static int SEARCH_TRANSITION = 8;
	
	public static final String CHART_TYPE_ID="charts";  // This doesn't belong here, IA should have this constant somewhere

	private final LoggerEx log;
	private final DesignerContext context;
	private PopupWrapper popup = new PopupWrapper();
	private final Project project;
	private ChosenCharts which;
	private List<ProjectResource> allResources;

	public IlsSfcSearchProvider(DesignerContext ctx,Project project) {
		this.context = ctx;
		this.project = project;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.which = ChosenCharts.All;
		this.allResources = null; 
		log.infof("%s.creating an instance", TAG);
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

	// Called when the user selects "Find" from the dialog.
	@Override
	public Iterator<SearchObject> retrieveSearchableObjects(Collection<Object> selectedCategories, List<Object> arg1,TaskProgressListener progress) {
		SearchObjectAggregator agg = new SearchObjectAggregator(progress);
		int searchKey = 0;     // Describes what categories to search
		if( selectedCategories.contains("Chart"))              searchKey += SEARCH_CHART;
		if( selectedCategories.contains("Step"))               searchKey += SEARCH_STEP;
		if( selectedCategories.contains("Recipe Data"))        searchKey += SEARCH_RECIPE;
		if( selectedCategories.contains("Transition"))         searchKey += SEARCH_TRANSITION;

		// Get a list of resources that we will consider for the search and pass it to the search engine.
		List<ProjectResource> chartResources = getResources(which);
		for(ProjectResource res:chartResources ) {
			log.tracef("%s.retrieveSearchableObjects %s (%d)", TAG, res.getFolderPath(), res.getResourceId().hashCode());
			agg.add(new ChartSearchCursor(context, res, project, searchKey));
		}
		return agg;
	}

	// This is called when the popup is opened
	@Override
	public void selectObjects(SelectedObjectsHandler handler) {
		log.infof("%s.selectObjects()...", TAG);
		if (!this.popup.hidePopup()) {
			SelectionPanel contents = new SelectionPanel(handler);
			this.popup.showPopup(handler.getSelectionComponent(), contents);
		}
	}

	/** 
	 * This is the hypertexted string that appears next to the category in the search/replace dialog.
	 * It is called:
	 * 	o  when the find dialog is opened
	 * 	o  when the user selects from the popup
	 * @param arg0 is null
	 * @return string with chart count
	 */
	@Override
	public String selectedObjectsToString(List<Object> objects) {
		log.infof("%s.selectedObjectsToString()...", TAG);
		this.allResources = context.getProject().getResourcesOfType(new ResourceType(SFCModule.MODULE_ID, CHART_TYPE_ID));
		this.which = chosen(objects);
		StringBuilder sb = new StringBuilder();
		sb.append(which.name() + " Charts");
		List<ProjectResource> chartResources = getResources(which);
		sb.append(" (").append(Integer.toString(chartResources.size())).append(")");
		return sb.toString();
	}
	
	private ChosenCharts chosen(List<Object> selection) {
		log.infof("%s.chosen()...", TAG);
		ChosenCharts which = null;
		if ((selection == null) || (selection.isEmpty()))
			which = ChosenCharts.All;
		else {
			which = (ChosenCharts)selection.get(0);
		}
		return which;
	}

	// Create a list of resources for the charts of interest
	// This appears to be called when the Find Window is posted and whenever the SFC search scope (all charts, open charts, selected charts) is changed
	private List<ProjectResource> getResources(ChosenCharts which) {
		log.infof("%s.getResources()...", TAG);
		
		List<ProjectResource> results = new ArrayList<>();
		
		log.infof("Gathering ALL SFC resources...");
		allResources = context.getProject().getResourcesOfType(new ResourceType(SFCModule.MODULE_ID, CHART_TYPE_ID));
		log.infof("Found %d resources...", allResources.size());
		for(ProjectResource aRes:allResources) {
			log.infof("%s - found %s:%s", TAG, aRes.getResourceId().getProjectName(),
					aRes.getResourceId().getResourcePath().getPath().toString());
		}
		
		// All
		if( which.equals(ChosenCharts.All) ) {			
			log.infof("%s.getResources() - finding ALL resources...", TAG);
			for(ProjectResource res:allResources) {
				log.infof("%s.getResources() - adding %s...", TAG, res.getResourcePath());
				results.add(res);
			}
		}
		
		// Open
		else if( which.equals(ChosenCharts.Open) ) {
			log.infof("%s.getResources() - finding OPEN resources...", TAG);
			SFCDesignerHook iaSfcHook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
			SfcWorkspace workspace = iaSfcHook.getWorkspace();
			Container root = workspace.getRootPane().getContentPane();
			gatherResources(results,root);
		}
		
		// Selected
		else if( which.equals(ChosenCharts.Selected) ) {
			log.infof("%s.getResources() - finding SELECTED resources...", TAG);
			SfcFolderNode root = getSfcRootFolder();
			TreePath[] paths = root.getSelectionModel().getSelectionPaths();
			if(paths!=null) {
				for(TreePath path:paths) {
					if(path.getLastPathComponent() instanceof SfcNode ) {
						SfcNode node = (SfcNode)path.getLastPathComponent();
						Optional<ProjectResource> res = node.getProjectResource();
						if(!res.isEmpty()) {
							results.add(res.get());
						}
					}
				}
			}
		}
		return results;
	}
	
	// 
	private void gatherResources(List<ProjectResource> results, Container node)  {
		log.infof("%s.gatherResources()...", TAG);
		Component[] components = node.getComponents();                                                                                                      
		for (Component child:components) {
			//log.infof("A: %s", child.getClass().toString());
			if ( child instanceof Container ) {
				//log.infof(" B");
				if( child instanceof SfcDesignPanel ) {
					SfcDesignPanel panel = (SfcDesignPanel)child;
					String resPath = panel.getDesignable().getResourcePath().getFolderPath();
					log.infof("  Found a SFC Design Panel: %s", resPath);
					for( ProjectResource pr:allResources) {
						if( pr.getFolderPath() == resPath) {
							log.infof("   D: Adding");
							results.add(pr);
						}
					}
				}
				//log.infof("   D - making a recursive call...");
				gatherResources(results,(Container)child);
			}
		}
	}

	private SfcFolderNode getSfcRootFolder() {
		log.tracef("%s.getSfcRootFolder()...", TAG);
		AbstractNavTreeNode global = context.getProjectBrowserRoot().getParent();
		SfcFolderNode root = null;
		Enumeration<Object> enumeration = global.children();
		while( enumeration.hasMoreElements()) {
			Object node = enumeration.nextElement();
			if(node instanceof SfcFolderNode ) {
				root = (SfcFolderNode)node;
				break;
			}
		}
		return root;
	}

	// =============================== Popup Window ======================================
	// This is the little popup window that allows the user to select the scope of charts to be searched.
	// This only gets called when the user presses the little dropdown arrow to the right of the "SFC Charts" label
	
	public class SelectionPanel extends JPanel {
		private static final long serialVersionUID = -7374750676641708299L;
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
			add(selected,"wrap" );
			add(open,"wrap");
			log.info("Creating a SelectionPanel...");
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

	// ================================== Change Listener =================================
	// When the open hierarchy changes, "regather" the Ids
}
