package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.CancelStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class CancelStep extends IlsAbstractChartStep implements CancelStepProperties {
	private boolean canceled;
	
	public CancelStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.CANCEL);
	}

	@Override
	public void cancelStep() {
		super.cancelStep();
		canceled = true;
	}

	@Override
	public void deactivateStep() {
		super.deactivateStep();
		/** Prevent execution from passing beyond this step until this 
		 *  chart has been paused, as we may have to wait until pausing 
		 *  has trickled down from the top-level chart
		 */
		while(!canceled) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		} 
		canceled = false; // probably not necessary
	}
}
