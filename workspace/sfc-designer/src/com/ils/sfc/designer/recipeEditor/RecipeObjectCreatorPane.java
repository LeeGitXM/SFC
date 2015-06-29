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

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.RecipeDataTranslator;
import com.ils.sfc.designer.panels.ButtonPanel;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;
import com.ils.sfc.designer.panels.EditorPanel;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class RecipeObjectCreatorPane extends EditorPanel {
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private JTextField keyTextField = new JTextField();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	private RecipeEditorController controller;
	
	public RecipeObjectCreatorPane(RecipeEditorController controller, int index) {
		super(controller, index);
		this.controller = controller;
		initTypes();
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCreate(); }		
		});
	}

	@Override
	public void activate(int returnIndex) {
		keyTextField.requestFocus();
		super.activate(returnIndex);
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
		mainPanel.add(new JLabel("Key:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		mainPanel.add(keyTextField, con);
		keyTextField.setPreferredSize(new Dimension(100,25));
	}

	private void initTypes() {
		for(Class<?> type: RecipeDataTranslator.getConcreteClasses()) {
			typesCombo.addItem(new ComboWrapper(type.getSimpleName(), type));
		}
		typesCombo.setSelectedIndex(0);
	}

	private void doCreate() {
		ComboWrapper selectedType = (ComboWrapper)typesCombo.getSelectedItem();
		String key = keyTextField.getText().trim();
		if(key.length() == 0) {
			controller.getMessagePane().setText("You must specify a key");
			controller.getMessagePane().activate(myIndex);
			return;
		}
		try {
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			Data newObject = Data.createNewInstance(selectedClass);
			newObject.setKey(key);
			keyTextField.setText("");
			controller.getRecipeData().add(newObject);
			controller.getEditor().setRecipeData(newObject);
			controller.getEditor().activate(myIndex);
		}
		catch(Exception e) {
			e.printStackTrace();
			controller.getMessagePane().setText("Unexpected error creating object: " + e.getMessage());
			controller.getMessagePane().activate(myIndex);
			return;
		}
	}
	
}
