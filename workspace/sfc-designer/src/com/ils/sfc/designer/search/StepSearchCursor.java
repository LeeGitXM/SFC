package com.ils.sfc.designer.search;



import com.ils.sfc.common.IlsSfcModule;
import com.ils.sfc.common.chartStructure.ChartStructureManager;
import com.ils.sfc.designer.IlsSfcDesignerHook;
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
	private final long resourceId;
	private final String parent;
	private final int searchKey;
	private final StepDefinition step;
	private final boolean searchStepName;
	private final boolean searchProperty;
	private final boolean searchRecipe;
	private int index = 0;
	private String stepName = null;

	public StepSearchCursor(DesignerContext ctx,String parentPath,long resid,StepDefinition sdef,int key) {
		this.context = ctx;
		this.parent = parentPath;
		this.resourceId = resid;  // For the chart
		this.searchKey = key;
		this.searchProperty = (searchKey&(IlsSfcSearchProvider.SEARCH_SCOPE+IlsSfcSearchProvider.SEARCH_SCOPE_DATA)) !=0;
		this.searchRecipe   = (searchKey&(IlsSfcSearchProvider.SEARCH_KEY+IlsSfcSearchProvider.SEARCH_DATA)) !=0;
		this.searchStepName = (searchKey&IlsSfcSearchProvider.SEARCH_STEP)!=0;
		this.step = sdef;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
	}
	@Override
	public Object next() {
		Object so = null;   // SearchObject
		if( index==0 ) {
			ChartStructureManager structureManager = ((IlsSfcDesignerHook)context.getModule(IlsSfcModule.MODULE_ID)).getChartStructureManager();
			stepName = structureManager.getStepName(step.getElementId().toString());
			if( stepName==null ) {
				log.infof("%s.next Failed to find step name %s(%s)",TAG,parent,step.getElementId().toString());
				return null;
			}
		}
		
		if( index==0 && searchStepName) {
			so = new StepNameSearchObject(context,parent,resourceId,stepName);
		}
		else {
			int subindex = (searchStepName?1:0);
			for(PropertyValue<?> pv:step.getProperties().getValues()) {
				if( searchRecipe && pv.getProperty().getName().equalsIgnoreCase("associated-data")) {
					if( subindex==index ) {
						so = new RecipeSearchCursor(context,parent,resourceId,stepName,pv,searchKey);
						break;
					}
					subindex++;
				}
				else if( searchProperty ) {
					if( subindex==index ) {
						so = new StepPropertySearchCursor(context,parent,resourceId,pv,searchKey);
						break;
					}
					subindex++;
				}
			}
		}
		index++;
		return so;
	}

}
