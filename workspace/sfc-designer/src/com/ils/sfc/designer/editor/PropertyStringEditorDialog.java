package com.ils.sfc.designer.editor;

import java.awt.Frame;

@SuppressWarnings("serial")
public class PropertyStringEditorDialog extends AbstractStringEditorDialog {
	private PropertyTableModel model;
	private int row;
	
	/** A simple dialog for editing text. A null result indicates Cancel was pressed. */
	public PropertyStringEditorDialog(Frame owner, PropertyTableModel model, int row) {
		super(owner);
		this.model = model;
		this.row = row;
		PropertyRow rowObj = model.getRowObject(row);
		textField.setText((String)rowObj.getValue());
		setTitle("Edit " + rowObj.getDisplayLabel());
	}
	
	protected void doOK() {
		model.setValueAt(textField.getText(), row, PropertyTableModel.VALUE_COLUMN);
		this.dispose();
	}

	protected void doCancel() {
		this.dispose();
	}

}
