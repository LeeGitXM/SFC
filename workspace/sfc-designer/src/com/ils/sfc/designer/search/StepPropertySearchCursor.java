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
	private final long resourceId;
	private int index = 0;
	private final boolean searchName;
	private final boolean searchValue;

	public StepPropertySearchCursor(DesignerContext ctx,String parentName,long resid, PropertyValue val,int searchKey) {
		this.context = ctx;
		this.parent = parentName;
		this.pv = val;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		this.resourceId = resid;
		this.searchName = (searchKey&IlsSfcSearchProvider.SEARCH_SCOPE)!=0;
		this.searchValue = (searchKey&IlsSfcSearchProvider.SEARCH_SCOPE_DATA)!=0;
	}
	@Override
	public Object next() {
		Object so = null;   // SearchObject
		if( index==0 && searchName ) {
			so = new StepPropertySearchObject(context,parent,resourceId,pv.getProperty().getName(),pv.getProperty().getName());
		}
		else if( searchValue &&  index== (searchName?1:0) ) {
			so = new StepPropertySearchObject(context,parent,resourceId,pv.getProperty().getName(),pv.getValue().toString());
		}
		index++;
		return so;
	}

}
