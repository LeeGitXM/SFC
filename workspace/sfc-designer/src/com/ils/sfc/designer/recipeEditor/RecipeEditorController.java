package com.ils.sfc.designer.recipeEditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.json.JSONObject;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.designer.EditorErrorHandler;
import com.ils.sfc.designer.panels.MessagePanel;
import com.ils.sfc.designer.panels.PanelController;
import com.ils.sfc.designer.panels.StringEditorPanel;
import com.ils.sfc.designer.panels.TagBrowserPanel;
import com.ils.sfc.designer.panels.UnitChooserPanel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** A controller for all the sliding panes that are involved in editing recipe data. */
public class RecipeEditorController extends PanelController implements EditorErrorHandler {
			
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
	private RecipeBrowserPane browser = new RecipeBrowserPane(this, BROWSER);
	private RecipeObjectCreatorPane creator = new RecipeObjectCreatorPane(this, OBJECT_CREATOR);
	private RecipePropertyEditorPane objectEditor = new RecipePropertyEditorPane(this, OBJECT_EDITOR);
	private StringEditorPanel textEditor = new StringEditorPanel(this, TEXT_EDITOR);
	private MessagePanel messagePanel = new MessagePanel(this, MESSAGE);
	private RecipeFieldCreatorPane fieldCreator = new RecipeFieldCreatorPane(this, FIELD_CREATOR);
	private TagBrowserPanel tagBrowser = new TagBrowserPanel(this, TAG_BROWSER);
	private UnitChooserPanel unitChooser = new UnitChooserPanel(this, UNIT_CHOOSER);
	
	// The step whose recipe data we are editing:
	private ChartUIElement element;
	private List<Data> recipeData;
	
	public RecipeEditorController(DesignerContext ctx) { 
		super(ctx);
		objectEditor.getPropertyEditor().getTableModel().setErrorHandler(this);
		// sub-panes added according to the indexes above:
		slidingPane.add(browser);
		slidingPane.add(creator);
		slidingPane.add(objectEditor);
		slidingPane.add(fieldCreator);
		slidingPane.add(textEditor);
		slidingPane.add(messagePanel);
		slidingPane.add(tagBrowser);
		slidingPane.add(unitChooser);
		slidingPane.add(new JPanel());  // a blank pane
		slideTo(EMPTY_PANE);
	}	
	
	public void setRecipeData(List<Data> recipeData) {
		this.recipeData = recipeData;
		browser.rebuildTree();
	}

	public List<Data> getRecipeData() {
		return recipeData;
	}

	public RecipeBrowserPane getBrowser() {
		return browser;
	}

	public RecipeObjectCreatorPane getCreator() {
		return creator;
	}

	public RecipeFieldCreatorPane getFieldCreator() {
		return fieldCreator;
	}

	public RecipePropertyEditorPane getEditor() {
		return objectEditor;
	}

	public StringEditorPanel getTextEditor() {
		return textEditor;
	}

	public MessagePanel getMessagePane() {
		return messagePanel;
	}

	public TagBrowserPanel getTagBrowser() {
		return tagBrowser;
	}
	
	public UnitChooserPanel getUnitChooser() {
		return unitChooser;
	}

	public void showMessage(String message, int returnPanelIndex) {
		messagePanel.setText(message);
		messagePanel.activate(returnPanelIndex);
	}

	public void setElement(ChartUIElement element, String chartPath) {
		this.element = element;
		String stepName = element.get(IlsProperty.NAME);
		String stepPath = chartPath + "/" + stepName;
		creator.setChartPath(stepPath);
		if(element.contains(ChartStepProperties.AssociatedData)) {
			JSONObject associatedData = element.getOrDefault(ChartStepProperties.AssociatedData);
			try {
				//System.out.println(associatedData.toString());
				List<Data> recipeData = Data.fromAssociatedData(associatedData);
				for(Data data: recipeData) {
					data.setStepPath(stepPath);
				}
				setRecipeData(recipeData);
			} catch (Exception e) {
				showMessage("Error getting associated recipe data: " + e.getMessage(), BROWSER);
				setRecipeData(new ArrayList<Data>());			
			}
		}
		else {
			setRecipeData(new ArrayList<Data>());			
		}
	}
	
	public void commit() {
		if(recipeData == null) return;
		
		try {
			// Note: we are assuming that we have exclusive use of the associatedData
			// and can completely overwrite it.
			JSONObject newAssociatedData = Data.toAssociatedData(recipeData);			
			element.set(ChartStepProperties.AssociatedData, newAssociatedData);
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
