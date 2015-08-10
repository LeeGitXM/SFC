package com.ils.sfc.designer.search;


/**
 * Iteration over a property is trivial. First time we 
 * get the name, next time the value.
 */
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
public class StepPropertySearchCursor extends SearchObjectCursor {
	private final String TAG = "StepPropertySearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private final String parent;
	private final PropertyValue pv;
	private int index = 0;

	public StepPropertySearchCursor(DesignerContext ctx,String parentName,PropertyValue val) {
		this.context = ctx;
		this.parent = parentName;
		this.pv = val;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null;   // SearchObject
		// First time through only - get the XML and name
		if( index==0 ) {
			so = new StepPropertySearchObject(context,parent,pv.getProperty().getName());
		}
		else if( index==1 ) {
			so = new StepPropertySearchObject(context,pv.getProperty().getName(),pv.getValue().toString());
		}
		index++;
		return so;
	}

}
