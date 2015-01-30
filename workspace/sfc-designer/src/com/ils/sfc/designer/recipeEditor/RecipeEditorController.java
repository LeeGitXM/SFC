package com.ils.sfc.designer.recipeEditor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.recipe.RecipeDataMap;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.inductiveautomation.ignition.client.util.gui.SlidingPane;

public class RecipeEditorController {
	interface RecipeEditorPane {
		public void onShow();
	}
	//private SlidingPane slidingPane = new SlidingPane();
	private RecipeDataBrowser browser = new RecipeDataBrowser(this);
	private RecipeObjectCreator creator = new RecipeObjectCreator(this);
	private PropertyEditorPane editor = new PropertyEditorPane(this);
	private StringEditorPane textEditor = new StringEditorPane(this);
	private MessagePane messagePane = new MessagePane(this);
	private StructureFieldCreator fieldCreator = new StructureFieldCreator(this);
	
	java.awt.CardLayout cardLayout = new java.awt.CardLayout();
	private JPanel slidingPane = new JPanel(cardLayout);
	
	public RecipeEditorController() {
		browser.setName("browser");
		creator.setName("creator");
		fieldCreator.setName("fieldCreator");
		editor.setName("editor");
		textEditor.setName("textEditor");
		messagePane.setName("message");
		// TEMPORARY: use a card layout
		slidingPane.add(browser, browser.getName());
		slidingPane.add(creator, creator.getName());
		slidingPane.add(editor, editor.getName());
		slidingPane.add(fieldCreator, fieldCreator.getName());
		slidingPane.add(textEditor, textEditor.getName());
		slidingPane.add(messagePane, messagePane.getName());
		editor.getPropertyEditor().setStringEditListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doStringEdit();}			
		});
	}
	
	private void doStringEdit() {
		String value = (String)editor.getPropertyEditor().getStringEditValue();
		textEditor.setText(value);
		slideTo(textEditor);
	}
	
	public void slideTo(Container container) {
		// slidingPane.setSelectedPane(index);
		((RecipeEditorPane)container).onShow();
		cardLayout.show(slidingPane, container.getName());		
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

	public StructureFieldCreator getFieldCreator() {
		return fieldCreator;
	}

	public PropertyEditorPane getEditor() {
		return editor;
	}

	public StringEditorPane getTextEditor() {
		return textEditor;
	}

	public MessagePane getMessagePane() {
		return messagePane;
	}

	public static void main(String[] args) {
		RecipeEditorController controller = new RecipeEditorController();
		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		Group topGroup = new Group();
		topGroup.setKey("Top");
		RecipeDataBrowser browser = controller.getBrowser();
		browser.setRecipeData(topGroup);
		frame.setContentPane(controller.getSlidingPane());
		frame.setSize(300,200);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
