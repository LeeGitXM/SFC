package com.ils.sfc.step;

import com.ils.sfc.common.PythonCall;
import com.ils.sfc.common.step.TimedDelayStepProperties;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.definitions.StepDefinition;

public class TimedDelayStep extends IlsAbstractChartStep implements TimedDelayStepProperties {
	private boolean pause;
	
	public TimedDelayStep(ChartContext context, ScopeContext scopeContext, StepDefinition definition) {
		super(context,scopeContext,  definition);
		this.scopeContext = scopeContext;
	}

	@Override
	public void activateStep() {
		super.activateStep();
		exec(PythonCall.TIMED_DELAY);	
		/*
		 * TODO: use startMillis and measure actual elapsed time
		final int delayIncrementSeconds = 10;
		int totalDelaySeconds = exec(PythonCall.TIMED_DELAY_1)
		int secondsDelayed = 0;
		while(!pause && secondsDelayed < totalDelaySeconds) {
			int secondsToDelay = Math.max(0, totalDelaySeconds - secondsDelayed)
			Thread.sleep(1000*secondsToDelay);
		}
		exec(PythonCall.TIMED_DELAY_2)
		catch(InterruptedException e) {}
		 * */
	}

	@Override
	public void pauseStep() {
	}

	@Override
	public void resumeStep() {
	}
}
