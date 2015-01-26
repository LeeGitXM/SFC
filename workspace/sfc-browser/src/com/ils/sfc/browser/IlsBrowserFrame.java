/**
 * Copyright 2014. ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser;
import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
	private ResourceBundle rb = null;
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final JPanel contentPanel;
	private final JPanel legendPanel;
	private ChartTreeView view = null;
	
	public IlsBrowserFrame(DesignerContext ctx) {
		super(DOCKING_KEY);  // Pinned icon
		context = ctx;
		contentPanel = new JPanel(new BorderLayout());
		legendPanel  = createLegendPanel();
		rb = ResourceBundle.getBundle("com.ils.sfc.browser.browser");
		JTabbedPane mainPanel = createTabPane(contentPanel,legendPanel);
		init(mainPanel);
		updateContentPanel();
		context.addProjectChangeListener(this);
	}

	/**
	 * Initialize the tabbed panel as the window's content pane
	 */
	private void init(JTabbedPane mainPanel) {
		setTitle(rb.getString("chart.tab.title"));
		setSideTitle(rb.getString("chart.tab.short.title"));
		setTabTitle(rb.getString("chart.tab.short.title"));
		setContentPane(mainPanel);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
	}
	
	private JTabbedPane createTabPane(JPanel content,JPanel legend) {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.addTab(rb.getString("main.tab.tree"),null,content,
		                  rb.getString("main.tab.tree.tooltip"));
		tabbedPane.addTab(rb.getString("main.tab.legend"),null,legend,
                          rb.getString("main.tab.legend.tooltip"));
		tabbedPane.setSelectedIndex(0);
		
		return tabbedPane;
	}
	private void updateContentPanel() {
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
		return new ChartTreeView(context,model,BrowserConstants.NAME);
	}
	
	private JPanel createLegendPanel() {
		JPanel lp = new JPanel(new BorderLayout());
		JLabel label = new JLabel();
		String html = 
			"<html><head>"
			+ "<title>ILS Chart Browser for SFC's</title></head>"
			+ "<H2>Summary</H2>"
			+ "<P> The <UL>ILS Chart Browser</UL> provides a logical view of what may be a complicated Sequential Function Chart diagram."
			+ "<H2>Gestures</H2>"
			+ "<H2>Legend</H2>"
			+ "</html>";
		label.setText(html);
		lp.add(label,BorderLayout.CENTER);
		return lp;
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
		if( res.getResourceType().equals(BrowserConstants.CHART_RESOURCE_TYPE)) {
			updateContentPanel();
		}
	}

	@Override
	public void projectUpdated(Project arg0) {
		log.infof("%s.projectResourceUpdated", TAG);
		updateContentPanel();
	}
}
