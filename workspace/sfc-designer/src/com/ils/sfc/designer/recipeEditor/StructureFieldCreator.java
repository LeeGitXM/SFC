package com.ils.sfc.designer.recipeEditor;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;

@SuppressWarnings("serial")
public class StructureFieldCreator extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JTextField nameField = new JTextField();
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private JComboBox<ComboWrapper> unitTypesCombo = new JComboBox<ComboWrapper>();
	private JComboBox<ComboWrapper> unitsCombo = new JComboBox<ComboWrapper>();
	private JButton createButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private Structure recipeData;
	
	public StructureFieldCreator(RecipeEditorController controller) {
		this.controller = controller;
		initUI();
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCreate(); }		
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCancel(); }		
		});
	}

	/** Do anything that needs to be done before re-showing this. */
	public void onShow() {
		
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		nameField.setPreferredSize(new Dimension(50,15));
		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Name:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		add(nameField, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		add(typesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 2, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Unit Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 2, new Insets(2, 5, 2, 0), 0, 0);
		add(unitTypesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 3, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Units:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 3, new Insets(2, 5, 2, 0), 0, 0);
		add(unitsCombo, con);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		DesignerUtil.setConstraints(con, CENTER, NONE, 1, 2, 0, 4, new Insets(2, 0, 2, 5), 0, 0);
		add(buttonPanel, con);
		
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
			controller.getMessagePane().setText("You must specify a name", controller.getCreator());
			controller.slideTo(controller.getMessagePane());
			return;
		}
		try {
			ComboWrapper selectedType = (ComboWrapper)typesCombo.getSelectedItem();
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			recipeData.addDynamicProperty(name, selectedClass, null);
			controller.getEditor().getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
			controller.slideTo(controller.getEditor());
		}
		catch(Exception e) {
			controller.getMessagePane().setText("Unexpected error creating object: " + e.getMessage(), controller.getCreator());
			controller.slideTo(controller.getMessagePane());
			return;
		}
	}
	
	private void doCancel() {
		controller.slideTo(controller.getEditor());
	}
}
