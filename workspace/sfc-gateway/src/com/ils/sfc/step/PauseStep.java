package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.PauseStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class PauseStep extends IlsAbstractChartStep implements PauseStepProperties {
	private boolean paused = false;
	
	public PauseStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context, scopeContext, definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.PAUSE);
	}
	
	@Override
	public void pauseStep() {
		super.pauseStep();
		paused = true;
	}

	@Override
	public void deactivateStep() {
		super.deactivateStep();
		/** Prevent execution from passing beyond this step until this 
		 *  chart has been canceled, as cancellation 
		 *  may trickle down from the top-level chart
		 */
		while(!paused) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
		} 
		paused = false; // probably unnecessary...
	}
}
