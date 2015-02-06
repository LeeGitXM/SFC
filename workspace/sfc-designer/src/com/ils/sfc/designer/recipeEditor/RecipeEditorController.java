package com.ils.sfc.designer.recipeEditor;

import javax.swing.JPanel;

import org.json.JSONObject;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Group;
import com.ils.sfc.designer.propertyEditor.PropertyTableModel;
import com.inductiveautomation.ignition.client.util.gui.SlidingPane;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** A controller for all the sliding panes that are involved in editing recipe data. */
public class RecipeEditorController implements PropertyTableModel.ErrorHandler {
	public static java.awt.Color background = new java.awt.Color(238,238,238);
	
	interface RecipeEditorPane {
		/** show yourself, after doing any necessary preparation. */
		public void activate();
	}
	
	private SlidingPane slidingPane = new SlidingPane();
	
	// indices for the sub-panes:
	static final int BROWSER = 0;
	static final int OBJECT_CREATOR = 1;
	static final int OBJECT_EDITOR = 2;
	static final int FIELD_CREATOR = 3;
	static final int TEXT_EDITOR = 4;
	static final int MESSAGE = 5;
	static final int TAG_BROWSER = 6;
	static final int UNIT_CHOOSER = 7;
	static final int EMPTY_PANE = 8;
	
	// The sub-panes:
	private BrowserPane browser = new BrowserPane(this);
	private ObjectCreatorPane creator = new ObjectCreatorPane(this);
	private ObjectEditorPane objectEditor = new ObjectEditorPane(this);
	private StringEditorPane textEditor = new StringEditorPane(this);
	private MessagePane messagePane = new MessagePane(this);
	private FieldCreatorPane fieldCreator = new FieldCreatorPane(this);
	private TagBrowserPane tagBrowser;
	private UnitChooserPane unitChooser = new UnitChooserPane(this);
	
	// The step whose recipe data we are editing:
	private ChartUIElement element;
	private Group recipeData;
	
	public RecipeEditorController() {
		tagBrowser = new TagBrowserPane(this);
		objectEditor.getPropertyEditor().getTableModel().setErrorHandler(this);
		// sub-panes added according to the indexes above:
		slidingPane.add(browser);
		slidingPane.add(creator);
		slidingPane.add(objectEditor);
		slidingPane.add(fieldCreator);
		slidingPane.add(textEditor);
		slidingPane.add(messagePane);
		slidingPane.add(tagBrowser);
		slidingPane.add(unitChooser);
		slidingPane.add(new JPanel());  // a blank pane
		slideTo(EMPTY_PANE);
	}	
	
	public void setRecipeData(Group recipeData) {
		this.recipeData = recipeData;
		browser.rebuildTree();
	}

	public Group getRecipeData() {
		return recipeData;
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
		return objectEditor;
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
	
	public UnitChooserPane getUnitChooser() {
		return unitChooser;
	}

	public void showMessage(String message, int returnPanelIndex) {
		messagePane.setText(message, returnPanelIndex);
		messagePane.activate();
	}

	public static void main(String[] args) {
		RecipeEditorController controller = new RecipeEditorController();		
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.setContentPane(controller.getSlidingPane());
		frame.setSize(300,200);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setElement(ChartUIElement element) {
		this.element = element;
		if(element.contains(ChartStepProperties.AssociatedData)) {
			JSONObject associatedData = element.getOrDefault(ChartStepProperties.AssociatedData);
			try {
				Group group = (Group)Data.fromJson(associatedData);
				setRecipeData(group);
			} catch (Exception e) {
				showMessage("Error getting associated recipe data: " + e.getMessage(), BROWSER);
			}
		}
		else {
			Group group = new Group();
			group.setKey("data");
			setRecipeData(group);			
		}
	}
	
	public void commit() {
		if(recipeData == null) return;
		
		try {
			JSONObject associatedData = recipeData.toJSON();
			element.set(ChartStepProperties.AssociatedData, associatedData);
		} catch (Exception e) {
			showMessage("Error setting associated recipe data: " + e.getMessage(), BROWSER);
		}
	}

	/** Handler for bad format errors in property editor */
	@Override
	public void handleError(String msg) {
		showMessage(msg, OBJECT_EDITOR);		
	}

}
