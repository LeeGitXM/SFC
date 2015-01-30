package com.ils.sfc.designer.recipeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.ils.sfc.common.recipe.objects.Data;
import com.ils.sfc.common.recipe.objects.Structure;
import com.ils.sfc.designer.propertyEditor.PropertyEditor;
import com.inductiveautomation.ignition.common.config.Property;

/** A thin wrapper for a PropertyEditor that adds an OK action */
@SuppressWarnings("serial")
public class PropertyEditorPane extends JPanel implements RecipeEditorController.RecipeEditorPane {
	private RecipeEditorController controller;
	private PropertyEditor editor = new PropertyEditor();
	private JButton okButton = new JButton("OK");
	private JPanel dynamicPropertyPanel;

	private Data recipeData;
	
	public PropertyEditorPane(RecipeEditorController controller) {
		super(new BorderLayout());
		this.controller = controller;
		add(editor, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		add(buttonPanel, BorderLayout.SOUTH);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doOK();}			
		});
		createDynamicPropertyPanel();
	}

	private void createDynamicPropertyPanel() {
		dynamicPropertyPanel = new JPanel();
		add(dynamicPropertyPanel, BorderLayout.NORTH);
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAdd();}			
		});
		dynamicPropertyPanel.add(addButton);
	
		JPanel buttonPanel = new JPanel();
		JButton removeButton = new JButton("Remove");
		buttonPanel.add(removeButton);
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemove();}			
		});
		dynamicPropertyPanel.add(removeButton);
	}

	private void doAdd() {
		controller.getFieldCreator().setRecipeData((Structure)recipeData);
		controller.slideTo(controller.getFieldCreator());
	}
	
	private void doRemove() {
		Property<?> selectedProperty = getPropertyEditor().getSelectedProperty();
		if(selectedProperty != null) {
			Structure structureData = (Structure) recipeData;
			structureData.removeDynamicProperty(selectedProperty);
			getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
		}
	}
	
	/** Do anything that needs to be done before re-showing this. */
	public void onShow() {
	}

	private void doOK() {
		recipeData.setProperties(editor.getPropertyValues());
		controller.slideTo(controller.getBrowser());
	}

	public PropertyEditor getPropertyEditor() {
		return editor;
	}

	public void setRecipeData(Data recipeData) {
		this.recipeData = recipeData;
		boolean isStructure = recipeData instanceof Structure;
		dynamicPropertyPanel.setVisible(isStructure);
		validate();
		getPropertyEditor().setPropertyValues(recipeData.getProperties(), false);
	}

}
