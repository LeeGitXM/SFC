package com.ils.sfc.designer.recipeEditor;

import java.util.List;

import javax.swing.JComponent;

import com.inductiveautomation.ignition.client.designable.DesignableContainer;
import com.inductiveautomation.ignition.designer.designable.DesignableWorkspaceListener;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.ignition.designer.model.ResourceWorkspaceFrame;
import com.inductiveautomation.sfc.client.ui.StepComponent;
import com.inductiveautomation.sfc.designer.workspace.SFCWorkspace;
import com.jidesoft.docking.DockContext;

/** Provides a dockable frame in the Ignition Designer to hold our recipe data editor. */
@SuppressWarnings("serial")
public class RecipeEditorFrame extends com.jidesoft.docking.DockableFrame implements ResourceWorkspaceFrame, DesignableWorkspaceListener {
	private static String KEY = "ILS Recipe Editor";
	private static String TITLE = "Recipe Data";
	private RecipeEditorController controller;
	private SFCWorkspace sfcWorkspace;
	private DesignerContext context;
	
	public RecipeEditorFrame(DesignerContext ctx, SFCWorkspace sfcWorkspace) {
		super(KEY);
		this.sfcWorkspace = sfcWorkspace;
		context = ctx;
       	setInitSide(DockContext.DOCK_SIDE_WEST);
       	setInitIndex(10);
       	setTitle(TITLE);
       	setTabTitle(TITLE);
       	setSideTitle(TITLE);
      	controller = new RecipeEditorController(ctx);
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
			long resourceId = sfcWorkspace.getSelectedContainer().getResourceId();
			String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
			controller.setElement(stepComponent.getElement(), chartPath);
			controller.readRecipeDataFromTags();
			controller.getBrowser().activate(-1);
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
