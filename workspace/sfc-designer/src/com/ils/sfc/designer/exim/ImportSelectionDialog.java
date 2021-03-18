package com.ils.sfc.designer.exim;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ils.common.ILSProperties;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.ignition.common.Dataset;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import net.miginfocom.swing.MigLayout;

public class ImportSelectionDialog extends JDialog implements ActionListener {
	private final static String CLSS = "ImportSelectionDialog.java";
	private static final String APPROVE_BUTTON = "OK";
	private final static String FILE_CHOOSER_NAME = "FileChooser";
	private static final long serialVersionUID = 8813971334526492335L;
	private static final int DLG_HEIGHT = 80;
	private static final int DLG_WIDTH = 400;
	private JFileChooser fc;
	private final String nameLabel;
	private final LoggerEx log;
	private final Preferences prefs;
	private File filePath = null;

	
	// Doing nothing works quite well.
	public ImportSelectionDialog(JRootPane root) {
		super(SwingUtilities.getWindowAncestor(root));
		String label = "Select .sproj file for import:";
		setModal(true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(new Dimension(DLG_WIDTH,DLG_HEIGHT));
        setAlwaysOnTop(true);
        this.nameLabel = label;
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
		
		//Create a file chooser
	    fc = new JFileChooser();
	    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Chart import projects","sproj");
	    fc.setFileFilter(filter);
	    String startDirectoryName = prefs.get(ILSProperties.PREFERENCES_PROJECT_EXIM_DIRECTORY, "");
	    if(!startDirectoryName.isEmpty() ) {
	    	File startDirectory = new File(startDirectoryName);
		    fc.setCurrentDirectory(startDirectory);
	    }
	    
	    fc.setDialogTitle("Chart Project Importer");
	    fc.setDialogType(JFileChooser.OPEN_DIALOG);
	    fc.setApproveButtonText(APPROVE_BUTTON);
	    fc.setEnabled(false);
	    fc.setMultiSelectionEnabled(false);
	    fc.setName(FILE_CHOOSER_NAME);
	    fc.addActionListener(this);
	    
		columnConstraints = "";
		layoutConstraints = "fillx,ins 10";
		rowConstraints = "";
	    JPanel namePanel = new JPanel(new MigLayout(layoutConstraints,columnConstraints,rowConstraints));
	    JLabel label = new JLabel(nameLabel);
	    namePanel.add(label, "skip");    
	    add(namePanel, "wrap");
	    add(fc, "wrap");
	}
	
	/**
	 * @return the file path that the user selected from the chooser.
	 */
	public File getFilePath() { return filePath; }
	/**
	 * Set the selected file to (presumably) the last path selected
	 */
	public void setSelectedFile(String path) {
		File file = new File(path);
		fc.setSelectedFile(file);
	}
	/**
	 * Set the selected file to (presumably) the last path selected
	 */
	public void setSelectionMode(int mode) {
		fc.setFileSelectionMode(mode);
	}
	/**
	 * We receive events from the file chooser.
	 * For the text field, the command is the field contents.
	 */
	public void actionPerformed(ActionEvent e) {
		log.infof("%s.actionPerformed %s = %s", CLSS,e.getActionCommand(),((JComponent)(e.getSource())).getName());
		if( e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
			filePath = fc.getSelectedFile();
			if( filePath!=null ) {
				// Initialize units. Since this is a lazy initialization, 
				Object[] args = new Object[1];
				args[0] = filePath.getAbsolutePath();
				try {
					PythonCall.IMPORT_CHARTS.exec(args);
				} 
				catch (JythonExecException jee) {
					log.errorf("%s: Error executing importCharts (%s)",CLSS,jee.getMessage());
				}
				catch (Exception ex) {
					log.errorf(CLSS+": Exception importing charts (%s)",ex.getMessage());
				}
			}
			log.infof("%s.actionPerformed set file path to: %s (%s)",CLSS,filePath.getAbsolutePath(),filePath.getParent());
			this.dispose();
		}
		else if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)){
			filePath = null;
			this.dispose();
		}
	}
}
