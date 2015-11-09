package com.ils.sfc.designer.panels;


import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ils.sfc.client.step.AbstractIlsStepUI;

/** This appears to the right of all recipe editor panels to provide
 *  add/remove/edit/accept actions. */
@SuppressWarnings("serial")
public class ButtonPanel extends JPanel {
	private static Icon checkIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/check.png"));
	private static Icon addIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/add.png"));
	private static Icon removeIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/delete.png"));
	private static Icon editIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/pencil.png"));
	private static Icon execIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/exclam.png"));
	private static Icon cancelIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/undo.png"));
	private static Icon backIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/back.png"));
	final JButton addButton = new JButton(addIcon);
	final JButton editButton = new JButton(editIcon);
	final JButton removeButton = new JButton(removeIcon);
	final JButton acceptButton = new JButton(checkIcon);
	final JButton execButton = new JButton(execIcon);
	final JButton cancelButton = new JButton(cancelIcon);
	final JButton backButton = new JButton(backIcon);
	final JLabel comboLabel = new JLabel();
	final JCheckBox checkBox = new JCheckBox();
	final Dimension buttonDimension = new Dimension(16,16);
	public static final  java.awt.Color background = new java.awt.Color(238,238,238);	

	public ButtonPanel(boolean showAccept, boolean showAdd, boolean showRemove, 
		boolean showEdit, boolean showExec, boolean showCancel, boolean showBack, boolean showCheckBox) {
		setBackground(background);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setPreferredSize(new Dimension(100,20));
		backButton.setToolTipText("Back");		
		acceptButton.setToolTipText("Accept");
		cancelButton.setToolTipText("Cancel");		
		addButton.setToolTipText("Add");
		removeButton.setToolTipText("Remove");
		editButton.setToolTipText("Edit");
		execButton.setToolTipText("Execute");
		if(showBack) addButton(backButton);
		if(showAccept) addButton(acceptButton);
		if(showCancel) addButton(cancelButton);
		if(showAdd) addButton(addButton);
		if(showRemove) addButton(removeButton);
		if(showEdit) addButton(editButton);
		if(showExec) addButton(execButton);
		if(showCheckBox) {
			add(Box.createRigidArea(new Dimension(50,10)));
			add(checkBox);
		}
	}
	
	public ButtonPanel() {
		this(true, true, true, true, true, true, true, true);
	}

	private void addButton(JButton button) {
		add(Box.createRigidArea(new Dimension(10,10)));
		button.setSize(buttonDimension);
		button.setContentAreaFilled(false);
		button.setBorder(null);
		add(button);		
	}
	
	public JButton getAddButton() {
		return addButton;
	}

	public JButton getEditButton() {
		return editButton;
	}

	public JButton getRemoveButton() {
		return removeButton;
	}

	public JButton getAcceptButton() {
		return acceptButton;
	}

	public JButton getExecButton() {
		return execButton;
	}

	public JLabel getComboLabel() {
		return comboLabel;
	}

	public JCheckBox getCheckBox() {
		return checkBox;
	}

	public JButton getBackButton() {
		return backButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

}
