package com.ils.sfc.designer.stepEditor;

import java.awt.Component;

import com.ils.sfc.client.step.AbstractIlsStepUI;
import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.step.AbstractIlsStepDelegate;
import com.ils.sfc.designer.EditorErrorHandler;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.panels.MessagePanel;
import com.ils.sfc.designer.panels.PanelController;
import com.ils.sfc.designer.panels.StringEditorPanel;
import com.ils.sfc.designer.panels.TagBrowserPanel;
import com.ils.sfc.designer.panels.UnitChooserPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.collectData.CollectDataPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.confirmControllers.ConfirmControllersPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.manualData.ManualDataPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.monitorDownloads.MonitorDownloadsPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.pvMonitor.PVMonitorPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.reviewData.ReviewDataPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.reviewFlows.ReviewFlowsPanel;
import com.ils.sfc.designer.stepEditor.rowEditor.writeOutput.WriteOutputPanel;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

/** An editor for all ILS step types */
public class StepEditorController extends PanelController implements EditorErrorHandler {
	static final int PROPERTY_EDITOR = 0;
	static final int TEXT_EDITOR = 1;
	static final int TAG_BROWSER = 2;
	static final int UNIT_CHOOSER = 3;
	static final int MESSAGE = 4;
	public static final int REVIEW_DATA = 5;
	public static final int COLLECT_DATA = 6;
	public static final int CONFIRM_CONTROLLERS = 7;
	public static final int MONITOR_DOWNLOADS = 8;
	public static final int PV_MONITOR = 9;
	public static final int WRITE_OUTPUT = 10;
	public static final int MANUAL_DATA_ENTRY = 11;
	public static final int REVIEW_FLOWS = 12;

	// The sub-panes:
	private StepPropertyEditorPane propertyEditor = new StepPropertyEditorPane(this, PROPERTY_EDITOR);
	private StringEditorPanel stringEditor = new StringEditorPanel(this, TEXT_EDITOR);
	private TagBrowserPanel tagBrowser = new TagBrowserPanel(this, TAG_BROWSER);
	private UnitChooserPanel unitChooser = new UnitChooserPanel(this, UNIT_CHOOSER);
	private MessagePanel messagePanel = new MessagePanel(this, MESSAGE);
	private ReviewDataPanel reviewDataPanel = new ReviewDataPanel(this, REVIEW_DATA);
	private ReviewFlowsPanel reviewFlowsPanel = new ReviewFlowsPanel(this, REVIEW_FLOWS);
	private CollectDataPanel collectDataPanel = new CollectDataPanel(this, COLLECT_DATA);
	private ConfirmControllersPanel confirmControllersPanel = new ConfirmControllersPanel(this, CONFIRM_CONTROLLERS);
	private MonitorDownloadsPanel monitorDownloadsPanel = new MonitorDownloadsPanel(this, MONITOR_DOWNLOADS);
	private PVMonitorPanel pvMonitorPanel = new PVMonitorPanel(this, PV_MONITOR);
	private WriteOutputPanel writeOutputPanel = new WriteOutputPanel(this, WRITE_OUTPUT);
	private ManualDataPanel manualDataEntryPanel = new ManualDataPanel(this, MANUAL_DATA_ENTRY);
	
	public StepEditorController(DesignerContext context) {
		super(context);
		slidingPane.add(propertyEditor);
		slidingPane.add(stringEditor);
		slidingPane.add(tagBrowser);
		slidingPane.add(unitChooser);
		slidingPane.add(messagePanel);
		slidingPane.add(reviewDataPanel);	
		slidingPane.add(collectDataPanel);	
		slidingPane.add(confirmControllersPanel);	
		slidingPane.add(monitorDownloadsPanel);	
		slidingPane.add(pvMonitorPanel);	
		slidingPane.add(writeOutputPanel);	
		slidingPane.add(manualDataEntryPanel);	
		slidingPane.add(reviewFlowsPanel);	
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

	public MessagePanel getMessagePanel() {
		return messagePanel;
	}

	public ReviewDataPanel getReviewDataPanel() {
		return reviewDataPanel;
	}

	public ReviewFlowsPanel getReviewFlowsPanel() {
		return reviewFlowsPanel;
	}

	public CollectDataPanel getCollectDataPanel() {
		return collectDataPanel;
	}

	public ConfirmControllersPanel getConfirmControllersPanel() {
		return confirmControllersPanel;
	}

	public MonitorDownloadsPanel getMonitorDownloadsPanel() {
		return monitorDownloadsPanel;
	}

	public PVMonitorPanel getPvMonitorPanel() {
		return pvMonitorPanel;
	}

	public WriteOutputPanel getWriteOutputPanel() {
		return writeOutputPanel;
	}
	
	public ManualDataPanel getManualDataEntryPanel() {
		return manualDataEntryPanel;
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
		String factoryId = (String)element.get(IlsProperty.FACTORY_ID);
		AbstractIlsStepDelegate stepDelegate = (AbstractIlsStepDelegate)AbstractIlsStepUI.getFactory(factoryId);
		getPropertyEditor().getPropertyEditor().setPropertyValues(element, stepDelegate.getOrderedProperties());
		slideTo(PROPERTY_EDITOR);
	}

	public EditorPanel getSelectedPanel() {
		 return (EditorPanel)slidingPane.getComponent(slidingPane.getSelectedPane());
	}

	public void commitEdit() {
		getSelectedPanel().commitEdit();		
	}

}
