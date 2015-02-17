package com.ils.sfc.designer.stepEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.ils.sfc.designer.UnitChooserPanel;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** An editor for creating a Step Data object. */
@SuppressWarnings("serial")
public class StepUnitChooserPane extends JPanel implements EditorPane {
	private StepEditorController controller;
	private UnitChooserPanel unitChooserPanel = new UnitChooserPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	private boolean initialized;
	
	public StepUnitChooserPane(StepEditorController controller) {
		this.controller = controller;
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAccept(); }		
		});
	}

	@Override
	public void activate() {
		if(!initialized) {
			unitChooserPanel.initTypes();
			initialized = true;
		}
		controller.slideTo(StepEditorController.UNIT_CHOOSER);
	}

	private void initUI() {
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(unitChooserPanel, BorderLayout.CENTER);
	}
	
	private void doAccept() {
		String selectedUnits = unitChooserPanel.getSelectedUnits();
		controller.getPropertyEditor().getPropertyEditor().setSelectedValue(selectedUnits);
		controller.getPropertyEditor().activate();
	}

	public void setUnit(String unit) {
		try {
			unitChooserPanel.setUnit(unit);
		} catch (JythonExecException e) {
			controller.showMessage("Error setting current units: " + e.getMessage(), StepEditorController.UNIT_CHOOSER);
		}
	}
	
}
