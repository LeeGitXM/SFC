package com.ils.sfc.designer.recipeEditor.old.view;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EditButtonPanel extends JPanel {
	public final JButton addButton = new JButton("+");
	public final JButton removeButton = new JButton("-");
	public final JButton editButton = new JButton("...");
	
	public EditButtonPanel() {
		this.setLayout(new FlowLayout());
		add(addButton);
		add(removeButton);
		add(editButton);
	}
}
