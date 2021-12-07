package com.ils.sfc.gateway.locator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.python.core.PyObject;
import org.python.core.PyString;

import system.ils.sfc.common.Constants;

import com.inductiveautomation.ignition.common.browsing.BrowseFilter;
import com.inductiveautomation.ignition.common.expressions.TagListener;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.tags.browsing.NodeDescription;
import com.inductiveautomation.ignition.common.tags.model.TagPath;
import com.inductiveautomation.ignition.common.tags.model.event.TagChangeEvent;
import com.inductiveautomation.ignition.common.tags.paths.BasicTagPath;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value. */

@SuppressWarnings("serial")
public class RecipeDataChartScope extends PyChartScope {
	private static LoggerEx log = LogUtil.getLogger(RecipeDataChartScope.class.getName());
	private final static boolean DEBUG = false;
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
	
	public RecipeDataChartScope(String stepPath, RecipeDataChartScope parent, String providerName, GatewayContext gatewayContext) {
		this.stepPath = stepPath;
		this.parent = parent;
		this.providerName = providerName;
		this.gatewayContext = gatewayContext;
	}
	
	@Override
	public void removeScopeObserver(ScopeObserver observer) {
		for(ListenerInfo listenerInfo: listenersByKey.values()) {
			gatewayContext.getTagManager().unsubscribeAsync(listenerInfo.tagPath, listenerInfo.tagListener);
		}
		listenersByKey = null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		log.infof("containsKey: key %s", key.toString());
		return false;
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
		log.infof("Class name: %s",keyObj.getClass().getCanonicalName());
		key = (String)keyObj;
		String keyPath = getKeyPath();
		String strTagPath = Constants.RECIPE_DATA_FOLDER + "/" + stepPath + "/" + keyPath;
		if( DEBUG ) log.infof("get: key %s keyPath %s strTagPath", key.toString(), keyPath, strTagPath);
		TagPath igTagPath = new BasicTagPath(providerName, getPathComponents(strTagPath));
		List<TagPath> tagpaths = new ArrayList<TagPath>();
		tagpaths.add(igTagPath);
		List<QualifiedValue> tagValue = null;
		
		try {
			tagValue = gatewayContext.getTagManager().readAsync(tagpaths).get();
		} catch (Exception e) {
			log.errorf("Error reading tagpath: %s", strTagPath, e);
		}
		if(tagValue.size() > 0 && tagValue.get(0) != null && tagValue.get(0).getQuality().isNot(QualityCode.Bad_NotFound)) {
			log.errorf("tag %s not found", strTagPath);
			return new PyChartScope();
		}
		// get the child tags
		List<NodeDescription> nodes;
		try {
			nodes = new ArrayList<NodeDescription>(gatewayContext.getTagManager().browseAsync(igTagPath, BrowseFilter.NONE).get().getResults());
		
			PyChartScope resultScope = null;
			if(nodes.get(0).hasChildren()) {
				// a hierarchy exists, build sub-scope that will handle requests for sub-keys
				return new RecipeDataChartScope(stepPath, this, providerName, gatewayContext);
			}
			else{ 
				// Leaf level; read the values of all the UDT members and put them in a PyChartScope, 
				// which is the return result
				resultScope = new PyChartScope();
				for(NodeDescription node : nodes) {
					resultScope.put(node.getName(), node.getCurrentValue().getValue());
					if( DEBUG ) log.infof("get: adding to scope %s = %s", node.getName(), node.getCurrentValue().getValue());
					if(!listenersByKey.containsKey(strTagPath)) {
						addValueChangeListener(strTagPath, igTagPath);
					}
				}
				return resultScope;
			}
		}
		catch (Exception e) {
			log.errorf("Error browsing tagpath: %s", strTagPath, e);
		}
		return null;
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
		if( DEBUG ) log.infof("notifying observers for key %s", key);
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
				if( DEBUG ) log.infof("tag changed: %s", e.getTagPath());
			}
		};
		// Note: we only listen for changes in the "value" member of a recipe data tag
		if( DEBUG ) log.infof("addValueChangeListener: on %s.value", igTagPath.toString());
		igTagPath = (BasicTagPath) BasicTagPath.append(igTagPath, "value");
		listenersByKey.put(key, new ListenerInfo(igTagPath, tagListener));
		gatewayContext.getTagManager().subscribeAsync(igTagPath, tagListener);
	}	
}

