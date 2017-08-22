package com.ils.sfc.designer.search;

import org.w3c.dom.Element;

import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.designer.IlsSfcDesignerHook;
import com.inductiveautomation.ignition.common.config.PropertyValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.findreplace.SearchObjectCursor;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.definitions.ElementDefinition;
import com.inductiveautomation.sfc.definitions.ParallelDefinition;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.definitions.TransitionDefinition;
public class StepSearchCursor extends SearchObjectCursor {
	private final String TAG = "StepSearchCursor";
	private final LoggerEx log;
	private final DesignerContext context;
	private final long resourceId;
	private final String parent;
	private final int searchKey;
	private final Element step;
	private final boolean searchStep;
	private final boolean searchRecipe;
	private final boolean searchExpression;
	private int index = 0;
	private String stepName = null;

	public StepSearchCursor(DesignerContext ctx, String parentPath, long resourceId, Element element, int searchKey) {
		this.context = ctx;
		this.parent = parentPath;
		this.resourceId = resourceId;  // For the chart
		this.searchKey = searchKey;
		this.searchStep = (searchKey & IlsSfcSearchProvider.SEARCH_STEP) != 0;
		this.searchRecipe   = (searchKey & IlsSfcSearchProvider.SEARCH_RECIPE) != 0;
		this.searchExpression = (searchKey & IlsSfcSearchProvider.SEARCH_EXPRESSION) != 0;
		this.step = element;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		
		log.infof("%s.new - initializing a STEP search cursor for resource = %d", TAG, resourceId);
	}
	
	@Override
	public Object next() {
		log.infof("Searching the next step...");
		Object so = null;   // SearchObject
		index++;
		return so;
	}

}
