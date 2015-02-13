package com.ils.sfc.designer.recipeEditor;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;
import com.ils.sfc.designer.UnitChooserPanel;
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class UnitChooserPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private UnitChooserPanel unitChooserPanel = new UnitChooserPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false, RecipeEditorController.background);
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
