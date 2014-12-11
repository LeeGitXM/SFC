package com.ils.sfc.common.recipe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ils.sfc.util.IlsSfcModule;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.project.ProjectResource;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Management of Recipe Data, which is structured as a dictionary and stored as a
 * project resource.
 */
public class RecipeDataManager {
	private static Map<String,Object> _recipeData = null;
	private static ProjectResource projectResource;
	private static DesignerContext context;
	private static final ObjectMapper mapper = new ObjectMapper();

	private static LoggerEx logger = LogUtil.getLogger(RecipeDataManager.class.getName());
	
	public static Map<String,Object> getData() {
		if(_recipeData == null) {
			loadData();
			if(_recipeData == null) {
				createData();
			}
		}
		return _recipeData;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadData() {
		logger.info("loading recipe data");
		List<ProjectResource> resources = context.getProject().getResourcesOfType(
				IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE);
		if(resources.size() >= 1) {
			projectResource = resources.get(0);
			if(resources.size() > 1) {
				logger.error("More than one recipe data resource--deleting extras");
				for(int i = 1; i < resources.size(); i++) {
					ProjectResource res = resources.get(i);
					context.deleteResource(res.getResourceId());
				}
			}
			try {
				_recipeData = mapper.readValue(projectResource.getData(), HashMap.class);
			} catch (Exception e) {
				logger.error("Error loading recipe data", e);
			}
		}
	}
	
	public static void clear() {
		createData();
	}
	
	public static void updateData() {
		logger.info("updating recipe data");
		try {
			String json = mapper.writeValueAsString(_recipeData);
			byte[] bytes = json.getBytes();
			projectResource.setData(bytes);
			context.updateResource(projectResource);
		} catch (Exception e) {
			logger.error("Could not update recipe data", e);
		}
	}

	public static void setDesignerContext(DesignerContext _context) {
		context = _context;
	}
	
	private static void createData() {
		logger.info("creating recipe data");
		try {
			_recipeData = new HashMap<String,Object>();
			final long newId = context.newResourceId();	
			String json = mapper.writeValueAsString(_recipeData);
			byte[] bytes = json.getBytes();
			projectResource = new ProjectResource(newId,
				IlsSfcModule.MODULE_ID, IlsSfcModule.RECIPE_RESOURCE_TYPE,
				IlsSfcModule.RECIPE_RESOURCE_NAME, ApplicationScope.ALL, bytes);
			context.updateResource(projectResource);
		} catch (Exception e) {
			logger.error("Could not create recipe data", e);
		}
	}
}
