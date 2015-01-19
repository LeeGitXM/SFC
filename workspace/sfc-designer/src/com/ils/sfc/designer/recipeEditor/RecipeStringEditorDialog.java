package com.ils.sfc.designer.recipeEditor;

import java.awt.Window;

import com.ils.sfc.designer.propertyEditor.AbstractStringEditorDialog;

@SuppressWarnings("serial")
public class RecipeStringEditorDialog extends AbstractStringEditorDialog {
	private String result;
	
	/** A simple dialog for editing text. A null result indicates Cancel was pressed. */
	public RecipeStringEditorDialog(Window owner, String currentValue, String title) {
		super(owner);
		textField.setText(currentValue);
		setTitle(title);
	}
	
	protected void doOK() {
		result = textField.getText();
		this.dispose();
	}

	public String getResult() {
		return result;
	}
	
	protected void doCancel() {
		this.dispose();
	}

}