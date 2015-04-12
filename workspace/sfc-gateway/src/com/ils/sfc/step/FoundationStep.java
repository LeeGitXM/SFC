package com.ils.sfc.step;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStep;

public class FoundationStep extends EnclosingStep {
	protected ScopeContext scopeContext;
	
	public FoundationStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		String msgQueue = getDefinition().getProperties().get(IlsProperty.MESSAGE_QUEUE);
		if(!IlsSfcCommonUtils.isEmpty(msgQueue)) {
			PyChartScope topScope = IlsSfcCommonUtils.getTopScope(scopeContext.getChartScope());
			topScope.put(IlsSfcNames.MESSAGE_QUEUE, msgQueue);
		}
		super.activateStep();
	}

	@Override
	public void deactivateStep() {
		super.deactivateStep();
	}

}
