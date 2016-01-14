package com.inductiveautomation.examples.sfc.gateway;

import com.inductiveautomation.examples.sfc.common.ExampleStepProperties;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import org.apache.log4j.LogManager;

/**
 * Created by carlg on 12/5/2015.
 */
public class ExampleStep implements StepElement {
	private final LoggerEx logger = new LoggerEx(LogManager.getLogger("sfc.steps.ExampleStep"));
	private final StepDefinition def;
	private final String name;
	private final int number;
	private int counter = 0;
	volatile boolean paused = false;
	volatile boolean cancelled = false;

	public ExampleStep(StepDefinition def) {
		this.def = def;
		name = def.getProperties().getOrDefault(ChartStepProperties.Name);
		number = def.getProperties().getOrDefault(ExampleStepProperties.EXAMPLE_PROPERTY);
	}

	protected boolean isRunning(){
		return !paused && !cancelled;
	}

	protected void doWork(StepController controller) {
		try {
			while (isRunning() && counter < number) {
				Thread.sleep(1000);

				counter++;

				// The yield function ensures that all outstanding messages have been delivered through the chart
				// control queue. We call this so that we know for a fact that the cancelled and paused flags are as accurate as possible.
				controller.yield();
			}
		} catch (Exception e) {
			logger.error("Error running example step", e);
		}
	}

	@Override
	public void activateStep(StepController controller) {
		logger.infof("Example step '%s' activating, will delay for %d seconds", name, number);
		//Executing long running tasks through the controller allows the step to block chart flow (the step won't deactivate until the work finishes),
		//but still respond to pause/cancel, as we're not blocking the chart execution queue.
		controller.execute(this::doWork);
	}

	@Override
	public void deactivateStep() {
		logger.infof("Example step deactivated");
		//In this example, we want to block flow until the work has finished. Therefore, we take no special action during deactivate.
	}

	@Override
	public void cancelStep() {
		logger.infof("Example step cancelled");
		cancelled = true;
	}

	@Override
	public void pauseStep() {
		logger.infof("Example step paused.");
		paused = true;
	}

	@Override
	public void resumeStep(StepController controller) {
		logger.infof("Example step resumed.");
		paused = false;
		//On resume, we can just continue the work that we were previously doing.
		controller.execute(this::doWork);
	}

	@Override
	public StepDefinition getDefinition() {
		return def;
	}
}
