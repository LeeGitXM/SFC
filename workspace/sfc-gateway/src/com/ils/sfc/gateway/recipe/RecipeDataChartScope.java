package com.ils.sfc.gateway.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.core.PyObject;
import org.python.core.PyString;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.expressions.TagListener;
import com.inductiveautomation.ignition.common.sqltags.model.Tag;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.model.TagProp;
import com.inductiveautomation.ignition.common.sqltags.model.event.TagChangeEvent;
import com.inductiveautomation.ignition.common.sqltags.model.types.TagType;
import com.inductiveautomation.ignition.common.sqltags.parser.BasicTagPath;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value. */

@SuppressWarnings("serial")
public class RecipeDataChartScope extends PyChartScope {
	private static LoggerEx logger = LogUtil.getLogger(RecipeDataChartScope.class.getName());
	private String stepPath;
	private String providerName;
	private GatewayContext gatewayContext;
	private Map<String,ListenerInfo> listenersByKey = new HashMap<String, ListenerInfo>();
	private RecipeDataChartScope parent;
	private String key;
	
	static class ListenerInfo {
		public TagPath tagPath;
		public TagListener tagListener;
		
		public ListenerInfo(TagPath tagPath, TagListener tagListener) {
			super();
			this.tagPath = tagPath;
			this.tagListener = tagListener;
		}
	};
	
	RecipeDataChartScope(String stepPath, RecipeDataChartScope parent, String providerName, GatewayContext gatewayContext) {
		this.stepPath = stepPath;
		this.parent = parent;
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
	/** Get a dictionary with the values of a recipe datum. 
	 *  If no hierarchy is involved, the given key will be the name of the datum,
	 *  and the returned map will have keys for each of the UDT members (including value).
	 *  If there is a hierarchy, the parent datums will correspond to tag folders. If a
	 *  folder is given as the key param, we return RecipeDataAccess object with the names of 
	 *  subfolders as the keys.
	 */
	public Object get(Object keyObj) {		
		// Build the tag path as far as the name of the UDT
		key = (String)keyObj;
		String keyPath = getKeyPath();
		String strTagPath = Constants.RECIPE_DATA_FOLDER + "/" + stepPath + "/" + keyPath;
		logger.infof("get: key %s keyPath %s strTagPath", key, keyPath, strTagPath);
		TagPath igTagPath = new BasicTagPath(providerName, getPathComponents(strTagPath));
		Tag tag = gatewayContext.getTagManager().getTag(igTagPath);
		if(tag == null) {
			logger.errorf("tag %s not found", strTagPath);
			return new PyChartScope();
		}
		// get the child tags
		List<Tag> childTags = gatewayContext.getTagManager().browse(igTagPath);	
		PyChartScope resultScope = null;
		if(tag.getType() == TagType.Folder) {
			logger.info("is folder");
			// a hierarchy exists, build sub-scope that will handle requests for sub-keys
			return new RecipeDataChartScope(stepPath, this, providerName, gatewayContext);
		}
		else{ 
			logger.info("is leaf");
			// Leaf level; read the values of all the UDT members and put them in a PyChartScope, 
			// which is the return result
			resultScope = new PyChartScope();
			for(Tag childTag: childTags) {
				resultScope.put(childTag.getName(), childTag.getValue().getValue());
				logger.infof("putting value in scope %s %s", childTag.getName(), childTag.getValue().getValue());
				if(!listenersByKey.containsKey(strTagPath)) {
					addValueChangeListener(strTagPath, igTagPath);
				}
			}
			return resultScope;
		}
	}

	private String getKeyPath() {
		StringBuilder builder = new StringBuilder();
		if(parent != null) {
			parent.prependKey(builder);
		}
		builder.append(key);
		return builder.toString();
	}

	private void prependKey( StringBuilder builder) {
		builder.append(key);
		builder.append('/');
	}

	private List<String> getPathComponents(String tagPath) {
		List<String> pathComponents = new ArrayList<String>();
		String[] parts = tagPath.split("/|\\.");
		for(String part: parts) {
			pathComponents.add(part);
		}
		return pathComponents;
	}

	protected void notifyObservers() {
		logger.infof("notifiying observers for key %s", key);
		RecipeDataChartScope.this.notifyObservers(new PyString(key), (PyObject)null);
		if(parent != null) {
			parent.notifyObservers();
		}
	}
	
	private void addValueChangeListener(final String key, TagPath igTagPath) {
		TagListener tagListener = new TagListener() {
			@Override
			public void tagChanged(TagChangeEvent e) {
				notifyObservers();
				logger.infof("tag changed: %s", e.getTagPath());
			}
			public TagProp getTagProperty() {
				return TagProp.Value;
			}
		};
		// Note: we only listen for changes in the "value" member of a recipe data tag
		logger.infof("adding tag listener on %s.value", igTagPath.toString());
		igTagPath = (BasicTagPath) BasicTagPath.append(igTagPath, "value");
		listenersByKey.put(key, new ListenerInfo(igTagPath, tagListener));
		gatewayContext.getTagManager().subscribe(igTagPath, tagListener);
	}	
}

