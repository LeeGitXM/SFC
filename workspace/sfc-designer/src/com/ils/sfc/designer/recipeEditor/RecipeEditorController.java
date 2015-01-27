package com.ils.sfc.designer.recipeEditor;

import com.inductiveautomation.ignition.client.util.gui.SlidingPane;

public class RecipeEditorController {
	private SlidingPane slidingPane = new SlidingPane();
	private enum Panes {TREE};
	private RecipeDataBrowser browser = new RecipeDataBrowser(this);
	
	public RecipeEditorController() {
		
	}

	public SlidingPane getSlidingPane() {
		return slidingPane;
	}

}
