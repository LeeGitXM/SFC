package com.ils.sfc.gateway.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.python.core.PyObject;
import org.python.core.PyString;

import com.inductiveautomation.ignition.common.expressions.TagListener;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.tags.model.TagPath;
import com.inductiveautomation.ignition.common.tags.model.TagProvider;
import com.inductiveautomation.ignition.common.tags.model.event.TagChangeEvent;
import com.inductiveautomation.ignition.common.tags.paths.parser.TagPathParser;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.sfc.api.PyChartScope;

/** A wrapper around a chart scope that actually goes out to recipe data
 *  tags to get the value. */

@SuppressWarnings("serial")
public class TagChartScope extends PyChartScope {
	private static LoggerEx log = LogUtil.getLogger(TagChartScope.class.getName());
	private final static boolean DEBUG = false;
	private final String providerName;
	private final GatewayContext context;
	private final Map<String,ListenerInfo> listenersByKey = new HashMap<String, ListenerInfo>();
	private String key;
	
	public class ListenerInfo {
		public TagPath tagPath;
		public TagListener tagListener;
		
		public ListenerInfo(TagPath tagPath, TagListener tagListener) {
			super();
			this.tagPath = tagPath;
			this.tagListener = tagListener;
		}
	};
	
	public TagChartScope(String providerName, GatewayContext gatewayContext) {
		this.providerName = providerName;
		this.context = gatewayContext;
		if( DEBUG ) log.infof("new tagchartscope");
	}
	
	@Override
	public void removeScopeObserver(ScopeObserver observer) {
		for(ListenerInfo listenerInfo: listenersByKey.values()) {
			context.getTagManager().unsubscribeAsync(listenerInfo.tagPath, listenerInfo.tagListener);
		}
		listenersByKey.clear();
	}
	
	// The object is a string.
	@Override
	public boolean containsKey(Object keyObj) {
		if( DEBUG ) log.infof("containsKey: %s", keyObj.toString());
		return true;
	}
	
	/**
	 * This is a new function that causes keys like "a.b.c" to be sent in their entirety.
	 * We are expecting each tag directory separately.
	 */
	//@Override
	public boolean supportsSubScopes() {
		return true;
	}
		
	@Override 
	/** 
	 * @return the value of the tag specified by the key. 
	 */
	public Object get(Object keyObj) {		
		key = (String)keyObj;   // This is the tag path, less provider
		String path = String.format("[%s]%s",providerName,key);
		try {
			TagPath tp = TagPathParser.parse(path);
			ArrayList<TagPath> tpWrapper = new ArrayList();
			tpWrapper.add(tp);
			String providerName = tp.getSource();
			TagProvider provider = context.getTagManager().getTagProvider(providerName);
			if( provider!=null) {
				QualifiedValue tag = provider.readAsync(tpWrapper, null).get().get(0);
				if(tag != null) {
					// Read the tag return its value.
					if( DEBUG ) log.infof("get: return %s = %s", tp.getItemName(), tag.getValue());
					if(!listenersByKey.containsKey(tp.getItemName())) {
						addValueChangeListener(tp.getItemName(), tp);
					}
					return tag.getValue();
				}
				else {
					log.errorf("get: tag %s not found", tp.toString());
				}
			}
			else {
				log.errorf("get: provider %s not found", providerName);
			}
		}
		catch(IOException ioe) {
			log.warnf("get: Exception parsing path %s",path);
		}
		catch(NullPointerException npe) {
			log.warnf("get: Null value for path %s",path);
		} catch (InterruptedException e) {
			//caused by thread interrupt, no need to log
		} catch (ExecutionException e) {
			log.warnf("get: error reading value for path %s",  path, e);
		}
		return "";   // Error return
	}

	
	protected void notifyObservers() {
		if( DEBUG ) log.infof("notifying observers for key %s", key);
		TagChartScope.this.notifyObservers(new PyString(key), (PyObject)null);
	}
	
	private void addValueChangeListener(final String key, TagPath tagPath) {
		TagListener tagListener = new TagListener() {
			@Override
			public void tagChanged(TagChangeEvent e) {
				notifyObservers();
				if( DEBUG ) log.infof("tag changed: %s", e.getTagPath());
			}
		};
		// Note: we only listen for changes in the "value" member of a recipe data tag
		if( DEBUG ) log.infof("addValueChangeListener: on %s.value", tagPath.toString());
		listenersByKey.put(key, new ListenerInfo(tagPath, tagListener));
		context.getTagManager().subscribeAsync(tagPath, tagListener);
	}	
}

