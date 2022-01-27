package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.ils.sfc.designer.propertyEditor.PropertyRow;
import com.ils.sfc.designer.propertyEditor.PropertyTableModel;
import com.ils.sfc.designer.propertyEditor.ValueHolder;
import com.inductiveautomation.ignition.common.config.BasicPropertySet;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import system.ils.sfc.common.Constants;

/** A thin wrapper for a PropertyEditor that adds an accept action.
 *  Also provides add/remove for dynamic properties, and extended
 *  editing for strings and tags.  */
@SuppressWarnings("serial")
public class StepPropertyEditorPane extends EditorPanel implements ValueHolder {
	private final LoggerEx log = LogUtil.getLogger(getClass().getName());
	private StepEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private ButtonPanel buttonPanel = new ButtonPanel(false, false, false, true, true,  false, false, true);

	public StepPropertyEditorPane(StepEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		log.tracef("Creating a %s", getClass().getName());
		add(editor, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getEditButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doEdit();}			
		});
		buttonPanel.getCheckBox().setText("Isolation Mode");
		buttonPanel.getExecButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doExec();}			
		});
		
		// listen for selection so we can enable/disable buttons e.g.
		editor.getSelectionModel().addListSelectionListener(
			new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e) {
					selectionChanged();
				}
		});
		addMouseListener();
		selectionChanged();
	}
	
	
	private void addMouseListener() {
		/* An attempt by Pete to add a mouse listener to every text fiel in the table that will automatically slide to another pane, based on the type of property and cell being edited. */
  		// Can't add a mouse listener to a table!
		log.tracef("Adding a mouse listener...");
		
		JTable table = editor.getTable();
		PropertyTableModel tableModel = editor.getTableModel();
		
		/* Add a mouse listener to the table, this only seems to do anything if they click on the first column */
		table.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        if (evt.getClickCount() == 2) {
		        	log.tracef("In mouseClicked()!");
		        	 
		        	Property<?> selectedProperty = getPropertyEditor().getSelectedRow().getProperty();
		        	PropertyValue selectedPropertyValue =  getPropertyEditor().getSelectedRow().getPropertyValue();
		        	log.tracef("  Property: %s", selectedProperty.toString());
		        	
		        	if(selectedProperty.equals(IlsProperty.PRIMARY_REVIEW_DATA) ||
		    				selectedProperty.equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
		    				selectedProperty.equals(IlsProperty.SECONDARY_REVIEW_DATA) ||
		    				selectedProperty.equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE )) {
		    			// REVIEW_DATA properties hold a complex configuration in a stringified JSON object
		    			controller.getReviewDataPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getReviewDataPanel().activate(myIndex);				
		    		}
		    		else if(selectedProperty.equals(IlsProperty.REVIEW_FLOWS)) {
		    			controller.getReviewFlowsPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getReviewFlowsPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.COLLECT_DATA_CONFIG)) {
		    			// COLLECT_DATA properties hold a complex configuration in a stringified JSON object
		    			controller.getCollectDataPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getCollectDataPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.CONFIRM_CONTROLLERS_CONFIG)) {
		    			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
		    			controller.getConfirmControllersPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getConfirmControllersPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.MONITOR_DOWNLOADS_CONFIG)) {
		    			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
		    			controller.getMonitorDownloadsPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getMonitorDownloadsPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.PV_MONITOR_CONFIG)) {
		    			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
		    			controller.getPvMonitorPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getPvMonitorPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.WRITE_OUTPUT_CONFIG)) {
		    			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
		    			controller.getWriteOutputPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getWriteOutputPanel().activate(myIndex);							
		    		}
		    		else if(selectedProperty.equals(IlsProperty.MANUAL_DATA_CONFIG)) {
		    			controller.getManualDataEntryPanel().setConfig((PropertyValue) selectedPropertyValue);
		    			controller.getManualDataEntryPanel().activate(myIndex);							
		    		}
		        }
		    }
		});
	}

	
	private void selectionChanged() {
		log.tracef("Selection changed");
		buttonPanel.enableEditButton(getPropertyEditor());
	}
	
	private void doExec() {
		BasicPropertySet propertyValues = editor.getPropertyValues();
		String sql = propertyValues.getOrDefault(IlsProperty.SQL);
		boolean isolationMode = buttonPanel.getCheckBox().isSelected();
		Object[] args = {sql, isolationMode};
		try {
			PythonCall.TEST_QUERY.exec(args);
		} catch (JythonExecException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void activate(int returnIndex) {
		log.tracef("In activate() with index: %d", returnIndex);

		// HACK!! we recognize "testable" elements by looking for SQL related properties
		boolean isTestable = editor.getPropertyValues().contains(IlsProperty.SQL);
		buttonPanel.getExecButton().setVisible(isTestable);
		buttonPanel.getCheckBox().setVisible(isTestable);
		super.activate(returnIndex);
	}
	
	@SuppressWarnings("unchecked")
	private void doEdit() {
		log.tracef("Editing...");
		PropertyRow selectedRow = getPropertyEditor().getSelectedRow();
		boolean hasChoices = selectedRow != null && selectedRow.getChoices() != null;
		PropertyValue selectedPropertyValue = null;
		if (selectedRow != null) {
			selectedPropertyValue = (PropertyValue)selectedRow.getValue();
		}
		if(selectedPropertyValue == null) return;
		Property<?>selectedProperty = selectedPropertyValue.getProperty();
		log.tracef("Property: %s", selectedProperty);
		editor.stopCellEditing();
		if(selectedProperty.equals(IlsProperty.TAG_PATH)) {
			controller.getTagBrowser().setValue(selectedPropertyValue.getValue());
			controller.getTagBrowser().activate(this);
		}
//PAH		else if(selectedProperty.equals(IlsProperty.KEY) ||
//PAH				selectedProperty.equals(IlsProperty.BUTTON_KEY) ||
//PAH				selectedProperty.equals(IlsProperty.TIMER_KEY) ||
//PAH				selectedProperty.equals(IlsProperty.TIME_LIMIT_RECIPE_KEY) ||
//PAH				selectedProperty.equals(IlsProperty.CHOICES_KEY)) {
			//controller.getRecipeDataBrowser().setConfig((PropertyValue<String>) selectedPropertyValue);
//PAH			controller.getRecipeDataBrowser().activate(this);										
//PAH		}
		else if(selectedProperty.equals(IlsProperty.PRIMARY_REVIEW_DATA) ||
				selectedProperty.equals(IlsProperty.PRIMARY_REVIEW_DATA_WITH_ADVICE) ||
				selectedProperty.equals(IlsProperty.SECONDARY_REVIEW_DATA) ||
				selectedProperty.equals(IlsProperty.SECONDARY_REVIEW_DATA_WITH_ADVICE )) {
			// REVIEW_DATA properties hold a complex configuration in a stringified JSON object
			controller.getReviewDataPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getReviewDataPanel().activate(myIndex);				
		}
		else if(selectedProperty.equals(IlsProperty.REVIEW_FLOWS)) {
			controller.getReviewFlowsPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getReviewFlowsPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.COLLECT_DATA_CONFIG)) {
			// COLLECT_DATA properties hold a complex configuration in a stringified JSON object
			controller.getCollectDataPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getCollectDataPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.CONFIRM_CONTROLLERS_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getConfirmControllersPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getConfirmControllersPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.MONITOR_DOWNLOADS_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getMonitorDownloadsPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getMonitorDownloadsPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.PV_MONITOR_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getPvMonitorPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getPvMonitorPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.WRITE_OUTPUT_CONFIG)) {
			// CONFIRM_CONTROLLERS properties hold a complex configuration in a stringified JSON object
			controller.getWriteOutputPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getWriteOutputPanel().activate(myIndex);							
		}
		else if(selectedProperty.equals(IlsProperty.MANUAL_DATA_CONFIG)) {
			controller.getManualDataEntryPanel().setConfig((PropertyValue) selectedPropertyValue);
			controller.getManualDataEntryPanel().activate(myIndex);							
		}
		else if(selectedProperty.getName().endsWith(Constants.UNIT_SUFFIX) && !hasChoices) {
			editor.stopCellEditing();
			controller.getUnitChooser().activate(myIndex);
			// as activate may initialize units; we set unit AFTER activation:
			controller.getUnitChooser().setValue(editor.getSelectedValue());
		}
		else if(selectedProperty.getType() == String.class) {
			editor.stopCellEditing();
			Object value = selectedPropertyValue.getValue();
			controller.getStringEditor().setValue(value);
			controller.getStringEditor().activate(this);
		}
		// else do nothing
	}
	
	public PropertyEditor getPropertyEditor() {
		return editor;
	}

	@Override
	public void commitEdit() {
		editor.stopCellEditing();
	}

	@Override
	public void setValue(Object value) {
		getPropertyEditor().setSelectedValue(value);
	}

}
