package com.ils.sfc.designer.recipeEditor;

import com.ils.sfc.designer.AbstractTagBrowserPane;
import com.ils.sfc.designer.EditorPane;

/** A wrapper for an Ignition tag browser so the user can browse tags instead
 *  of manually typing in the tag path. 
 */
@SuppressWarnings("serial")
public class RecipeTagBrowserPane extends AbstractTagBrowserPane implements EditorPane  {
	private final RecipeEditorController controller;
	
	public RecipeTagBrowserPane(RecipeEditorController controller) {
		this.controller = controller;
	}

	@Override
	public void activate() {	
		super.activate();
		controller.slideTo(RecipeEditorController.TAG_BROWSER);
	}

	public void doAccept() {
		String tagPath = tagBrowser.getTagPath();
		controller.getEditor().getPropertyEditor().setSelectedValue(tagPath);
		controller.getEditor().activate();
	}
}
