package com.ils.sfc.designer.recipeEditor.old.model;

import java.util.ArrayList;
import java.util.List;

public class RecipeInstance {
	private RecipeClass myClass;
	private List<Object> values;
	
	public RecipeInstance(RecipeClass myClass) {
		this.myClass = myClass;
		values = new ArrayList<Object>(myClass.getProperties().size());
	}
	
	@Override
	public String toString() {
		return myClass.getName();
	}
	
	public void setValue(int i, Object value) {
		values.set(i, value);
	}
}
