package com.ils.sfc.designer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;

/** Basically just a big text area for editing long strings. */
@SuppressWarnings("serial")
public abstract class AbstractStringEditorPane extends JPanel implements EditorPane {
	protected JTextArea textField = new JTextArea();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	
	public AbstractStringEditorPane() {
		super(new BorderLayout());
		add(textField, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}		
		});
	}

	/** Do anything that needs to be done before re-showing this. */
	public void activate() {
		textField.requestFocus();
	}

	public void setText(String text) {
		textField.setText(text);
	}
	
	public abstract void doOK();
	
}
