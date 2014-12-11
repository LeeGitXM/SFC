package com.ils.sfc.designer.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class AbstractStringEditorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	protected JTextArea textField = new JTextArea();
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	
	/** A simple dialog for editing text. A null result indicates Cancel was pressed. */
	public AbstractStringEditorDialog(Window owner) {
		super(owner, ModalityType.APPLICATION_MODAL);
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		panel.add(textField, BorderLayout.CENTER);
		textField.setEditable(true);
		textField.setLineWrap(true);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}
		});		
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doCancel();}
		});
		panel.add(buttonPanel, BorderLayout.SOUTH);
		this.setSize(400,200);
	}
	
	protected abstract void doOK();

	protected abstract void doCancel();

	
}