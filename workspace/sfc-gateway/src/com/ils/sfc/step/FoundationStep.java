package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.IlsSfcCommonUtils;
import com.ils.sfc.common.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStep;

public class FoundationStep extends EnclosingStep {
	protected ScopeContext scopeContext;
	private static final Logger logger = LoggerFactory.getLogger(FoundationStep.class);
	
	public FoundationStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
		this.scopeContext = scopeContext;
	}

	
	protected void exec(PythonCall pcall) {
		try {
			logger.trace(pcall.getMethodName());
			//indexElements(getChartContext());
			pcall.exec(scopeContext, getDefinition().getProperties());
		} catch (Exception e) {
			logger.error("Error calling " + pcall.getMethodName(), e);
		}
	}

	@Override
	public void activateStep(StepController controller) {
		String msgQueue = getDefinition().getProperties().get(IlsProperty.MESSAGE_QUEUE);
		if(!IlsSfcCommonUtils.isEmpty(msgQueue)) {
			PyChartScope topScope = IlsSfcCommonUtils.getTopScope(scopeContext.getChartScope());
			topScope.put(Constants.MESSAGE_QUEUE, msgQueue);
		}
		super.activateStep(controller);
	}

}
