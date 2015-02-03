package com.ils.sfc.designer.recipeEditor;

import java.util.List;

import javax.swing.JComponent;

import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.client.designable.DesignableContainer;
import com.inductiveautomation.ignition.designer.designable.DesignableWorkspaceListener;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.event.DockableFrameAdapter;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.docking.event.DockableFrameListener;

/** Provides a dockable frame in the Ignition Designer to hold our recipe data editor. */
@SuppressWarnings("serial")
public class RecipeDataEditorFrame extends com.jidesoft.docking.DockableFrame implements ResourceWorkspaceFrame, DesignableWorkspaceListener {
	private static String KEY = "ILS Recipe Editor";
	private static String TITLE = "Recipe Data";
	private RecipeEditorController controller;
	
	public RecipeDataEditorFrame() {
		super(KEY);
       	setInitSide(DockContext.DOCK_SIDE_WEST);
       	setInitIndex(10);
       	setTitle(TITLE);
       	setTabTitle(TITLE);
       	setSideTitle(TITLE);
      	controller = new RecipeEditorController();
       	setContentPane(controller.getSlidingPane());
	}
	
	public RecipeEditorController getController() {
		return controller;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean isInitiallyVisible() {
		return true;
	}

	public void itemSelectionChanged(List<JComponent> selectedComponents) {
		JComponent selectedComponent = selectedComponents.size() == 1 ? selectedComponents.get(0) : null;
		if(selectedComponent == null) {
			controller.slideTo(RecipeEditorController.EMPTY_PANE);
		}
		else if(selectedComponent instanceof StepComponent) {
			StepComponent stepComponent = (StepComponent) selectedComponent;
			// TODO: get step's "other stuff" property and deserialize JSON to map
			// Map<String,Object> map = Data.jsonToMap(json);
			// Data recipeData = Data.fromMap(map);
			// controller.getBrowser().setRecipeData(recipeData);
			 controller.getBrowser().activate();
		}
		else {
			// nothing?
		}
			
	}
	
	public void containerClosed(DesignableContainer arg0) {}
	public void containerOpened(DesignableContainer arg0) {}
	public void containerSelected(DesignableContainer arg0) {}

}
