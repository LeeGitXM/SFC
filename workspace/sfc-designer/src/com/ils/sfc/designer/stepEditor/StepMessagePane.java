package com.ils.sfc.designer.stepEditor;

import com.ils.sfc.designer.AbstractMessagePane;

/** A sliding pane for displaying a message */
@SuppressWarnings("serial")
public class StepMessagePane extends AbstractMessagePane {
	private StepEditorController controller;
	
	public StepMessagePane(StepEditorController controller) {
		this.controller = controller;
	}
	
	@Override
	public void activate() {
		super.activate();
		controller.slideTo(StepEditorController.MESSAGE);
	}
	
	public void doOK() {
		controller.slideTo(returnIndex);
	}
	
}
