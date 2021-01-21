package com.ils.sfc.designer.exim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.python.core.PyDictionary;

import com.ils.common.ILSProperties;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer;
import com.ils.sfc.common.chartStructure.SimpleHierarchyAnalyzer.ChartInfo;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.StepRegistry;

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
	private final StepRegistry registry;

	
	// Doing nothing works quite well.
	public ExportSelectionDialog(JRootPane root,DesignerContext ctx,StepRegistry reg) {
		super(SwingUtilities.getWindowAncestor(root));
		this.context = ctx;
		this.registry = reg;
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
	    JPanel bottomPanel = new JPanel(new MigLayout("","20%[]10[]10[]10[]",""));
		add(bottomPanel,BorderLayout.SOUTH);
		
		bottomPanel.add(selectAllButton);
		selectAllButton.setPreferredSize(BUTTON_SIZE);
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTree tree = chartSelector.getTree();
				expandAllNodes(tree,0,tree.getRowCount());
				// Skip the first row
				int count = tree.getRowCount()-1;
				if( count>0 ) {
					int[] rows = new int[count];
					int row = 0;
					while( row < count ) {
						rows[row] = row+1;
						row++;
					}
					tree.setSelectionRows(rows);
				}
			}
		});
		bottomPanel.add(selectNoneButton);
		selectNoneButton.setPreferredSize(BUTTON_SIZE);
		selectNoneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreeSelectionModel selectionModel = chartSelector.getSelectionModel();
				selectionModel.clearSelection();
			}
		});
		bottomPanel.add(approveButton);
		approveButton.setPreferredSize(BUTTON_SIZE);
		approveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTree tree = chartSelector.getTree();
				expandAllNodes(tree,0,tree.getRowCount());
				TreeSelectionModel selectionModel = chartSelector.getSelectionModel();
				TreeModel model = tree.getModel();
				SimpleHierarchyAnalyzer analyzer = chartSelector.getAnalyzer();
				int[] rows = selectionModel.getSelectionRows();
				if( rows!=null && rows.length>0 ) {
					// Initialize units. Since this is a lazy initialization, 
					Object[] args = new Object[1];
					PyDictionary dict = new PyDictionary();
					args[0] = dict;
					
					for( int row:rows) {
						TreePath path = tree.getPathForRow(row);
						String chartPath = chartPathForTreePath(path);
						ChartInfo info = analyzer.getChartsByPath().get(chartPath);
						if( info!=null ) {
							try {
								String xml = info.model.toXml(registry);
								dict.put(chartPath, xml);
								//log.info(String.format("%s.approve: %d = %s (%s)",CLSS,row,chartPath,xml));
							}
							catch(Exception ex) {
								log.errorf(CLSS+": Exception getting chart XML (%s)",ex.getMessage());
							}
						}
					}
					
					try {
						PythonCall.EXPORT_CHARTS.exec(args);
					} 
					catch (JythonExecException jee) {
						log.errorf("%s: Error executing importCharts (%s)",CLSS,jee.getMessage());
					}
					catch (Exception ex) {
						log.errorf(CLSS+": Exception importing charts (%s)",ex.getMessage());
					}
				}
				log.infof("%s.actionPerformed: exporting selections.",CLSS);
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
	
	// Recursively expand all nodes.
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for(int i=startingIndex;i<rowCount;++i){
	        tree.expandRow(i);
	    }

	    if(tree.getRowCount()!=rowCount){
	        expandAllNodes(tree,rowCount, tree.getRowCount());
	    }
	}

	/**
	 * We receive events from the file chooser.
	 * For the text field, the command is the field contents.
	 */
	public void actionPerformed(ActionEvent e) {
		log.infof("%s.actionPerformed %s = %s", CLSS,e.getActionCommand(),((JComponent)(e.getSource())).getName());
		this.dispose();
	}
	
	// Create a chart path from a tree path
	private String chartPathForTreePath(TreePath tp) {
		StringBuilder path = new StringBuilder();
		int count = tp.getPathCount();
		int index = 1;
		while(index<count) {
			if( path.length()>0) {
				path.append("/");
			}
			path.append(tp.getPathComponent(index));
			index++;
		}
		return path.toString();
	}
	
}
