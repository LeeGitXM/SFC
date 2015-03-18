package com.ils.sfc.step;

import org.python.core.PyDictionary;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.gateway.IlsScopeLocator;
import com.inductiveautomation.ignition.common.script.JythonExecException;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.enclosing.EnclosingStep;

public class OperationStep extends EnclosingStep {
	private static LoggerEx logger = LogUtil.getLogger(OperationStep.class.getName());

	public OperationStep(ChartContext context, StepDefinition definition,
			ScopeContext scopeContext) {
		super(context, definition, scopeContext);
	}

	@Override
	public void activateStep() {
		super.activateStep();
		
		// message the client to update the current operation
		String stepName = getDefinition().getProperties().get(IlsProperty.NAME);
		PyChartScope topScope = IlsScopeLocator.getTopScope(getChartContext().getChartScope());
		String chartRunId = (String)topScope.get("instanceId");
		String projectName = (String)topScope.get("project");
		PyDictionary payload = new PyDictionary();
		payload.put("instanceId", chartRunId);
		payload.put("status", stepName);
		try {
			PythonCall.SEND_CURRENT_OPERATION.exec(projectName, payload);
		} catch (JythonExecException e) {
			logger.error("error sending chart status", e);
		}
	}
}