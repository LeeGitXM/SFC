package com.ils.sfc.designer;

import java.awt.BorderLayout;

import com.ils.sfc.designer.editor.PropertyEditor;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** An editor for all ILS step types */
public class IlsStepEditor extends AbstractStepEditor {

	private static final long serialVersionUID = 1L;

	private PropertyEditor propertyEditor = new PropertyEditor();
	
	public IlsStepEditor(ChartUIModel chartModel) {
		super(new BorderLayout(), chartModel);
		add(propertyEditor);
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);
		propertyEditor.setElement(element);
	}

	@Override
	public void commitEdit() {
	}

	public static class Factory implements StepConfigFactory {

		IlsStepEditor editor;

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new IlsStepEditor(model);
			}
			editor.setElement(element);
			return editor;
		}

	}
}
