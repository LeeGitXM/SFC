package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

/** A sliding pane for displaying a message */
@SuppressWarnings("serial")
public class MessagePanel extends EditorPanel {
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	protected JTextArea testArea = new JTextArea();
	protected int returnIndex;
	
	public MessagePanel(PanelController controller, int index) {
		super(controller, index);
		testArea.setEditable(false);
		testArea.setLineWrap(true);
		add(testArea, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAddButton().setVisible(false);
		buttonPanel.getEditButton().setVisible(false);
		buttonPanel.getRemoveButton().setVisible(false);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}		
		});
	}
	
	public void setText(String text) {
		testArea.setText(text);
	}
	
}
