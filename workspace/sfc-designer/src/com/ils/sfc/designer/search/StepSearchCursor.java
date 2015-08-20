package com.ils.sfc.designer.search;



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
	private final ElementDefinition step;
	private final boolean searchStepName;
	private final boolean searchProperty;
	private final boolean searchRecipe;
	private final boolean searchExpression;
	private int index = 0;
	private String stepName = null;
	private final ElementDefinition.ElementType elementType;
	private final ChartStructureManager structureManager;

	public StepSearchCursor(DesignerContext ctx,String parentPath,long resid,ElementDefinition element,int key) {
		this.context = ctx;
		this.parent = parentPath;
		this.resourceId = resid;  // For the chart
		this.searchKey = key;
		this.searchProperty = (searchKey&(IlsSfcSearchProvider.SEARCH_SCOPE+IlsSfcSearchProvider.SEARCH_SCOPE_DATA)) !=0;
		this.searchRecipe   = (searchKey&(IlsSfcSearchProvider.SEARCH_KEY+IlsSfcSearchProvider.SEARCH_DATA)) !=0;
		this.searchStepName = (searchKey&IlsSfcSearchProvider.SEARCH_STEP)!=0;
		this.searchExpression = (searchKey&IlsSfcSearchProvider.SEARCH_EXPRESSION)!=0;
		this.step = element;
		this.log = LogUtil.getLogger(getClass().getPackage().getName());
		this.index = 0;
		this.structureManager = ((IlsSfcDesignerHook)context.getModule(IlsSfcModule.MODULE_ID)).getChartStructureManager();
		this.elementType = structureManager.getStepType(element.getElementId().toString());
	}
	@Override
	public Object next() {
		Object so = null;   // SearchObject


		if( index==0 && searchStepName && ElementDefinition.ElementType.Step.equals(elementType) ) {
			stepName = structureManager.getStepName(step.getElementId().toString());
			if( stepName==null ) {
				log.infof("%s.next Failed to find step name %s(%s)",TAG,parent,step.getElementId().toString());
				return null;
			}
			so = new StepNameSearchObject(context,parent,resourceId,stepName);
			//log.infof("%s.next Found step %s(%s)",TAG,parent,step.getElementId().toString());
		}
		else if( index==0 && searchExpression && ElementDefinition.ElementType.Parallel.equals(elementType) ) {
			ParallelDefinition element = (ParallelDefinition)step;
			so = new StepExpressionSearchObject(context,parent,resourceId,element);
		}
		else if( index==0 && searchExpression && ElementDefinition.ElementType.Transition.equals(elementType) ) {
			TransitionDefinition element = (TransitionDefinition)step;
			so = new StepExpressionSearchObject(context,parent,resourceId,element);
			//log.infof("%s.next Found transition %s(%s)",TAG,parent,step.getElementId().toString());
		}
		else {
			int subindex = (searchStepName?1:0);
			if( searchProperty && ElementDefinition.ElementType.Step.equals(elementType)) {
				StepDefinition stepDef = (StepDefinition)step;
				for(PropertyValue<?> pv:stepDef.getProperties().getValues()) {
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
		}
		index++;
		return so;
	}

}
