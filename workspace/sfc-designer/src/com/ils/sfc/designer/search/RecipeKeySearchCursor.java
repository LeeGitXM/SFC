package com.ils.sfc.designer.search;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class RecipeKeySearchCursor extends SearchObjectCursor {
	private final String TAG = "PropertySearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private final String parent;
	private JSONObject json = null;
	private int index = 0;

	public RecipeKeySearchCursor(DesignerContext ctx,String parentName,PropertyValue val) {
		this.context = ctx;
		this.parent = parentName;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		if( val.getValue() instanceof JSONObject ) this.json = (JSONObject) val.getValue();
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null; // SearchObject
		// We expect the type to be a JSONObject
		//log.infof("%s.next %d %s:%s",TAG,index,block.getName(),property.getName());
		int subindex = 0;
		Iterator<String> iter = json.keys();
		while( iter.hasNext() ) {
			String key = iter.next();
			if( subindex==index) {
				try {
					so = new JSONSearchCursor(context,parent,key,json.getJSONObject(key));
				}
				catch(JSONException jse) {
					log.warnf("%s.next: Exception getting value for key ^%s (&%s)", TAG,key,jse.getLocalizedMessage());
				}
				break;
			}
			subindex++;
		}
		
		index++;
		return so;
	}

}

