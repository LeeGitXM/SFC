package com.ils.sfc.designer.recipeEditor;

import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.ils.sfc.common.recipe.RecipeDataMap;
import com.ils.sfc.common.recipe.objects.S88RecipeDataGroup;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.ils.sfc.util.IlsSfcNames;
import com.inductiveautomation.ignition.client.util.gui.SlidingPane;

public class RecipeEditorController {
	//private SlidingPane slidingPane = new SlidingPane();
	private RecipeDataBrowser browser = new RecipeDataBrowser(this);
	private RecipeObjectCreator creator = new RecipeObjectCreator(this);
	private PropertyEditorPane editor = new PropertyEditorPane(this);
	// create 
	// edit
	
	private static final String BROWSER = "browser";
	private static final String CREATOR = "creator";
	private static final String EDITOR = "editor";
	java.awt.CardLayout cardLayout = new java.awt.CardLayout();
	private JPanel slidingPane = new JPanel();
	
	public RecipeEditorController() {
		// TEMPORARY: use a card layout
		slidingPane.add(browser, BROWSER);
		slidingPane.add(editor, EDITOR);
		slidingPane.add(editor, EDITOR);
	}
	
	public void slideToBrowser() {
		// slidingPane.setSelectedPane(0);
		cardLayout.show(slidingPane, BROWSER);
	}

	public void slideToCreator() {
		cardLayout.show(slidingPane, CREATOR);
		// slidingPane.setSelectedPane(1);
	}

	public void slideToEditor() {
		// slidingPane.setSelectedPane(2);
		cardLayout.show(slidingPane, EDITOR);
	}

	public JPanel getSlidingPane() {
		return slidingPane;
	}

	public RecipeDataBrowser getBrowser() {
		return browser;
	}

	public RecipeObjectCreator getCreator() {
		return creator;
	}

	public PropertyEditorPane getEditor() {
		return editor;
	}

	public static void main(String[] args) {
		RecipeEditorController controller = new RecipeEditorController();
		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		
		RecipeDataMap folder = new RecipeDataMap();
		folder.put(IlsSfcNames.CLASS, S88RecipeDataGroup.className);
		folder.put(IlsSfcNames.KEY, "data");
		List<Map<String,Object>> children = new java.util.ArrayList<Map<String,Object>>();
		folder.put(IlsSfcNames.CHILDREN, children);
		
		RecipeDataMap subMap = new RecipeDataMap();
		children.add(subMap);
		subMap.put(IlsSfcNames.CLASS, "dummy");
		subMap.put(IlsSfcNames.KEY, "anObject");
		subMap.put("foo", "bar");

		subMap = new RecipeDataMap();
		children.add(subMap);
		subMap.put(IlsSfcNames.CLASS, "dummy");
		subMap.put(IlsSfcNames.KEY, "anotherObject");
		subMap.put("foo", "bar");

		RecipeDataBrowser browser = controller.getBrowser();
		browser.setRecipeData(folder);
		frame.setContentPane(controller.getSlidingPane());
		frame.setSize(200,200);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


}
