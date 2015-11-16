package com.ils.sfc.gateway.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.python.core.PyObject;
import org.python.core.PyString;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.expressions.TagListener;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.Tag;
import com.inductiveautomation.ignition.common.sqltags.model.TagCollection;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.model.TagProp;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeEvent;
import com.inductiveautomation.ignition.common.sqltags.parser.BasicTagPath;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value. */

@SuppressWarnings("serial")
public class RecipeDataChartScope extends PyChartScope {
	private String stepPath;
	private String providerName;
	private GatewayContext gatewayContext;
	private Map<String,ListenerInfo> listenersByKey = new HashMap<String, ListenerInfo>();
	private boolean isRecipeDataTag;
	
	static class ListenerInfo {
		public TagPath tagPath;
		public TagListener tagListener;
		
		public ListenerInfo(TagPath tagPath, TagListener tagListener) {
			super();
			this.tagPath = tagPath;
			this.tagListener = tagListener;
		}
	};
	
	RecipeDataChartScope(String stepPath, String providerName, GatewayContext gatewayContext, boolean isRecipeDataTag) {
		this.stepPath = stepPath;
		this.providerName = providerName;
		this.gatewayContext = gatewayContext;
		this.isRecipeDataTag = isRecipeDataTag;
	}
	
	@Override
	public void removeScopeObserver(ScopeObserver observer) {
		for(ListenerInfo listenerInfo: listenersByKey.values()) {
			gatewayContext.getTagManager().unsubscribe(listenerInfo.tagPath, listenerInfo.tagListener);
		}
		listenersByKey = null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return true;
	}
	
	@Override 
	public Object get(Object key) {		
		// Build the tag path as far as the name of the UDT
		PyChartScope result = new PyChartScope();
		BasicTagPath igTagPath = null;
		if(isRecipeDataTag) {
			String tagPath = Constants.RECIPE_DATA_FOLDER + "/" + stepPath + "/" + key;
			List<String> pathComponents = getPathComponents(tagPath);
			
			// create multiple tag paths to each UDT member
			igTagPath = new BasicTagPath(providerName, pathComponents);
			List<Tag> tags = gatewayContext.getTagManager().browse(igTagPath);	
			
			// Read the values of all the UDT members and put them in a PyChartScope, which is the return result
			for(Tag tag: tags) {
				result.put(tag.getName(), tag.getValue().getValue());
			}
		}
		else {  // is a non-recipe tag
			List<String> pathComponents = getPathComponents((String)key);
			igTagPath = new BasicTagPath(providerName, pathComponents);
			Tag tag = gatewayContext.getTagManager().getTag(igTagPath);
			if(tag!= null) {
				Object tagValue = tag.getValue().getValue();
				// Limitation: we are not filling in any other tag properties
				// like AlarmActiveAckCount. We could if needed.
				// Hack: Experiment shows that transition expressions a) require one to add
				// ".value" or ".Value" but b) are case sensitive so since we
				// are not passed the final attributem we work around that by putting
				// both cases:
				result.put("value", tagValue);
				result.put("Value", tagValue);
			}
		}
		/*
		List<TagPath> paths = new ArrayList<TagPath>();
		paths.add(igTagPath);
		List<QualifiedValue> values = gatewayContext.getTagManager().read(paths);
		Object currentValue = values.get(0).getValue();
		*/
		if(!listenersByKey.containsKey(key)) {
			addTagChangeListener((String)key, igTagPath);
		}
		return result;
	}

	private List<String> getPathComponents(String tagPath) {
		List<String> pathComponents = new ArrayList<String>();
		String[] parts = tagPath.split("/|\\.");
		for(String part: parts) {
			pathComponents.add(part);
		}
		return pathComponents;
	}

	private void addTagChangeListener(final String key, TagPath igTagPath) {
		TagListener tagListener = new TagListener() {
			@Override
			public void tagChanged(TagChangeEvent e) {
				RecipeDataChartScope.this.notifyObservers(new PyString(key), (PyObject)null);
			}
			public TagProp getTagProperty() {
				return TagProp.Value;
			}
		};
		// Note: we only listen for changes in the "value" member of a recipe data tag
		if(isRecipeDataTag) {
			igTagPath = (BasicTagPath) BasicTagPath.append(igTagPath, "value");
		}
		listenersByKey.put(key, new ListenerInfo(igTagPath, tagListener));
		gatewayContext.getTagManager().subscribe(igTagPath, tagListener);
	}	
}

