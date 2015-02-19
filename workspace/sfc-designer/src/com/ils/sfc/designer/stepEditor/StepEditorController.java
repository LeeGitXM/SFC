package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;

import com.ils.sfc.designer.propertyEditor.PropertyTableModel;
import com.inductiveautomation.ignition.client.util.gui.SlidingPane;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.designer.api.AbstractStepEditor;
import com.inductiveautomation.sfc.designer.api.ElementEditor;
import com.inductiveautomation.sfc.designer.api.StepConfigFactory;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;
import com.inductiveautomation.sfc.uimodel.ChartUIModel;

/** An editor for all ILS step types */
public class StepEditorController extends AbstractStepEditor implements PropertyTableModel.ErrorHandler {
	private static final long serialVersionUID = 1L;
	private SlidingPane slidingPane = new SlidingPane();
	
	static final int PROPERTY_EDITOR = 0;
	static final int TEXT_EDITOR = 1;
	static final int TAG_BROWSER = 2;
	static final int UNIT_CHOOSER = 3;
	static final int MESSAGE = 4;
	static final int REVIEW_DATA = 5;

	// The sub-panes:
	private StepPropertyEditorPane propertyEditor = new StepPropertyEditorPane(this);
	private StepStringEditorPane stringEditor = new StepStringEditorPane(this);
	private StepTagBrowserPane tagBrowser = new StepTagBrowserPane(this);
	private StepUnitChooserPane unitChooser = new StepUnitChooserPane(this);
	private StepMessagePane messagePane = new StepMessagePane(this);
	private ReviewDataPane reviewDataPane = new ReviewDataPane(this);
			
	public StepEditorController(ChartUIModel chartModel, ChartUIElement element) {
		super(new BorderLayout(), chartModel);
		add(slidingPane);
		slidingPane.add(propertyEditor);
		slidingPane.add(stringEditor);
		slidingPane.add(tagBrowser);
		slidingPane.add(unitChooser);
		slidingPane.add(messagePane);
		slidingPane.add(reviewDataPane);
	}

	
	public StepPropertyEditorPane getPropertyEditor() {
		return propertyEditor;
	}


	public StepStringEditorPane getStringEditor() {
		return stringEditor;
	}


	public StepTagBrowserPane getTagBrowser() {
		return tagBrowser;
	}


	public StepUnitChooserPane getUnitChooser() {
		return unitChooser;
	}


	public StepMessagePane getMessagePane() {
		return messagePane;
	}


	public ReviewDataPane getReviewDataPane() {
		return reviewDataPane;
	}

	public <T> void set(Property<T> property, T value) {
		element.set(property, value);
		setElement(element);
	}

	@Override
	public void setElement(ChartUIElement element) {
		super.setElement(element);
		propertyEditor.getPropertyEditor().setPropertyValues(element, true);
	}

	@Override
	public void commitEdit() {
		// TODO: what to do here??
	}

	public static class Factory implements StepConfigFactory {

		StepEditorController editor;

		@Override
		public ElementEditor createConfigUI(ChartUIModel model,
				ChartUIElement element) {
			if (editor == null || editor.model != model) {
				editor = new StepEditorController(model, element);
			}
			editor.setElement(element);
			return editor;
		}

	}

	public void showMessage(String message, int returnPanelIndex) {
		//messagePane.setText(message, returnPanelIndex);
		//messagePane.activate();
	}

	public SlidingPane getSlidingPane() {
		return slidingPane;
	}

	/** Handler for bad format errors in property editor */
	@Override
	public void handleError(String msg) {
		showMessage(msg, PROPERTY_EDITOR);		
	}

	public void slideTo(int index) {
		slidingPane.setSelectedPane(index);	
	}

}
