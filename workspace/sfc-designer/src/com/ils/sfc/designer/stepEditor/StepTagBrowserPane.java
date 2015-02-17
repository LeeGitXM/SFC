package com.ils.sfc.designer.stepEditor;

import com.ils.sfc.designer.AbstractTagBrowserPane;
import com.ils.sfc.designer.EditorPane;

/** A wrapper for an Ignition tag browser so the user can browse tags instead
 *  of manually typing in the tag path. 
 */
@SuppressWarnings("serial")
public class StepTagBrowserPane extends AbstractTagBrowserPane implements EditorPane  {
	private final StepEditorController controller;
	
	public StepTagBrowserPane(StepEditorController controller) {
		this.controller = controller;
	}

	@Override
	public void activate() {	
		super.activate();
		controller.slideTo(StepEditorController.TAG_BROWSER);
	}

	public void doAccept() {
		String tagPath = tagBrowser.getTagPath();
		//controller.getEditor().getPropertyEditor().setSelectedValue(tagPath);
		//controller.getEditor().activate();
	}
}
