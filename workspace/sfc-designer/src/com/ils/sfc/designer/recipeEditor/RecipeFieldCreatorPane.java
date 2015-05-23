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
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.BasicUnitChooserPanel;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;
import com.ils.sfc.designer.panels.EditorPanel;
import com.ils.sfc.designer.panels.UnitChooserPanel;

@SuppressWarnings("serial")
public class RecipeFieldCreatorPane extends EditorPanel {
	private RecipeEditorController controller;
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	private JTextField nameField = new JTextField();
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private BasicUnitChooserPanel unitChooserPanel = new BasicUnitChooserPanel();
	private Structure recipeData;
	private boolean initialized;
	
	public RecipeFieldCreatorPane(RecipeEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCreate(); }		
		});
	}

	@Override
	public void activate(int returnIndex) {
		if(!initialized) {
			unitChooserPanel.initTypes();
			initialized = true;
		}
		super.activate(returnIndex);
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

		DesignerUtil.setConstraints(con, EAST, NONE, 2, 2, 0, 2, new Insets(2, 0, 2, 5), 0, 0);
		formPanel.add(unitChooserPanel, con);
		
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
			controller.getMessagePane().setText("You must specify a name");
			controller.getMessagePane().activate(myIndex);
			return;
		}
		try {
			ComboWrapper selectedType = (ComboWrapper)typesCombo.getSelectedItem();
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			recipeData.addDynamicProperty(name, selectedClass, null);
			controller.getEditor().getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
			controller.getEditor().activate(myIndex);
		}
		catch(Exception e) {
			controller.getMessagePane().setText("Unexpected error creating object: " + e.getMessage());
			controller.getMessagePane().activate(myIndex);
			return;
		}
	}
	
}
