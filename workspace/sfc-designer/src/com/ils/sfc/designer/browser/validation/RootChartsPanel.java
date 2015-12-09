/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser.validation;

import javax.swing.table.DefaultTableModel;

import prefuse.data.Graph;
import prefuse.data.Table;

import com.ils.sfc.designer.browser.BrowserConstants;
import com.ils.sfc.designer.browser.ChartTreeDataModel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Panel in a tab that displays a list of all charts without parents. 
 */

public class RootChartsPanel extends AbstractChartValidationPanel {
	private static final long serialVersionUID = 5295796590362824609L;
	private static final String[] columnNames = {BrowserConstants.RESID_COL,BrowserConstants.CHART_PATH_COL};
	private static final int[]   columnWidths = {BrowserConstants.RESID_WIDTH,BrowserConstants.STEP_PATH_WIDTH};

	public RootChartsPanel(DesignerContext ctx,ChartTreeDataModel model) {
		super(ctx,model);
	}
	
	protected String getChartLabel() { return rb.getString("validate.tab.root.label"); }
	protected String[] getColumnNames() { return columnNames; }
	protected int[]    getColumnWidths() { return columnWidths; }
	
	/**
	 * Analyze the model and update the data table.
	 * Create a list of all nodes that are connected to the root
	 */
	public void updateTable() {
		tableModel = new DefaultTableModel(columnNames,0);
		Table edges = dataModel.getEdges();
		Table nodes = dataModel.getNodes();
		int rows = edges.getRowCount();
		int row  = 0;
		int ncols = columnNames.length;
		Object[] tableRow = new Object[ncols];
		
		// Simply add the path of any rows with a parent of ROOT
		while( row<rows ) {
			int source = edges.getInt(row, Graph.DEFAULT_SOURCE_KEY);
			int destination = edges.getInt(row, Graph.DEFAULT_TARGET_KEY);
			if( source==ChartTreeDataModel.ROOT_ROW ) {
				if( destination==source ) continue;  // The root node
				long resId = nodes.getLong(destination, BrowserConstants.RESOURCE);
				//String folder = nodes.getString(destination, BrowserConstants.NAME);
				String path   = nodes.getString(destination, BrowserConstants.PATH);
				tableRow[0] = new Long(resId);
				tableRow[1] = path;
				tableModel.addRow(tableRow);
			}
			row++;
		}
		table.setModel(tableModel);
	}
}
