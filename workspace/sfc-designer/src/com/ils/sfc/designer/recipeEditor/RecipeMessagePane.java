package com.ils.sfc.designer.recipeEditor;

import javax.swing.JTextArea;

import com.ils.sfc.designer.AbstractMessagePane;
import com.ils.sfc.designer.ButtonPanel;

/** A sliding pane for displaying a message */
@SuppressWarnings("serial")
public class RecipeMessagePane extends AbstractMessagePane {
	private RecipeEditorController controller;
	
	public RecipeMessagePane(RecipeEditorController controller) {
		this.controller = controller;
	}
	
	@Override
	public void activate() {
		super.activate();
		controller.slideTo(RecipeEditorController.MESSAGE);
	}
	
	public void doOK() {
		controller.slideTo(returnIndex);
	}
	
}
