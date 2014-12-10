package com.ils.sfc.designer.recipeEditor.old.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import com.ils.sfc.designer.recipeEditor.old.model.RecipeClass;

@SuppressWarnings("serial")
public class ClassesListModel extends AbstractListModel<RecipeClass> {
	private List<RecipeClass> classes = new ArrayList<RecipeClass>();
	
	public List<RecipeClass> getClasses() {
		return classes;
	}

	public void setClasses(Collection<RecipeClass> newClasses) {
		classes.clear();
		classes.addAll(newClasses);
		fireContentsChanged(this, 0, classes.size());
	}

	@Override
	public int getSize() {
		return classes.size();
	}

	@Override
	public RecipeClass getElementAt(int index) {
		return classes.get(index);
	}

}
