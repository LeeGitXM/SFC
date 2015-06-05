package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;

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
				editor = new IlsStepEditor(model, element, context);
			}
			editor.setElement(element);
			return editor;
		}

	}

	protected IlsStepEditor(ChartUIModel chartModel, ChartUIElement element, DesignerContext context) {
		super(new BorderLayout(), chartModel);
		stepEditorController = new StepEditorController(context);
		add(stepEditorController.getSlidingPane());
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);
		stepEditorController.setElement(element);
	}

	@Override
	public void commitEdit() {
		// TODO: what to do here??
	}
	
	public <T> void set(Property<T> property, T value) {
		element.set(property, value);
		setElement(element);
	}

}
