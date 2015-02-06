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

import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;

@SuppressWarnings("serial")
public class FieldCreatorPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false, RecipeEditorController.background);
	private JTextField nameField = new JTextField();
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private JComboBox<ComboWrapper> unitTypesCombo = new JComboBox<ComboWrapper>();
	private JComboBox<ComboWrapper> unitsCombo = new JComboBox<ComboWrapper>();
	private Structure recipeData;
	
	public FieldCreatorPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCreate(); }		
		});
	}

	@Override
	public void activate() {
		controller.slideTo(RecipeEditorController.FIELD_CREATOR);
	}

	private void initUI() {
		add(buttonPanel, BorderLayout.NORTH);
		JPanel formPanel = new JPanel(new GridBagLayout());
		add(formPanel, BorderLayout.CENTER);
		GridBagConstraints con = new GridBagConstraints();
		nameField.setPreferredSize(new Dimension(100,25));
		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		formPanel.add(new JLabel("Name:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		formPanel.add(nameField, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		formPanel.add(new JLabel("Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		formPanel.add(typesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 2, new Insets(2, 0, 2, 5), 0, 0);
		formPanel.add(new JLabel("Unit Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 2, new Insets(2, 5, 2, 0), 0, 0);
		formPanel.add(unitTypesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 3, new Insets(2, 0, 2, 5), 0, 0);
		formPanel.add(new JLabel("Units:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 3, new Insets(2, 5, 2, 0), 0, 0);
		formPanel.add(unitsCombo, con);
		
		typesCombo.addItem(new ComboWrapper("String", String.class));
		typesCombo.addItem(new ComboWrapper("Double", Double.class));
		typesCombo.addItem(new ComboWrapper("Boolean", Boolean.class));
		typesCombo.setSelectedIndex(0);

	}

	public void setRecipeData(Structure recipeData) {
		this.recipeData = recipeData;
	}

	private void doCreate() {
		String name = nameField.getText().trim();
		if(name.length() == 0) {
			controller.getMessagePane().setText("You must specify a name", RecipeEditorController.FIELD_CREATOR);
			controller.getMessagePane().activate();
			return;
		}
		try {
			ComboWrapper selectedType = (ComboWrapper)typesCombo.getSelectedItem();
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			recipeData.addDynamicProperty(name, selectedClass, null);
			controller.getEditor().getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
			controller.getEditor().activate();
		}
		catch(Exception e) {
			controller.getMessagePane().setText("Unexpected error creating object: " + e.getMessage(), RecipeEditorController.FIELD_CREATOR);
			controller.getMessagePane().activate();
			return;
		}
	}
	
}
