package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;
import com.ils.sfc.designer.UnitChooserPanel;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class UnitChooserPane extends JPanel implements EditorPane {
	private RecipeEditorController controller;
	private UnitChooserPanel unitChooserPanel = new UnitChooserPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	private boolean initialized;
	
	public UnitChooserPane(RecipeEditorController controller) {
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
		controller.slideTo(RecipeEditorController.UNIT_CHOOSER);
	}

	private void initUI() {
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(unitChooserPanel, BorderLayout.CENTER);
	}
	
	private void doAccept() {
		String selectedUnits = unitChooserPanel.getSelectedUnits();
		controller.getEditor().getPropertyEditor().setSelectedValue(selectedUnits);
		controller.getEditor().activate();
	}
	
}
