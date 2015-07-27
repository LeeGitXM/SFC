package com.ils.sfc.gateway;

import java.util.ArrayList;
import java.util.List;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.expressions.TagListener;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.Tag;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
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
	private List<ListenerInfo> listeners = new ArrayList<ListenerInfo>();
	
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
		for(ListenerInfo listenerInfo: listeners) {
			gatewayContext.getTagManager().unsubscribe(listenerInfo.tagPath, listenerInfo.tagListener);
		}
		listeners = null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return true;
	}
	
	@Override 
	public Object get(Object key) {		
		String tagPath = Constants.RECIPE_DATA_FOLDER + "/" + stepPath + "/" + key;
		List<String> pathComponents = new ArrayList<String>();
		String[] parts = tagPath.split("/");
		for(String part: parts) {
			pathComponents.add(part);
		}
		BasicTagPath igTagPath = new BasicTagPath(providerName, pathComponents);
		List<Tag> tags = gatewayContext.getTagManager().browse(igTagPath);
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
		// TODO: listen to the tags so we can respond to changes
		/*
		TagListener tagListener = new TagListener() {
			@Override
			public void tagChanged(TagChangeEvent e) {
				//RecipeDataChartScope.this.notifyObservers();
			}
		};
		listeners.add(new ListenerInfo(igTagPath, tagListener));
		gatewayContext.getTagManager().subscribe(igTagPath, tagListener);
		*/
		return result;
	}	
}

