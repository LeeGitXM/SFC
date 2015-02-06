package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ils.sfc.designer.ButtonPanel;

/** A sliding pane for displaying a message */
@SuppressWarnings("serial")
public class MessagePane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false, RecipeEditorController.background);
	private RecipeEditorController controller;
	private JTextArea textField = new JTextArea();
	private int returnIndex;
	
	public MessagePane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		textField.setEditable(false);
		add(textField, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAddButton().setVisible(false);
		buttonPanel.getEditButton().setVisible(false);
		buttonPanel.getRemoveButton().setVisible(false);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}		
		});
	}
	
	@Override
	public void activate() {
		controller.slideTo(RecipeEditorController.MESSAGE);
	}
	
	public void setText(String text, int returnIndex) {
		textField.setText(text);
		this.returnIndex = returnIndex;
	}
	
	public void doOK() {
		controller.slideTo(returnIndex);
	}
	
}
