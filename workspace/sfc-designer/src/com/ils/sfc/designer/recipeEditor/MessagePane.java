package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** A sliding pane for displaying a message */
public class MessagePane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private JTextField textField = new JTextField();
	private JButton okButton = new JButton("OK");
	private Container returnPane;
	
	public MessagePane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		textField.setEditable(false);
		add(textField, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		add(buttonPanel, BorderLayout.SOUTH);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}		
		});
	}
	
	/** Do anything that needs to be done before re-showing this. */
	public void onShow() {
	}
	
	public void setText(String text, Container returnPane) {
		textField.setText(text);
		this.returnPane = returnPane;
	}
	
	public void doOK() {
		controller.slideTo(returnPane);
	}
	
}
