package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.json.JSONObject;

import com.ils.sfc.common.IlsClientScripts;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.designer.EditorErrorHandler;
import com.ils.sfc.designer.panels.MessagePanel;
import com.ils.sfc.designer.panels.PanelController;
import com.ils.sfc.designer.panels.StringEditorPanel;
import com.ils.sfc.designer.panels.TagBrowserPanel;
import com.ils.sfc.designer.panels.UnitChooserPanel;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** 
 * A controller for all the sliding panes that are involved in editing recipe data.
 * There is a single controller instance for the recipe editing frame that gets instantiated when 
 * Designer is launched.
 */
public class RecipeEditorController extends PanelController implements EditorErrorHandler {
	private static LoggerEx logger = LogUtil.getLogger(RecipeEditorController.class.getName());
			
	static final int BROWSER = 0;
	static final int OBJECT_CREATOR = 1;
	static final int OBJECT_EDITOR = 2;
	static final int FIELD_CREATOR = 3;
	static final int TEXT_EDITOR = 4;
	static final int MESSAGE = 5;
	static final int TAG_BROWSER = 6;
	static final int UNIT_CHOOSER = 7;
	static final int EMPTY_PANE = 8;
	static final int LOADING_PANE = 9;
	
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
	private ChartUIElement element= null;
	private List<Data> recipeData = null;
	
	public RecipeEditorController(DesignerContext ctx) { 
		super(ctx);
		logger.info("Initializing a RecipeEditorController");
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
		JPanel loadingPane = new JPanel(new BorderLayout());
		JLabel loadingLabel = new JLabel("Loading Recipe Data Tag Values...");
		loadingPane.add(loadingLabel);
		slidingPane.add(loadingPane);  // a blank pane
		slideTo(EMPTY_PANE);
	}	
	/**
	 * At this point the recipe data is newly un-serialized. It has not
	 * been updated from the tags.
	 * @param recipeData
	 */
	public void setRecipeData(List<Data> recipeData) {
		this.recipeData = recipeData;
		logger.info("...telling the browser to rebuild the tree...");
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
	/**
	 * Called by the workspace on itemSelectionChanged.
	 * @param element
	 * @param chartPath
	 */
	public void setElement(ChartUIElement element, String chartPath) {
		this.element = element;
		logger.infof("The chart element is: %s", element.toString());
		String stepName = element.get(IlsProperty.NAME);
		String stepUUID = element.getId().toString();
		String stepPath = chartPath + "/" + stepName;
		creator.setChartPath(stepPath);
		
		logger.infof("In setElement with %s - %s", stepName, stepUUID);
		try {
			List<Data> recipeData = Data.fromDatabase(stepUUID);
			String provider = IlsClientScripts.getProviderName(false);
			for(Data data: recipeData) {
				data.setStepPath(stepPath);
				data.setProvider(provider);
				logger.infof("%s", data);
			}
			setRecipeData(recipeData);
		} catch (Exception e){
			showMessage("Error getting associated recipe data: " + e.getMessage(), BROWSER);
			setRecipeData(new ArrayList<Data>());			
		}

		// This used to call RecipeDataTagReader.reader.recipeDataTagValues() but since we get the latest values from the database
		// we don't need to do this anymore.  There was also logic to refresh teh browser periodically, I'm not sure that it worked.  
		// We may need a refresh button. PAH
		getBrowser().activate(-1);
	}
	
	
	public void commit() {
		logger.infof("In commit - bypassing overwrite of associated data (PETE)");
		if(recipeData == null) return;
		
//		try {
			// Note: we are assuming that we have exclusive use of the associatedData
			// and can completely overwrite it.
//			JSONObject newAssociatedData = Data.toAssociatedData(recipeData);			
//			element.set(ChartStepProperties.AssociatedData, newAssociatedData);
//		} catch (Exception e) {
//			showMessage("Error setting associated recipe data: " + e.getMessage(), BROWSER);
//		}
	}

	/** Handler for bad format errors in property editor */
	@Override
	public void handleError(String msg) {
		showMessage(msg, OBJECT_EDITOR);		
	}


}
