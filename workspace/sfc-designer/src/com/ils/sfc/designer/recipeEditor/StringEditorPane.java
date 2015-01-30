package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class StringEditorPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JTextField textField = new JTextField();
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	public StringEditorPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		add(textField, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}		
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}		
		});
	}

	/** Do anything that needs to be done before re-showing this. */
	public void onShow() {
		textField.requestFocus();
	}

	public void setText(String text) {
		textField.setText(text);
	}
	
	public void doOK() {
		controller.getEditor().getPropertyEditor().setStringEditValue(textField.getText());
		controller.slideTo(controller.getEditor());
	}
	
	public void doCancel() {
		controller.slideTo(controller.getEditor());
	}

}
