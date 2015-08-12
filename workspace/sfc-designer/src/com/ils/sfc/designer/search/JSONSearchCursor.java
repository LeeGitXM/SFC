package com.ils.sfc.designer.search;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

public class JSONSearchCursor extends SearchObjectCursor {
	private final String TAG = "PropertySearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private final String key;
	private final String parent;
	private final long resourceId;
	private Object value = null;
	private int index = 0;

	public JSONSearchCursor(DesignerContext ctx,String parentName,long resid,String keyName,Object val) {
		this.context = ctx;
		this.key = keyName;
		this.parent = parentName;
		this.resourceId = resid;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.value = val;
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null; // SearchObject
		// We expect the type to be a JSONObject or a simple
		//log.infof("%s.next %d %s:%s",TAG,index,block.getName(),property.getName());
		
		if( value instanceof JSONObject ) {
			int subindex = 0;
			Iterator<String> iter = ((JSONObject)value).keys();
			while( iter.hasNext() ){
				String k = iter.next();
				if( subindex==index ) {
					try {
						so= new JSONSearchCursor(context,key,resourceId,k,((JSONObject)value).get(k));
					}
					catch(JSONException jse) {
						log.warnf("%s.next: Exception getting value for key ^%s (&%s)", TAG,k,jse.getLocalizedMessage());
					}
					break;
				}
				subindex++;
			}
		}
		else if(index==0) {
			so= new RecipeValueSearchObject(context,key,resourceId,value.toString());
		}

		index++;
		return so;
	}

}

