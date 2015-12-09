/**
 * Copyright 2014. ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser;
import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import com.inductiveautomation.ignition.client.util.EDTUtil;
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
public class SfcBrowserFrame extends DockableFrame implements ResourceWorkspaceFrame, ProjectChangeListener  {
	private static final long serialVersionUID = 4278524462387470494L;
	private static final String TAG = "IlsBrowserFrame";
	private static final String DOCKING_KEY = "SfcChartBrowserFrame";
	private ResourceBundle rb = null;
	private final ChangeEvent changeEvent;
	private final DesignerContext context;
	private final EventListenerList eventListeners;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final JPanel contentPanel;
	private final JPanel legendPanel;
	private ChartTreeDataModel model = null;
	private ChartTreeView view = null;
	
	public SfcBrowserFrame(DesignerContext ctx) {
		super(DOCKING_KEY);  // Pinned icon
		this.context = ctx;
		this.eventListeners = new EventListenerList();
		this.changeEvent = new ChangeEvent(this);
		contentPanel = new JPanel(new BorderLayout());
		legendPanel  = createLegendPanel();
		rb = ResourceBundle.getBundle("com.ils.sfc.designer.browser.browser");
		JTabbedPane mainPanel = createTabPane(contentPanel,legendPanel);
		init(mainPanel);
		EDTUtil.invokeAfterJoin(new Runnable() {
			@Override
			public void run() {
				updateContentPanel();	
			}
		},Thread.currentThread());
	}
	
	public ChartTreeDataModel getModel() { return this.model; }
	public void addChangeListener() {
		
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
		model = new ChartTreeDataModel(context);
		return new ChartTreeView(context,model,BrowserConstants.NAME);
	}
	
	private JPanel createLegendPanel() {
		JPanel lp = new JPanel(new BorderLayout()); 
		JLabel label = new JLabel();
		String html = 
			"<html><head>"
			+ "<title>ILS Chart Browser for SFC's</title>"
			+ "<style type=\"text/css\">"
			// Style applies to all divs - this is the default
			+ "div { background:#fbfbfb; border:3px solid; color:black;margin:10px;padding:5px }" 
            + "h3 { padding-top: 10px; padding-right: 0px; padding-bottom:3px;padding-left:0px; }"  
            + "</style> </head><body>"
            + "<div>"
			+ "<h3>Summary</h3>"
			+ "<P> The <u>ILS Chart Browser</u> provides an alternate layout for Sequential Function Charts. "
			+ "The folder structure used to organize the Inductive Automation chart resources is ignored in favor"
			+ " of a logical tree view that expands the enclosure hierarchy into a single navigable tree. " 
			+ "If an enclosure is referenced more than once in the charts, then it will appear multiple times in the tree."
			+ "<h3>Gestures</h3>"
			+ "<ul>"
			+ "<li>double-click - display the selected chart in the SFC editor. <br>"
			+ "NOTE: This has an effect only if the SFC workspace is current.</li>"
			+ "<li>drag - pan the view.</li>"
			+ "<li>mouse-wheel - zoom the view.</li>"
			+ "<li>right-click - zoom to fit, centers the display and zooms to fit.</li>"
			+ "<li>single-click - selects a node and its ancestors.</li>"
			+ "</ul>"
			+ "<h3>Legend</h3>"
			+ "<ul>"
			+ "<li><span style=\"background-color:#699690;opacity:0.8;\">light-green</span> - most recent selection and ancestors.</li>"
			+ "<li><span style=\"background-color:#8a2c31;opacity:0.8;\">red </span> - step or chart with an error. The browser detects logical loops and unresolved references to enclosures.</li>"
			+ "<li><span style=\"background-color:#ffeb6f;opacity:0.8;\">yellow</span> - encapsulation step</li>"
			+ "</ul>"
			+ "</div>"
			+ "</body></html>";
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
	// =============================== Handle Event Listeners ===================
	public void addChangeListener(ChangeListener l) {
		eventListeners.add(ChangeListener.class,l);
	}
	public void removeChangeListener(ChangeListener l) {
		eventListeners.remove(ChangeListener.class,l);
	}

	// Notify all listeners that have registered interest for
	// notification on this event type.  The event instance
	// is lazily created using the parameters passed into
	// the fire method.

	private void fireStateChanged() {
		// Guaranteed to return a non-null array
		Object[] listnrs = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listnrs.length-2; i>=0; i-=2) {
			if (listnrs[i]==ChangeListener.class) {
				((ChangeListener)listnrs[i+1]).stateChanged(changeEvent);
			}
		}
	}
	
	// =============================== Project Change Listener ===================
	@Override
	public void projectResourceModified(ProjectResource res, ProjectChangeListener.ResourceModification changeType) {
		if( res==null || res.getName()==null || changeType==null ) return;
		log.infof("%s.projectResourceModified: %s = %d (%s:%s)", TAG,changeType.name(),res.getResourceId(),res.getName(),res.getResourceType());
		if( res.getResourceType().equals(BrowserConstants.CHART_RESOURCE_TYPE)) {
			updateContentPanel();
			fireStateChanged();
		}
	}

	@Override
	public void projectUpdated(Project arg0) {
		log.infof("%s.projectResourceUpdated", TAG);
		updateContentPanel();
		fireStateChanged();
	}
}
