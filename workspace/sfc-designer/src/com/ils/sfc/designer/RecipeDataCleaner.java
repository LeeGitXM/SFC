package com.ils.sfc.designer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.ils.sfc.common.IlsSfcRequestHandler;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.chartStructure.ChartStructureCompiler;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectChangeListener;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.api.StepRegistry;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.definitions.ElementDefinition.ElementType;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** This class handles cleaning up orphan recipe data tags resulting from chart step deletion. */
public class RecipeDataCleaner implements ProjectChangeListener {
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataCleaner.class.getName());
	private StepRegistry stepRegistry;
	private DesignerContext context;
	
	public RecipeDataCleaner(DesignerContext context, StepRegistry stepRegistry) {
		this.context = context;
		this.stepRegistry = stepRegistry;
	}
	
	@Override
	public void projectResourceModified(ProjectResource resource,
			ResourceModification mod) {
		try {
			if(resource.getResourceType().equals(ChartStructureCompiler.CHART_RESOURCE_TYPE) && 
			  (mod == ResourceModification.Deleted || mod == ResourceModification.Updated)) {
				cleanupRecipeData(resource, mod == ResourceModification.Deleted);
			}
		}
		catch(Exception e) {
			logger.error("Error cleaning recipe data", e);
		}		
	}

	/** Remove any recipe data for deleted steps. This basically sets up for
	 *  a Python method that does the real work. */
	private void cleanupRecipeData(ProjectResource resource, boolean chartDeleted) throws Exception {
		// collect the step names--if chart deleted, act as if all steps were deleted:
		Set<String> stepNames = chartDeleted ? new HashSet<String>() : getStepNames(resource);
		// call the Python method to do the actual cleanup:
		String chartPath = context.getGlobalProject().getProject().getFolderPath(resource.getResourceId());
		IlsSfcRequestHandler requestHandler = new IlsSfcRequestHandler();
		String provider = requestHandler.getProviderName(false);
		Object[] args = {provider, chartPath, stepNames};
		PythonCall.CLEANUP_RECIPE_DATA.exec(args);
	}

	/** Get the step names in the given chart resource. */
	private Set<String> getStepNames(ProjectResource resource)
			throws IOException, XMLParseException {
		BasicProperty<String> nameProperty = new BasicProperty<String>("name", String.class);
		GZIPInputStream xmlInput = new GZIPInputStream(new ByteArrayInputStream(resource.getData()));
		ChartUIModel uiModel = ChartUIModel.fromXML(xmlInput, stepRegistry );
		Set<String> stepNames = new HashSet<String>();
		for(ChartUIElement element: uiModel.getChartElements()) {
			if(element.getType().equals(ElementType.Step)) {
				String name = element.get(nameProperty);
				stepNames.add(name);
			}
		}
		return stepNames;
	}

	@Override
	public void projectUpdated(Project arg0) {}

}
