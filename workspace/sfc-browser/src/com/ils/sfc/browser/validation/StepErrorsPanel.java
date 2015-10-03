/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser.validation;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.swing.table.DefaultTableModel;

import prefuse.data.Table;

import com.ils.sfc.browser.BrowserConstants;
import com.ils.sfc.browser.ChartTreeDataModel;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.client.api.ClientStepRegistry;
import com.inductiveautomation.sfc.client.api.ClientStepRegistryProvider;
import com.inductiveautomation.sfc.definitions.ChartDefinition;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartCompiler;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/**
 * Panel in a tab that displays a list of all charts without parents. 
 */

public class StepErrorsPanel extends AbstractChartValidationPanel {
	private static final long serialVersionUID = 2332388376824434427L;
	private static final String[] columnNames = {BrowserConstants.RESID_COL,BrowserConstants.STEP_PATH_COL,BrowserConstants.ERROR_COL};
	private static final int[]   columnWidths = {BrowserConstants.RESID_WIDTH,BrowserConstants.STEP_PATH_WIDTH,BrowserConstants.REMAINDER_WIDTH};

	
	public StepErrorsPanel(DesignerContext ctx,ChartTreeDataModel model) {
		super(ctx,model);  
	}
	
	protected String getChartLabel() { return rb.getString("validate.tab.serror.label"); }
	protected String[] getColumnNames() { return columnNames; }
	protected int[]    getColumnWidths() { return columnWidths; }
	
	/**
	 * Analyze the model and update the data table.
	 * Create a list of all steps with errors or warnings. We skip over any charts
	 * that might have errors as these are handled in another tab.
	 * 
	 * For now we limit ourselves to enclosures with faulty chart paths.
	 */
	public void updateTable() {
		Object iaSfcHook = context.getModule(SFCModule.MODULE_ID);
		ClientStepRegistry registry = ((ClientStepRegistryProvider)iaSfcHook).getStepRegistry();
		Table nodes = dataModel.getNodes();
		Map<String,Integer> rowLookup = dataModel.getRowLookup();
		tableModel = new DefaultTableModel(columnNames,0);
		List<ProjectResource> resources = context.getGlobalProject().getProject().getResources();
		for(ProjectResource res:resources) {
			if( res.getResourceType().equals(BrowserConstants.CHART_RESOURCE_TYPE)) {
				try {
					GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(res.getData()));
					ChartUIModel chartModel = ChartUIModel.fromXML(xmlInput,registry );
					ChartCompiler compiler = new ChartCompiler(chartModel,registry);
					ChartCompilationResults ccr = compiler.compile();
					if( ccr.isSuccessful() ) {
						ChartDefinition definition = ccr.getChartDefinition();
						traverseSteps(res,nodes,rowLookup,tableModel,definition.getBeginElement().getNextElements());
					}
				}
				catch(Exception ignore) {}
			}
			table.setModel(tableModel);
		}
	}
	
	// Check the steps for any problems with attributes
	private void traverseSteps(ProjectResource chartResource,Table nodes,Map<String,Integer> lookup,DefaultTableModel model,List<ElementDefinition> steps) {
		int ncols = columnNames.length;
		Object[] tableRow = new Object[ncols];
		
		for( ElementDefinition step:steps) {
			if( step instanceof StepDefinition ) {
				StepDefinition stepDef = (StepDefinition)step;
				// Custom enclosures don't inherit from Enclosing step, but they all must have a path.
				if( stepDef.getFactoryId().equals(EnclosingStepProperties.FACTORY_ID) ||
						stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH)!=null ) {
					String name = stepDef.getProperties().get(EnclosingStepProperties.Name);
					String path = stepDef.getProperties().get(EnclosingStepProperties.CHART_PATH);
					// See if the chart-path points to an existing chart
					if( lookup.get(path)==null ) {
						String chartRow = getPathForResource(chartResource,nodes);
						tableRow[0] = new Long(chartResource.getResourceId());
						tableRow[1] = chartRow+"/"+name;
						tableRow[2] = String.format("%s (%s) does not exist",EnclosingStepProperties.CHART_PATH,path);
						model.addRow(tableRow);
					}
				}
			}

		}
	}
}
