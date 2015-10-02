/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.validation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import prefuse.data.Table;

import com.ils.sfc.browser.BrowserConstants;
import com.ils.sfc.browser.ChartTreeDataModel;
import com.ils.sfc.browser.ChartTreeDataModel.EnclosingStep;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/**
 * Panel in a tab that displays a list of all charts without parents. 
 */

public class WhereUsedPanel extends AbstractChartValidationPanel {;
	private static final long serialVersionUID = 2552388376824434427L;
	private static final String[] columnNames = {BrowserConstants.RESID_COL,BrowserConstants.CHART_PATH_COL,BrowserConstants.CALLED_FROM_COL};
	private static final int[]   columnWidths = {BrowserConstants.RESID_WIDTH,BrowserConstants.CHART_PATH_WIDTH,BrowserConstants.REMAINDER_WIDTH};
 
	
	public WhereUsedPanel(DesignerContext ctx,ChartTreeDataModel model) {
		super(ctx,model);
	}
	
	protected String getChartLabel() { return rb.getString("validate.tab.used.label"); }
	protected String[] getColumnNames() { return columnNames; }
	protected int[]    getColumnWidths() { return columnWidths; }
	
	/**
	 * Analyze the model and update the data table.
	 * Create a list of all nodes that are connected to the root
	 */
	public void updateTable() {
		tableModel = new DefaultTableModel(columnNames,0);
		int ncols = columnNames.length;
		Object[] tableRow = new Object[ncols];
		
		Table nodes = dataModel.getNodes();
		List<EnclosingStep> enclosures = dataModel.getEnclosingSteps();
		for( EnclosingStep enclosure:enclosures) {
			int parent = enclosure.getParentRow();
			long resId = nodes.getLong(parent, BrowserConstants.RESOURCE);
			String path   = nodes.getString(parent, BrowserConstants.PATH);
			tableRow[0] = new Long(resId);
			tableRow[1] = enclosure.getChartPath();
			tableRow[2] = path+"/"+enclosure.getStepName();
			tableModel.addRow(tableRow);
		}
		table.setModel(tableModel);

		// Sort the path column
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();

		int columnIndexToSort = table.getColumnModel().getColumnIndex(BrowserConstants.CHART_PATH_COL);
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));

		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
}
