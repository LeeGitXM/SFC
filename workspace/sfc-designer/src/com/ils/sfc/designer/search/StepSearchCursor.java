package com.ils.sfc.designer.search;



import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
public class StepSearchCursor extends SearchObjectCursor {
	private final String TAG = "StepSearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private final String parent;
	private final StepDefinition step;
	private int index = 0;

	public StepSearchCursor(DesignerContext ctx,String parentName,StepDefinition sdef) {
		this.context = ctx;
		this.parent = parentName;
		this.step = sdef;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null;   // SearchObject
		int subindex = 0;
		for(PropertyValue<?> pv:step.getProperties().getValues()) {
			if( subindex==index ) {
				if( pv.getProperty().getName().equalsIgnoreCase("associated-data")) {
					so = new RecipeKeySearchCursor(context,parent,pv);
				}
			}
			else {
				so = new StepPropertySearchCursor(context,parent,pv);
			}
			subindex++;
		}
		index++;
		return so;
	}

}
