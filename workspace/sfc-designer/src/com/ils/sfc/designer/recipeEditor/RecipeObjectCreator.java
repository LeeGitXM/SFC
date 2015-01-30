package com.ils.sfc.designer.recipeEditor;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.CENTER;

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
import com.ils.sfc.designer.ComboWrapper;
import com.ils.sfc.designer.DesignerUtil;

@SuppressWarnings("serial")
public class RecipeObjectCreator extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JComboBox<ComboWrapper> typesCombo = new JComboBox<ComboWrapper>();
	private JTextField keyTextField = new JTextField();
	private JButton createButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	public RecipeObjectCreator(RecipeEditorController controller) {
		this.controller = controller;
		initTypes();
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

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 0, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Type:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 0, new Insets(2, 5, 2, 0), 0, 0);
		add(typesCombo, con);

		DesignerUtil.setConstraints(con, EAST, NONE, 1, 1, 0, 1, new Insets(2, 0, 2, 5), 0, 0);
		add(new JLabel("Key:", SwingConstants.RIGHT), con);
		DesignerUtil.setConstraints(con, WEST, BOTH, 1, 1, 1, 1, new Insets(2, 5, 2, 0), 0, 0);
		add(keyTextField, con);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		DesignerUtil.setConstraints(con, CENTER, NONE, 1, 2, 0, 2, new Insets(2, 0, 2, 5), 0, 0);
		add(buttonPanel, con);
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
			controller.getMessagePane().setText("You must specify a key", controller.getCreator());
			controller.slideTo(controller.getMessagePane());
			return;
		}
		try {
			Class<?> selectedClass = (Class<?>)selectedType.getObject();
			Data newObject = (Data)selectedClass.newInstance();
			newObject.setKey(key);
			controller.getBrowser().add(newObject);
			controller.getEditor().setRecipeData(newObject);
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
