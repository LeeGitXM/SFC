package com.ils.sfc.designer.recipeEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.inductiveautomation.ignition.client.util.gui.SlidingPane;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/** A controller for all the sliding panes that are involved in editing recipe data. */
public class RecipeEditorController {
	public static java.awt.Color background = new java.awt.Color(238,238,238);
	
	interface RecipeEditorPane {
		/** show yourself, after doing any necessary preparation. */
		public void activate();
	}
	
	private SlidingPane slidingPane = new SlidingPane();
	
	// indices for the sub-panes:
	static final int BROWSER = 0;
	static final int OBJECT_CREATOR = 1;
	static final int EDITOR = 2;
	static final int FIELD_CREATOR = 3;
	static final int TEXT_EDITOR = 4;
	static final int MESSAGE = 5;
	static final int TAG_BROWSER = 6;
	static final int EMPTY_PANE = 7;
	
	// The sub-panes:
	private BrowserPane browser = new BrowserPane(this);
	private ObjectCreatorPane creator = new ObjectCreatorPane(this);
	private ObjectEditorPane editor = new ObjectEditorPane(this);
	private StringEditorPane textEditor = new StringEditorPane(this);
	private MessagePane messagePane = new MessagePane(this);
	private FieldCreatorPane fieldCreator = new FieldCreatorPane(this);
	private TagBrowserPane tagBrowser;
	
	public RecipeEditorController() {
		tagBrowser = new TagBrowserPane(this);
		// sub-panes added according to the indexes above:
		slidingPane.add(browser);
		slidingPane.add(creator);
		slidingPane.add(editor);
		slidingPane.add(fieldCreator);
		slidingPane.add(textEditor);
		slidingPane.add(messagePane);
		slidingPane.add(tagBrowser);
		slidingPane.add(new JPanel());  // a blank pane
		slideTo(EMPTY_PANE);
	}
	
	public void setContext(DesignerContext context) {
		tagBrowser.setContext(context);
	}
	
	public void slideTo(int index) {
		slidingPane.setSelectedPane(index);	
	}	
	
	public JPanel getSlidingPane() {
		return slidingPane;
	}

	public BrowserPane getBrowser() {
		return browser;
	}

	public ObjectCreatorPane getCreator() {
		return creator;
	}

	public FieldCreatorPane getFieldCreator() {
		return fieldCreator;
	}

	public ObjectEditorPane getEditor() {
		return editor;
	}

	public StringEditorPane getTextEditor() {
		return textEditor;
	}

	public MessagePane getMessagePane() {
		return messagePane;
	}

	public TagBrowserPane getTagBrowser() {
		return tagBrowser;
	}

	public static void main(String[] args) {
		RecipeEditorController controller = new RecipeEditorController();		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.setContentPane(controller.getSlidingPane());
		frame.setSize(300,200);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
