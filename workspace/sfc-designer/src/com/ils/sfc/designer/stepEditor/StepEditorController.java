package com.ils.sfc.designer.stepEditor;

import com.ils.sfc.designer.panels.MessagePanel;
import com.ils.sfc.designer.panels.PanelController;
import com.ils.sfc.designer.panels.StringEditorPanel;
import com.ils.sfc.designer.panels.TagBrowserPanel;
import com.ils.sfc.designer.panels.UnitChooserPanel;
import com.ils.sfc.designer.propertyEditor.PropertyTableModel;
import com.ils.sfc.designer.stepEditor.collectData.CollectDataPanel;
import com.ils.sfc.designer.stepEditor.reviewData.ReviewDataPanel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** An editor for all ILS step types */
public class StepEditorController extends PanelController implements PropertyTableModel.ErrorHandler {
	private static final long serialVersionUID = 1L;
	static final int PROPERTY_EDITOR = 0;
	static final int TEXT_EDITOR = 1;
	static final int TAG_BROWSER = 2;
	static final int UNIT_CHOOSER = 3;
	static final int MESSAGE = 4;
	public static final int REVIEW_DATA = 5;
	public static final int COLLECT_DATA = 6;

	// The sub-panes:
	private StepPropertyEditorPane propertyEditor = new StepPropertyEditorPane(this, PROPERTY_EDITOR);
	private StringEditorPanel stringEditor = new StringEditorPanel(this, TEXT_EDITOR);
	private TagBrowserPanel tagBrowser = new TagBrowserPanel(this, TAG_BROWSER);
	private UnitChooserPanel unitChooser = new UnitChooserPanel(this, UNIT_CHOOSER);
	private MessagePanel messagePanel = new MessagePanel(this, MESSAGE);
	private ReviewDataPanel reviewDataPanel = new ReviewDataPanel(this, REVIEW_DATA);
	private CollectDataPanel collectDataPanel = new CollectDataPanel(this, COLLECT_DATA);
	
	public StepEditorController(DesignerContext context) {
		super(context);
		slidingPane.add(propertyEditor);
		slidingPane.add(stringEditor);
		slidingPane.add(tagBrowser);
		slidingPane.add(unitChooser);
		slidingPane.add(messagePanel);
		slidingPane.add(reviewDataPanel);	
		slidingPane.add(collectDataPanel);	
	}

	
	public StepPropertyEditorPane getPropertyEditor() {
		return propertyEditor;
	}


	public StringEditorPanel getStringEditor() {
		return stringEditor;
	}


	public TagBrowserPanel getTagBrowser() {
		return tagBrowser;
	}


	public UnitChooserPanel getUnitChooser() {
		return unitChooser;
	}


	public MessagePanel getMessagePane() {
		return messagePanel;
	}


	public ReviewDataPanel getReviewDataPane() {
		return reviewDataPanel;
	}

	public CollectDataPanel getCollectDataPane() {
		return collectDataPanel;
	}


	public void showMessage(String message, int returnPanelIndex) {
		messagePanel.setText(message);
		messagePanel.activate(returnPanelIndex);
	}

	/** Handler for bad format errors in property editor */
	@Override
	public void handleError(String msg) {
		showMessage(msg, PROPERTY_EDITOR);		
	}

	public void slideTo(int index) {
		slidingPane.setSelectedPane(index);	
	}

	public void setElement(ChartUIElement element) {
		getPropertyEditor().getPropertyEditor().setPropertyValues(element, true);
		slideTo(PROPERTY_EDITOR);
	}

}
