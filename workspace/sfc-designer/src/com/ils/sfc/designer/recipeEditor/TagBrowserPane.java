package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.TagBrowser;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/** A wrapper for an Ignition tag browser so the user can browse tags instead
 *  of manually typing in the tag path. 
 */
@SuppressWarnings("serial")
public class TagBrowserPane extends JPanel implements RecipeEditorController.RecipeEditorPane  {
	private final RecipeEditorController controller;
	private final ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false,  RecipeEditorController.background);
	private TagBrowser tagBrowser;
	private DesignerContext context;
	private boolean initialized;
	
	public TagBrowserPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAccept();}			
		});
	}

	public void setContext(DesignerContext context) {
		this.context = context;
	}

	@Override
	public void activate() {	
		// the tag browser's internal icons are not added until very late,
		// so to avoid errors we need to delay creating it until the last minute
		if(!initialized) {
			add(buttonPanel, BorderLayout.NORTH);
			tagBrowser = new TagBrowser(context);
			add(tagBrowser, BorderLayout.CENTER);
			validate();
		}
		controller.slideTo(RecipeEditorController.TAG_BROWSER);
	}

	public void doAccept() {
		controller.getEditor().getPropertyEditor().setStringEditValue(tagBrowser.getTagPath());
		controller.getEditor().activate();
	}
}
