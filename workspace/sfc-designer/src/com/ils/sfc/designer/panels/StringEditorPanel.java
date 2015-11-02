package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

import com.ils.sfc.designer.propertyEditor.ValueHolder;

/** Basically just a big text area for editing long strings. */
@SuppressWarnings("serial")
public class StringEditorPanel extends ValueHoldingEditorPanel {
	protected JTextArea textField = new JTextArea();
	private ButtonPanel buttonPanel = new ButtonPanel(true, false, false, false, false, false);
	
	public StringEditorPanel(PanelController controller, int index) {
		super(controller, index);
		textField.setLineWrap(true);
		textField.setWrapStyleWord(true);
		add(textField, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.getAcceptButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {accept();}		
		});
	}

	/** Do anything that needs to be done before re-showing this. */
	@Override
	public void activate(ValueHolder valueHolder) {
		textField.requestFocus();
		super.activate(valueHolder);
	}

	@Override
	public void setValue(Object text) {
		textField.setText((String)text);
	}

	@Override
	public Object getValue() {
		return textField.getText();
	}
	
}
