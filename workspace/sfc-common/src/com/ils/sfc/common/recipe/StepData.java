package com.ils.sfc.common.recipe;

/** An object corresponding to an SFC step. */
public class StepData {
	public String name;  // required
	public String id;  // required
	public String factoryId;  // required
	public String type;  // required
	public String parentId;  // nullable
	public String previousStepId;  // nullable
	public RecipeDataMap data;  // nullable
	
	public StepData(String name, String id, String factoryId, String type) {
		super();
		this.name = name;
		this.id = id;
		this.factoryId = factoryId;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getPreviousStepId() {
		return previousStepId;
	}
	
	public void setPreviousStepId(String previousStepId) {
		this.previousStepId = previousStepId;
	}
	
	public String getId() {
		return id;
	}
	
	public String getFactoryId() {
		return factoryId;
	}
	
	public String getType() {
		return type;
	}
	
	public void put(String key, Object value) {
		data.put(key, value);
	}
	
	public void remove(String key, Object value) {
		data.remove(key);
	}
	
	public void clearData(String key, Object value) {
		data = null;
	}
	
	public Object get(String path) throws RecipeDataException {
		if(data == null) throw new RecipeDataException("step " + id + " has no local data");
		return data.pathGet(path);
	}
	
	public void set(String path, Object value, boolean create) throws RecipeDataException {
		if(data == null) {
			data = new RecipeDataMap();
		}
		data.pathPut(path, value, create);
	}
}
