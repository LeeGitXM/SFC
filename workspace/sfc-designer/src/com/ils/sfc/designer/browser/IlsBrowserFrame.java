package com.ils.sfc.designer.browser;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.jidesoft.docking.DockableFrame;

/** This is a container for a Perfuse TreeView,
 * used to provide an alternative view of the list of SFC charts.
 */
public class IlsBrowserFrame extends DockableFrame implements ResourceWorkspaceFrame {
	private static final long serialVersionUID = 4278524462387470494L;
	private static final String TAG = "IlsBrowserFrame";
	private static final String DOCKING_KEY = "SfcChartBrowserFrame";
	private static final String TITLE = "Chart Browser";
	private static final String SHORT_TITLE = "Charts";
	private final DesignerContext context;
	private final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	private final JPanel contentPanel;
	
	public IlsBrowserFrame(DesignerContext ctx) {
		super(DOCKING_KEY);  // Pinned icon
		context = ctx;
		contentPanel = new JPanel(new BorderLayout());
		init();
	}

	/**
	 * Initialze the UI
	 */
	private void init() {
		setTitle(TITLE);
		setSideTitle(SHORT_TITLE);
		setTabTitle(SHORT_TITLE);
		setContentPane(contentPanel);
		
		contentPanel.setBorder(BorderFactory.createEtchedBorder());
		JLabel marker = new JLabel("Hi Mom");
		contentPanel.add(marker);
	}
	@Override
	public String getKey() { return DOCKING_KEY; }
	
	@Override
	public boolean isInitiallyVisible() {
		return true;
	}
}
