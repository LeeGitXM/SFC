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
	
	static class ListenerInfo {
		public TagPath tagPath;
		public TagListener tagListener;
		
		public ListenerInfo(TagPath tagPath, TagListener tagListener) {
			super();
			this.tagPath = tagPath;
			this.tagListener = tagListener;
		}
	};
	
	RecipeDataChartScope(String stepPath, String providerName, GatewayContext gatewayContext) {
		this.stepPath = stepPath;
		this.providerName = providerName;
		this.gatewayContext = gatewayContext;
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
		String tagPath = Constants.RECIPE_DATA_FOLDER + "/" + stepPath + "/" + key;
		List<String> pathComponents = new ArrayList<String>();
		String[] parts = tagPath.split("/");
		for(String part: parts) {
			pathComponents.add(part);
		}
		
		// create multiple tag paths to each UDT member
		BasicTagPath igTagPath = new BasicTagPath(providerName, pathComponents);
		List<Tag> tags = gatewayContext.getTagManager().browse(igTagPath);

		
		// Read the values of all the UDT members and put them in a PyChartScope, which is the return result
		PyChartScope result = new PyChartScope();
		for(Tag tag: tags) {
			result.put(tag.getName(), tag.getValue().getValue());
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

	private void addTagChangeListener(final String key, BasicTagPath igTagPath) {
		TagListener tagListener = new TagListener() {
			@Override
			public void tagChanged(TagChangeEvent e) {
				RecipeDataChartScope.this.notifyObservers(new PyString(key), (PyObject)null);
			}
			public TagProp getTagProperty() {
				return TagProp.Value;
			}
		};
		// TODO: remove this hack:
		// we should really listen for changes in all the tags, but initially
		// we will just listen for a change in value:
		TagPath valueTagPath = BasicTagPath.append(igTagPath, "value");
		listenersByKey.put(key, new ListenerInfo(valueTagPath, tagListener));
		gatewayContext.getTagManager().subscribe(valueTagPath, tagListener);
	}	
}

