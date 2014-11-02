/**
 * Copyright 2014. ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectChangeListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.jidesoft.docking.DockableFrame;

/** This is a DockableFrame container for a Perfuse TreeView,
 * used to provide an alternative view of the list of SFC charts.
 * 
 * It is the controller for the tree model and Perfuse view.
 */
public class IlsBrowserFrame extends DockableFrame implements ResourceWorkspaceFrame, ProjectChangeListener  {
	private static final long serialVersionUID = 4278524462387470494L;
	private static final String TAG = "IlsBrowserFrame";
	private static final String DOCKING_KEY = "SfcChartBrowserFrame";
	private static final String TITLE = "Chart Browser";
	private static final String SHORT_TITLE = "Charts";
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final JPanel contentPanel;
	private ChartTreeView view = null;
	
	public IlsBrowserFrame(DesignerContext ctx) {
		super(DOCKING_KEY);  // Pinned icon
		context = ctx;
		contentPanel = new JPanel(new BorderLayout());
		init();
	}

	/**
	 * Initialize the UI
	 */
	private void init() {
		setTitle(TITLE);
		setSideTitle(SHORT_TITLE);
		setTabTitle(SHORT_TITLE);
		setContentPane(contentPanel);
		
		contentPanel.setBorder(BorderFactory.createEtchedBorder());
		updateChartView();
	}
	
	private void updateChartView() {
		contentPanel.removeAll();
		contentPanel.invalidate();
		this.view = createChartTreeView();
		contentPanel.add(view,BorderLayout.CENTER);
		contentPanel.validate();
		contentPanel.setVisible(true);
	}
	
	private ChartTreeView createChartTreeView() {
		log.infof("%s.createChartTreeView: New view ....",TAG);
		ChartTreeDataModel model = new ChartTreeDataModel(context);
		return new ChartTreeView(model,ChartTreeDataModel.NAME);
	}
	
	@Override
	public String getKey() { return DOCKING_KEY; }
	
	@Override
	public boolean isInitiallyVisible() {
		return true;
	}
	
	// =============================== Project Change Listener ===================
		@Override
		public void projectResourceModified(ProjectResource res, ProjectChangeListener.ResourceModification changeType) {
			log.infof("%s.projectResourceModified: %s = %d (%s:%s)", TAG,changeType.name(),res.getResourceId(),res.getName(),res.getResourceType());
			if( res.getResourceType().equals(ChartTreeDataModel.CHART_RESOURCE_TYPE)) {
				updateChartView();
			}
		}

		@Override
		public void projectUpdated(Project arg0) {
			log.infof("%s.projectResourceUpdated", TAG);
			updateChartView();
		}
}
