/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser.validation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.table.DefaultTableModel;

import prefuse.data.Table;

import com.ils.sfc.designer.browser.BrowserConstants;
import com.ils.sfc.designer.browser.ChartTreeDataModel;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults.CompilationError;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/**
 * Panel in a tab that displays a list of all charts without parents. 
 */

public class ChartErrorsPanel extends AbstractChartValidationPanel {
	private static final long serialVersionUID = 2112388376824434427L;
	private static final String[] columnNames = {BrowserConstants.RESID_COL,BrowserConstants.CHART_PATH_COL,BrowserConstants.ERROR_COL};
	private static final int[]   columnWidths = {BrowserConstants.RESID_WIDTH,BrowserConstants.CHART_PATH_WIDTH,BrowserConstants.REMAINDER_WIDTH};
	
	public ChartErrorsPanel(DesignerContext ctx,ChartTreeDataModel model) {
		super(ctx,model);  
	}
	
	protected String getChartLabel() { return rb.getString("validate.tab.cerror.label"); }
	protected String[] getColumnNames() { return columnNames; }
	protected int[]    getColumnWidths() { return columnWidths; }
	
	/**
	 * Analyze the model and update the data table.
	 * Create a list of all charts with errors or warnings
	 */
	public void updateTable() {
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		Table nodes = dataModel.getNodes();
		tableModel = new DefaultTableModel(columnNames,0);
		int ncols = columnNames.length;
		Object[] tableRow = new Object[ncols];
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResources();
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(BrowserConstants.CHART_RESOURCE_TYPE)) {
				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if( ccr.getErrorCount() > 0 ) {
						tableRow[0] = new Long(res.getResourceId());
						tableRow[1] = getPathForResource(res,nodes);
						tableRow[2] = getErrorsAsString(true,ccr.getErrors());
						tableModel.addRow(tableRow);
					}
					else if(ccr.getWarningCount() > 0 ) {
						tableRow[0] = new Long(res.getResourceId());
						tableRow[1] = getPathForResource(res,nodes);
						tableRow[2] = getErrorsAsString(false,ccr.getErrors());
						tableModel.addRow(tableRow);
					}
				}
				catch(IOException ioe ) {
					log.warnf("%s.updateTable: IO Exception for %s (%s)", TAG,res.getName(),ioe.getLocalizedMessage());
					tableRow[0] = new Long(res.getResourceId());
					tableRow[1] = getPathForResource(res,nodes);
					tableRow[2] = "IO Exception ("+ioe.getLocalizedMessage()+")";
					tableModel.addRow(tableRow);
				}
				catch(NumberFormatException nfe ) {
					log.warnf("%s.updateTable: Chart instantiation error for %s (%s)", TAG,res.getName(),nfe.getLocalizedMessage());
					tableRow[0] = new Long(res.getResourceId());
					tableRow[1] = getPathForResource(res,nodes);
					tableRow[2] = "Number Format Exception ("+nfe.getLocalizedMessage()+")";
					tableModel.addRow(tableRow);
				}
				catch(XMLParseException xpe ) {
					log.warnf("%s.updateTable: Parse Exception for %s (%s)", TAG,res.getName(),xpe.getLocalizedMessage());
					tableRow[0] = new Long(res.getResourceId());
					tableRow[1] = getPathForResource(res,nodes);
					tableRow[2] = "Parse Exception ("+xpe.getLocalizedMessage()+")";
					tableModel.addRow(tableRow);
				}
				catch(Exception ex) {
					log.warn(TAG+".updateTable: Unhandled exception for "+ res.getName(),ex);
					tableRow[0] = new Long(res.getResourceId());
					tableRow[1] = getPathForResource(res,nodes);
					tableRow[2] = "Unhandled Exception ("+ex.getLocalizedMessage()+")";
					tableModel.addRow(tableRow);
				}
			}

			table.setModel(tableModel);
		}
	}
	//================================= Helper Methods ==============================
	
	private String getErrorsAsString(boolean isError,List<CompilationError> errors) {
		StringBuilder text = new StringBuilder("Warning: ");
		if( isError ) text = new StringBuilder("Error: ");
		String msg = "";
		for(CompilationError err:errors) {
			if( err.getMessage().equalsIgnoreCase(msg)) continue;
			msg = err.getMessage();
			text.append(msg);
		}
		return text.toString();
	}
}
