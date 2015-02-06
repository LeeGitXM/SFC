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
import com.inductiveautomation.ignition.common.script.JythonExecException;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class UnitChooserPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JComboBox<String> typesCombo = new JComboBox<String>();
	private JComboBox<String> unitsCombo = new JComboBox<String>();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false, RecipeEditorController.background);
	private boolean initialized;
	
	public UnitChooserPane(RecipeEditorController controller) {
		this.controller = controller;
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doAccept(); }		
		});
		typesCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getUnitsForType();
			}
		});
	}

	@Override
	public void activate() {
		if(!initialized) {
			initTypes();
			initialized = true;
		}
		controller.slideTo(RecipeEditorController.UNIT_CHOOSER);
	}

	private void initUI() {
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		add(mainPanel, BorderLayout.CENTER);
		GridBagConstraints con = new GridBagConstraints();

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		mainPanel.add(new JLabel("Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		mainPanel.add(typesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		mainPanel.add(new JLabel("Units:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		mainPanel.add(unitsCombo, con);
}

	private void initTypes() {
		try {
			String[] unitTypes = PythonCall.toArray(PythonCall.GET_UNIT_TYPES.exec());
			for(String unitType: unitTypes) {
				typesCombo.addItem(unitType);
			}
		} catch (JythonExecException e) {
			controller.showMessage("error loading unit types: " + e.getMessage(), RecipeEditorController.UNIT_CHOOSER);
		}
		typesCombo.setSelectedIndex(0);
	}

	private void getUnitsForType() {
		String selectedType = (String) typesCombo.getSelectedItem();
		try {
			String[] units = PythonCall.toArray(PythonCall.GET_UNITS_OF_TYPE.exec(selectedType));
			unitsCombo.removeAllItems();
			for(String unit: units) {
				unitsCombo.addItem(unit);
			}
			unitsCombo.setSelectedIndex(0);
		} catch (JythonExecException e) {
			controller.showMessage("error loading units: " + e.getMessage(), RecipeEditorController.UNIT_CHOOSER);
		}
	}
	
	private void doAccept() {
		String selectedUnits = (String) unitsCombo.getSelectedItem();
		controller.getEditor().getPropertyEditor().setSelectedValue(selectedUnits);
		controller.getEditor().activate();
	}
	
}
