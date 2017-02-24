package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;

import javax.swing.JPopupMenu;

import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.ils.sfc.designer.stepEditor.StepEditorController;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** A "bridge" class that allows us to plug our own SFC Step property editor into Ignition. */
@SuppressWarnings("serial")
public class IlsStepEditor  extends AbstractStepEditor {
	private StepEditorController stepEditorController;

	public static class Factory implements StepConfigFactory {
		DesignerContext context;
		IlsStepEditor editor;

		public Factory(DesignerContext context) {
			this.context = context;
		}

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new IlsStepEditor(model, context);
			}
			editor.setElement(element);
			return editor;
		}

		@Override
		public void initPopupMenu(ChartUIModel arg0, ChartUIElement arg1, JPopupMenu arg2) {
			// TODO Auto-generated method stub
			
		}
	}

	protected IlsStepEditor(ChartUIModel chartModel, DesignerContext context) {
		super(new BorderLayout(), chartModel);
		// TODO: this is cryptic--can we encapsulate it in a better place?
		long resourceId = IlsSfcDesignerHook.getSfcWorkspace().getSelectedContainer().getResourceId();
		String chartPath = context.getGlobalProject().getProject().getFolderPath(resourceId);
		stepEditorController = new StepEditorController(context, chartPath);
		add(stepEditorController.getSlidingPane());
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);
		stepEditorController.setElement(element);
	}

	@Override
	public void commitEdit() {
		stepEditorController.commitEdit();
	}
	
	public <T> void set(Property<T> property, T value) {
		element.set(property, value);
		setElement(element);
	}

}
