/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import net.miginfocom.swing.MigLayout;
import prefuse.data.Table;

import com.ils.sfc.browser.BrowserConstants;
import com.ils.sfc.browser.ChartTreeDataModel;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;

/**
 * This represents a panel in a tab that is part of the chart validation dialog.
 * Each panel contains a single table with somw particular validation.  
 */
public abstract class AbstractChartValidationPanel extends JPanel {
	private static final long serialVersionUID = 166875010095235952L;
	protected static String TAG = "AbstractChartValidationPanel";
	protected final LoggerEx log;
	protected final int PANEL_HEIGHT = 300;
	protected final int PANEL_WIDTH = 600;
	protected final int TABLE_HEIGHT = 500;
	protected final int TABLE_WIDTH = 2000;
	protected final ResourceBundle rb;
	protected final DesignerContext context;
	protected final ChartTreeDataModel dataModel;
	protected DefaultTableModel tableModel = null;
	protected JTable table = null;
	protected JPanel internalPanel = null;
	
	public AbstractChartValidationPanel(DesignerContext ctx,ChartTreeDataModel model) {
		this.context = ctx;
		this.dataModel = model;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.rb = ResourceBundle.getBundle("com.ils.sfc.browser.browser");  // browser.properties
		this.setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		initialize();
		updateTable();  
	}
	
	private void initialize() {
		setLayout(new MigLayout("ins 10","",""));
		internalPanel = createListPanel();
		add(internalPanel,BorderLayout.CENTER);
	}
	
	abstract protected String   getChartLabel();  
	abstract protected String[] getColumnNames();
	abstract protected int[]    getColumnWidths();
	
	/**
	 * Analyze the model and update the data table.
	 * Create a list of all nodes that are connected to the root
	 */
	abstract public void updateTable();
	
	
	
	/**
	 * A list add panel is a panel appending a string element in the list. It contains:-
	 *        Scroll pane with the table, label at the top.
	 */
	@SuppressWarnings("serial")
	private JPanel createListPanel()  {
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new MigLayout("ins 2,fillx,filly","[20][]","[20][]"));
		
		JLabel label = new JLabel(getChartLabel());
		label.setFont(new Font("Tahoma", Font.PLAIN, 18));
		label.setForeground(Color.BLUE);
		outerPanel.add(label,"wrap");
		
		
		table = new JTable();
		updateTable();  // Fills with data, sets columns
		
		// This doesn't really work to set initial column sizes -- I've tried dozens of combinations
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));

		TableColumnModel tcm = table.getColumnModel();
		int[] columnWidths = getColumnWidths();
		int ncols = tcm.getColumnCount();
		int col = 0;
		while( col<ncols ) {
			tcm.getColumn(col).setPreferredWidth(columnWidths[col]);
			col++;
		}
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        // This trick makes the whole row selectable
        table.getColumnModel().setSelectionModel( new DefaultListSelectionModel() {
			@Override
            public int getLeadSelectionIndex() {
                return -1;
            }
        });
       
        // This assumes that the first column is the resourceId. 
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // On a click we get the chart path and display it.
            	SFCDesignerHook hook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
            	int baseRow = table.convertRowIndexToModel(table.getSelectedRow());
            	String val0 = tableModel.getValueAt(baseRow,0).toString();
            	if( val0!=null) {
            		try {
            			long resId = Long.parseLong(val0);
            			if( resId>=0) hook.getWorkspace().openChart(resId);
            		}
            		catch(NumberFormatException nfe) {}   // A blank, presumably
            	}
            	else {
            		log.warnf("%s.valueChanged: tableModel returned a null in column 0", TAG);
            	}
            }
        });

        table.setAutoCreateRowSorter(true);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        table.setFillsViewportHeight(true);
        outerPanel.add(tablePane, "span,wrap");
		return outerPanel;
	}
	
	//================================= Helper Methods ==============================
	/**
	 * Loop through the nodelist looking for the indicated resource. Once found, return
	 * the path. If not found, Simple return the resource name.
	 * @param res
	 * @param nodes
	 * @return
	 */
	protected String getPathForResource(ProjectResource res,Table nodes) {
		String path = res.getName();
		int rows = nodes.getRowCount();
		int row = 0;
		long resid = res.getResourceId();
		while( row<rows ) {
			long id = nodes.getLong(row, BrowserConstants.RESOURCE);
			if( id==resid) {
				path = nodes.getString(row, BrowserConstants.PATH);
				break;
			}
			row++;
		}
		return path;
	}
	/**
	 * Loop through the nodelist looking for the indicated resource. Once found, return
	 * the path. If not found, Simple return the resource name.
	 * @param res
	 * @param nodes
	 * @return
	 */
	protected long getResourceForPath(String path,Table nodes) {
		int rows = nodes.getRowCount();
		int row = 0;
		Long resid = new Long(-1);
		while( row<rows ) {
			String nodePath = nodes.getString(row, BrowserConstants.PATH);
			if( path.equalsIgnoreCase(nodePath)) {
				resid = nodes.getLong(row, BrowserConstants.RESOURCE);
				if( resid<0 ) continue;  // We've found an enclosing step
				break;
			}
			row++;
		}
		log.infof("%s.getResourceForPath: %s (%d)", TAG,path,resid.longValue());
		return resid.longValue();
	}
}
