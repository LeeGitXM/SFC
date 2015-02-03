package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ils.sfc.designer.ButtonPanel;

/** Basically just a big text area for editing long strings. */
@SuppressWarnings("serial")
public class StringEditorPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JTextArea textField = new JTextArea();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, RecipeEditorController.background);
	
	public StringEditorPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		add(textField, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}		
		});
	}

	/** Do anything that needs to be done before re-showing this. */
	public void activate() {
		textField.requestFocus();
		controller.slideTo(RecipeEditorController.TEXT_EDITOR);
	}

	public void setText(String text) {
		textField.setText(text);
	}
	
	public void doOK() {
		controller.getEditor().getPropertyEditor().setSelectedValue(textField.getText());
		controller.getEditor().activate();
	}
	
}
