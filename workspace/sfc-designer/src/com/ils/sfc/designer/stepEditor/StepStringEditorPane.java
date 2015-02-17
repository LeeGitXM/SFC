package com.ils.sfc.designer.stepEditor;

import com.ils.sfc.designer.AbstractStringEditorPane;
import com.ils.sfc.designer.EditorPane;

/** Basically just a big text area for editing long strings. */
@SuppressWarnings("serial")
public class StepStringEditorPane extends AbstractStringEditorPane implements EditorPane {
	private StepEditorController controller;
	
	public StepStringEditorPane(StepEditorController controller) {
		this.controller = controller;
	}

	/** Do anything that needs to be done before re-showing this. */
	public void activate() {
		super.activate();
		controller.slideTo(StepEditorController.TEXT_EDITOR);
	}

	public void doOK() {
		controller.getPropertyEditor().getPropertyEditor().setSelectedValue(textField.getText());
		controller.getPropertyEditor().activate();
	}
	
}

