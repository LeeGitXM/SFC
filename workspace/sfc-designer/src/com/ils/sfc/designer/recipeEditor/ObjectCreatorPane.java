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
import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;

/** An editor for creating a Recipe Data object. */
@SuppressWarnings("serial")
public class ObjectCreatorPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private JTextField keyTextField = new JTextField();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false, RecipeEditorController.background);
	
	public ObjectCreatorPane(RecipeEditorController controller) {
		this.controller = controller;
		initTypes();
		initUI();
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { doCreate(); }		
		});
	}

	@Override
	public void activate() {
		keyTextField.requestFocus();
		controller.slideTo(RecipeEditorController.OBJECT_CREATOR);
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
		for(Class<?> type: Data.getConcreteClasses()) {
			typesCombo.addItem(new ComboWrapper(type.getSimpleName(), type));
		}
		typesCombo.setSelectedIndex(0);
	}

	private void doCreate() {
		ComboWrapper selectedType = (ComboWrapper)typesCombo.getSelectedItem();
		String key = keyTextField.getText().trim();
		if(key.length() == 0) {
			controller.getMessagePane().setText("You must specify a key", RecipeEditorController.OBJECT_CREATOR);
			controller.getMessagePane().activate();
			return;
		}
		try {
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			Data newObject = (Data)selectedClass.newInstance();
			newObject.setKey(key);
			controller.getBrowser().add(newObject);
			controller.getEditor().setRecipeData(newObject);
			controller.getEditor().activate();
		}
		catch(Exception e) {
			e.printStackTrace();
			controller.getMessagePane().setText("Unexpected error creating object: " + e.getMessage(), RecipeEditorController.OBJECT_CREATOR);
			controller.getMessagePane().activate();
			return;
		}
	}
	
}
