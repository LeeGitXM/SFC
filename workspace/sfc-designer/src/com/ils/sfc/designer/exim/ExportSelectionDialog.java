package com.ils.sfc.designer.exim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.ils.common.ILSProperties;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

import net.miginfocom.swing.MigLayout;

public class ExportSelectionDialog extends JDialog implements ActionListener {
	private final static String CLSS = "ExportSelectionDialog.java";
	private static final long serialVersionUID = 8813971334526492335L;
	private final Dimension BUTTON_SIZE = new Dimension(25,25);
	private static final String APPROVE_BUTTON = "Export";
	private static final String CANCEL_BUTTON = "Cancel";
	private static final String SELECT_ALL_BUTTON = "Select All";
	private static final String SELECT_NONE_BUTTON = "Select None";
	final JButton approveButton = new JButton(APPROVE_BUTTON);
	final JButton cancelButton = new JButton(CANCEL_BUTTON);
	final JButton selectAllButton = new JButton(SELECT_ALL_BUTTON);
	final JButton selectNoneButton = new JButton(SELECT_NONE_BUTTON);
	private static final int DLG_HEIGHT = 80;
	private static final int DLG_WIDTH = 400;
	private ChartSelectionPane chartSelector;
	private final DesignerContext context;
	private final LoggerEx log;
	private final Preferences prefs;

	
	// Doing nothing works quite well.
	public ExportSelectionDialog(JRootPane root,DesignerContext ctx) {
		super(SwingUtilities.getWindowAncestor(root));
		this.context = ctx;
		setModal(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(new Dimension(DLG_WIDTH,DLG_HEIGHT));
        setAlwaysOnTop(true);
        this.prefs = Preferences.userRoot().node(ILSProperties.ILS_PREFERENCES_DOMAIN);
        this.log = LogUtil.getLogger(getClass().getPackage().getName());
        initialize();
        setLocation(root.getWidth()/2-getWidth()/2, root.getHeight()/2-getHeight()/2);
	}

	/**
	 * Create the content pane and initialize layout.
	 */
	private void initialize() {
		String columnConstraints = "";
		String layoutConstraints = "filly,ins 10";
		String rowConstraints = "";
		setLayout(new MigLayout(layoutConstraints,columnConstraints,rowConstraints));
		
		//Create a selection tree
		chartSelector = new ChartSelectionPane(context);
	    String startDirectoryName = prefs.get(ILSProperties.PREFERENCES_PROJECT_EXIM_DIRECTORY, "");
	    if(!startDirectoryName.isEmpty() ) {
	    	
	    }
	  
	    
		columnConstraints = "";
		layoutConstraints = "fillx,ins 10";
		rowConstraints = "";
	    JPanel namePanel = new JPanel(new MigLayout(layoutConstraints,columnConstraints,rowConstraints));
	    JLabel label = new JLabel("Select charts to export:");
	    namePanel.add(label, "skip");    
	    add(namePanel, "wrap");
	    add(chartSelector, "wrap");
	    JPanel bottomPanel = new JPanel(new MigLayout("","[][][][]",""));
		add(bottomPanel,BorderLayout.SOUTH);
		
		bottomPanel.add(selectAllButton);
		cancelButton.setPreferredSize(BUTTON_SIZE);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		bottomPanel.add(selectNoneButton);
		cancelButton.setPreferredSize(BUTTON_SIZE);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		bottomPanel.add(approveButton);
		approveButton.setPreferredSize(BUTTON_SIZE);
		approveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		bottomPanel.add(cancelButton);
		cancelButton.setPreferredSize(BUTTON_SIZE);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * We receive events from the file chooser.
	 * For the text field, the command is the field contents.
	 */
	public void actionPerformed(ActionEvent e) {
		log.infof("%s.actionPerformed %s = %s", CLSS,e.getActionCommand(),((JComponent)(e.getSource())).getName());
		this.dispose();
	}
}
