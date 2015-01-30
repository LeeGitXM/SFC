package com.ils.sfc.common.recipe;

import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.chartStructure.IlsSfcChartStructureCompiler;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.Project;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.StepRegistry;

/**
 * Management of Recipe Data, which is stored as a (single) project resource.
 */
public class RecipeDataManager {
	private static RecipeData _recipeData = null;
	private static ProjectResource projectResource;
	private static Context context;
	private static StepRegistry stepRegistry;
	private static boolean isStale;

	private static LoggerEx logger = LogUtil.getLogger(RecipeDataManager.class.getName());
	
	/** This class abstracts things we need from either the ClientContext or DesignerContext */
	public static abstract class Context {
		public abstract boolean isClient();
		public abstract long createResourceId();  // only callable in Designer context !!!
		public abstract Project getGlobalProject();
	}
	
	/** Get the recipe data, lazily initializing it if necessary. */
	public static RecipeData getData() {
		if(_recipeData == null) {
			loadData();
			if(_recipeData == null) {
				createData();
			}
		}
		else if(isStale) {
			loadData();
			isStale = false;
		}
		return _recipeData;
	}
	
	/** Load the persistent recipe data--if there is none, the _recipeData variable
	 *  will continue to be null. This will over-write any unpersisted changes. 
	 *  Call this to re-load the recipe data if some other process has changed it
	 *  (e.g. a running chart may have changed it in the Gateway, so if a Designer
	 *  is running it will need to refresh the (stale) cached data). */
	public static RecipeData loadData() {
		logger.info("loading recipe data");
		
		projectResource = getResourceProject().getResourceOfType(
			IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE);
		if(projectResource != null) {
			try {
				_recipeData = RecipeData.deserialize(projectResource.getData());
				initializeData();
			} catch (Exception e) {
				logger.error("Error loading recipe data", e);
			}
		}
		
		return _recipeData;
	}
	
	public static boolean isStale() {
		return isStale;
	}
	
	public static void setStale() {
		isStale = true;
	}
	
	public static RecipeData getWorkingCopy() {
		return getData().copy();
	}
	
	private static void initializeData() {
		_recipeData.setCompiler(new IlsSfcChartStructureCompiler(context.getGlobalProject(), stepRegistry));
		_recipeData.compileStructure();
	}
	
	/** Re-initialize the recipe data. */
	public static void clear() {
		projectResource = null;
		_recipeData = new RecipeData();
		for(ProjectResource res: getResourceProject().getResourcesOfType(IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE)) {
			getResourceProject().deleteResource(res.getResourceId());
		}
		createData();
		updateData();
	}
	
	/** Sync the persisted data with the in-memory data. */
	public static void updateData() {
		logger.info("updating recipe data");
		try {
			byte[] bytes = getData().serialize();
			projectResource.setData(bytes);
			getResourceProject().putResource(projectResource);
		} catch (Exception e) {
			logger.error("Could not update recipe data", e);
		}
	}

	public static void setContext(Context _context) {
		RecipeDataManager.context = _context;
	}

	public static void setStepRegistry(StepRegistry stepRegistry) {
		RecipeDataManager.stepRegistry = stepRegistry;
	}

	private static Project getResourceProject() {
		return context.getGlobalProject();
	}
	
	/** Create recipe data for the first time in this Gateway. */
	private static void createData() {
		if(context.isClient()) {
			logger.error("Recipe data cannot be created in Client--only in Designer");
			return;
		}
		logger.info("creating recipe data");
		try {
			_recipeData = new RecipeData();
			initializeData();
			byte[] bytes = _recipeData.serialize();
			final long newId = context.createResourceId();
			projectResource = new ProjectResource(newId,
				IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE,
				IlsSfcModule.RECIPE_RESOURCE_NAME, ApplicationScope.ALL, bytes);
			getResourceProject().putResource(projectResource);
		} catch (Exception e) {
			logger.error("Could not create recipe data", e);
		}
	}
}
