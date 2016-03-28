package com.ils.sfc.common.recipe.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.inductiveautomation.ignition.common.config.BasicPropertySet;

/**
superiorClass: sequence (the symbol S88-RECIPE-DATA)
attributes:sequence (structure (ATTRIBUTE-NAME: the symbol RECIPE-DATA-V2,
    ATTRIBUTE-TYPE-SPECIFICATION: the symbol S88-RECIPE-DATA-LIST,
    ATTRIBUTE-INITIAL-ITEM-CLASS: the symbol S88-RECIPE-DATA-LIST))
 */
public class Group extends Data {
	private List<Data> children = new ArrayList<Data>();
	
	public Group() {
		// in G2, this has a S88-RECIPE-DATA-LIST
	}
	
	public List<Data> getChildren() {
		return children;
	}

	protected void print(int level) {
		super.print(level);
		for(Data data: children) {
			data.print(level + 1);
		}
	}
			
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject jobj = super.toJSON();
		for(Data child: children) {
			jobj.put(child.getKey(), child.toJSON());
		}
		return jobj;
	}

	@Override
	protected void setFromJson(JSONObject jsonObj) throws Exception {
		super.setFromJson(jsonObj);
		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = jsonObj.keys();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			Object value = jsonObj.get(key);
			if(value instanceof JSONObject) {
				JSONObject childJSONObj = (JSONObject) value;
				addChild(Data.fromJson(childJSONObj));
			}
		}
	}

	// Create a Group that is the root recipe data object for a step
	public static JSONObject getStepData() throws JSONException {
		return (new Group()).toJSON();
	}

	public void addChild(Data child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void setStepPath(String chartPath) {
		super.setStepPath(chartPath);
		for(Data child: children) {
			child.setStepPath(chartPath);
		}
	}
	
	public void setProvider(String provider) {
		super.setProvider(provider);
		for(Data child: children) {
			child.setProvider(provider);
		}
	}
}
