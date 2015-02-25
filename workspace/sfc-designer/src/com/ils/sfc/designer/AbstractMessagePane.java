package com.ils.sfc.designer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.ils.sfc.designer.ButtonPanel;
import com.ils.sfc.designer.EditorPane;

/** A sliding pane for displaying a message */
@SuppressWarnings("serial")
public abstract class AbstractMessagePane extends JPanel implements EditorPane {
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	protected JTextArea testArea = new JTextArea();
	protected int returnIndex;
	
	public AbstractMessagePane() {
		super(new BorderLayout());
		testArea.setEditable(false);
		testArea.setLineWrap(true);
		add(testArea, BorderLayout.CENTER);
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
	}
	
	public void setText(String text, int returnIndex) {
		testArea.setText(text);
		this.returnIndex = returnIndex;
	}
	
	public abstract void doOK();
	
}
