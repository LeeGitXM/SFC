package com.ils.sfc.designer.recipeEditor.old.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeClass {
	private static Map<String, RecipeClass> classesByName = new HashMap<String, RecipeClass>();

	private String name;
	private final List<RecipeProperty> properties = new ArrayList<RecipeProperty>();

	static {
		// debug
		addClass(new RecipeClass("hi"));
		addClass(new RecipeClass("there"));
		addClass(new RecipeClass("Rob"));
	}
	
	public RecipeClass(String name) {
		this.name = name;		
	}
	
	public static void addClass(RecipeClass rclass) {
		if(getClass(rclass.getName()) != null) {
			throw new IllegalArgumentException("class " + rclass.getName() + " already exists");
		}
		classesByName.put(rclass.getName(), rclass);
	}
	
	public static RecipeClass getClass(String name) {
		return classesByName.get(name);
	}
	
	public static Collection<RecipeClass> getClasses() {
		return classesByName.values();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RecipeProperty> getProperties() {
		return properties;
	}
	
	public void addProperty(RecipeProperty property) {
		if(getProperty(property.getName()) != null) {
			throw new IllegalArgumentException("property " + property.getName() + " is already present");
		}
		properties.add(property);
	}
	
	public RecipeProperty getProperty(String name) {
		for(RecipeProperty prop: properties) {
			if(prop.getName().equals(name)) {
				return prop;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return
			o != null && 
			o instanceof RecipeProperty && 
			name.equals(((RecipeProperty)o).getName());
	}
	
	@Override
	public String toString() {
		return name;
	}

	public static void remove(RecipeClass selectedClass) {
		classesByName.remove(selectedClass.getName());
	}
}
