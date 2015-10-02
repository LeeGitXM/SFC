/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.validation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ils.sfc.browser.ChartTreeDataModel;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Scan all charts defined in the gateway and report any issues. The
 * dialog consists of the following panels:
 *      RootCharts  - display a list of charts that have no parents
 *      ChartErrors - display a list of charts with compile errors
 *      StepErrors  - display a list of chart steps with errors
 *      WhereUsed   - display a list of charts that are referenced by enclosures
 */

public class ValidationDialog extends JDialog implements ChangeListener{
	private static String TAG = "ValidationDialog";
	private final LoggerEx log;
	private static final long serialVersionUID = 2002388376824434427L;
	private final int DIALOG_HEIGHT = 300;
	private final int DIALOG_WIDTH = 600;
	private final ResourceBundle rb;
	private final ChartTreeDataModel chartDataModel;
	private JTabbedPane tabbedPane = null;
	private final ChartErrorsPanel chartErrorsPanel;
	private final RootChartsPanel rootChartsPanel;
	private final StepErrorsPanel stepErrorsPanel;
	private final WhereUsedPanel whereUsedPanel;
	
	public ValidationDialog(DesignerContext ctx,ChartTreeDataModel model) {
		super(ctx.getFrame());
		this.chartDataModel = model;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.setTitle("SFC Chart Validity Analysis");
		this.rb = ResourceBundle.getBundle("com.ils.sfc.browser.browser");  // browser.properties
		setModal(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(DIALOG_WIDTH,DIALOG_HEIGHT));
		chartErrorsPanel = new ChartErrorsPanel(ctx,chartDataModel);
		rootChartsPanel  = new RootChartsPanel(ctx,chartDataModel);;
		stepErrorsPanel  = new StepErrorsPanel(ctx,chartDataModel);;
		whereUsedPanel   = new WhereUsedPanel(ctx,chartDataModel);;
		initialize();
	}
	
	private void initialize() {
		JPanel mainPanel = new JPanel();
		// The center panel is a TabbedPane
		mainPanel.setLayout(new BorderLayout());
		tabbedPane = createTabPane();
		mainPanel.add(tabbedPane,BorderLayout.CENTER);
		
		// Place a button panel at the bottom to dismiss the dialog
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Dismiss");
		buttonPanel.add(okButton, "");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		setContentPane(mainPanel);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
	}
	
	private JTabbedPane createTabPane() {
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
		pane.addTab(rb.getString("validate.tab.root"),null,rootChartsPanel,
		            rb.getString("validate.tab.root.tooltip"));
		pane.addTab(rb.getString("validate.tab.cerror"),null,chartErrorsPanel,
                    rb.getString("validate.tab.cerror.tooltip"));
		pane.addTab(rb.getString("validate.tab.serror"),null,stepErrorsPanel,
                    rb.getString("validate.tab.serror.tooltip"));
		pane.addTab(rb.getString("validate.tab.used"),null,whereUsedPanel,
                    rb.getString("validate.tab.used.tooltip"));
		pane.setSelectedIndex(0);
		
		return pane;
	}

	/**
	 * Update the UI per most current information from the controller
	 */
	private void updateInformation() {
		chartErrorsPanel.updateTable();
		rootChartsPanel.updateTable();
		stepErrorsPanel.updateTable();
		whereUsedPanel.updateTable();
	}
	
	// =============================== Change Listener ===================
	/**
	 * The DataTree has changed. Re-read the data structures 
	 */
		@Override
		public void stateChanged(ChangeEvent e) {
			updateInformation();
			
		}
}
