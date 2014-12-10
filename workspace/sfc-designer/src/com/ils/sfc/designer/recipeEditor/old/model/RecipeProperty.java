package com.ils.sfc.designer.recipeEditor.old.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RecipeProperty {
	public static enum ValueTypes {FLOAT, INT, STRING, ARRAY, MATRIX};
	private static Map<String, RecipeProperty> propertiesByName = new HashMap<String, RecipeProperty>();
	private String name; // key in the dictionary; subject to Python key name constraints
	private String label; // in the dictionary
	private String description; // in the dictionary
	private ValueTypes valueType; // double, int, vector, array...
	private String unitType;  // may be null
	private String unitName;  // may be null
	private String defaultFormattedValue;
	
	// unit
	
	public static Map<String, RecipeProperty> getPropertiesByName() {
		return propertiesByName;
	}

	public static void setPropertiesByName(
			Map<String, RecipeProperty> propertiesByName) {
		RecipeProperty.propertiesByName = propertiesByName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ValueTypes getValueType() {
		return valueType;
	}

	public void setValueType(ValueTypes valueType) {
		this.valueType = valueType;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getDefaultFormattedValue() {
		return defaultFormattedValue;
	}

	public void setDefaultFormattedValue(String defaultFormattedValue) {
		this.defaultFormattedValue = defaultFormattedValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Collection<RecipeProperty> getProperties() {
		return propertiesByName.values();
	}
	
	public RecipeProperty(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void addProperty(RecipeProperty p) {
		if(getProperty(p.getName()) != null) {
			throw new IllegalArgumentException("property " + p.getName() + "already exists.");
		}
		propertiesByName.put(p.getName(), p);
	}

	public RecipeProperty getProperty(String name) {
		return propertiesByName.get(name);
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
}

	
