package com.ils.sfc.designer.recipeEditor.old.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.ils.sfc.designer.recipeEditor.old.model.RecipeClass;

@SuppressWarnings("serial")
public class ClassesPanel extends JPanel {
	private DefaultListModel<RecipeClass> listModel = new DefaultListModel<RecipeClass>();
	private JList<RecipeClass> list = new JList<RecipeClass>(listModel);
	
	public ClassesPanel() {
		setLayout(new BorderLayout());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(list), BorderLayout.CENTER);
		EditButtonPanel buttonPanel = new EditButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doAddClass();}			
		});
		buttonPanel.editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doEditClass();}			
		});
		buttonPanel.removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {doRemoveClass();}			
		});
		loadClasses();
	}

	private void loadClasses() {
		for(RecipeClass aClass: RecipeClass.getClasses()) {
			listModel.addElement(aClass);
		}
	}
	
	private void doAddClass() {
	}

	private void doEditClass() {
		RecipeClass selectedClass = list.getSelectedValue();
		if(selectedClass != null) {
		}
		else {
			JOptionPane.showMessageDialog(this, "No class is selected", "No Selection", JOptionPane.WARNING_MESSAGE);
		}
	}	

	private void doRemoveClass() {
		RecipeClass selectedClass = list.getSelectedValue();
		if(selectedClass != null) {
			int response = JOptionPane.showConfirmDialog(this, "Do you really want to delete this class?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
			if(response == JOptionPane.YES_OPTION) {
				RecipeClass.remove(selectedClass);
				loadClasses();
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "No class is selected", "No Selection", JOptionPane.WARNING_MESSAGE);
		}
	}
}
