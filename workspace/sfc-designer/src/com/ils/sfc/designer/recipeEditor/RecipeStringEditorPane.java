package com.ils.sfc.designer.recipeEditor;

import com.ils.sfc.designer.AbstractStringEditorPane;
import com.ils.sfc.designer.EditorPane;

/** Basically just a big text area for editing long strings. */
@SuppressWarnings("serial")
public class RecipeStringEditorPane extends AbstractStringEditorPane implements EditorPane {
	private RecipeEditorController controller;
	
	public RecipeStringEditorPane(RecipeEditorController controller) {
		this.controller = controller;
	}

	/** Do anything that needs to be done before re-showing this. */
	public void activate() {
		super.activate();
		controller.slideTo(RecipeEditorController.TEXT_EDITOR);
	}

	public void doOK() {
		controller.getEditor().getPropertyEditor().setSelectedValue(textField.getText());
		controller.getEditor().activate();
	}
	
}
