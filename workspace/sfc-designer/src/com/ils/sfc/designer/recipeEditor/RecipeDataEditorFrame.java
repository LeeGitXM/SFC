package com.ils.sfc.designer.recipeEditor;

import java.util.List;

import javax.swing.JComponent;

import com.ils.sfc.common.recipe.objects.Data;
import com.inductiveautomation.ignition.client.designable.DesignableContainer;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.designer.designable.DesignableWorkspaceListener;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
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

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public boolean isInitiallyVisible() {
		return true;
	}

	public void itemSelectionChanged(List<JComponent> selectedComponents) {
		controller.commit();
		JComponent selectedComponent = selectedComponents.size() == 1 ? selectedComponents.get(0) : null;
		if(selectedComponent instanceof StepComponent) {
			StepComponent stepComponent = (StepComponent) selectedComponent;
			controller.setElement(stepComponent.getElement());
			controller.getBrowser().activate();
		}
		else {
			// either no step was selected, or it was a multiple selection
			controller.slideTo(RecipeEditorController.EMPTY_PANE);
		}
			
	}
	
	public void containerClosed(DesignableContainer arg0) {
		controller.commit();
	}
	
	public void containerOpened(DesignableContainer arg0) {
		controller.commit();
	}
	
	public void containerSelected(DesignableContainer arg0) {
		controller.commit();
	}

	public RecipeEditorController getController() {
		return controller;
	}

}
