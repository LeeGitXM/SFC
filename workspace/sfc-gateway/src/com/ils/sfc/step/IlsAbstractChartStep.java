package com.ils.sfc.step;

import org.apache.log4j.LogManager;

import system.ils.sfc.common.Constants;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.step.annotation.ILSStep;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.AbstractChartElement;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/**
 * This abstract class is the basis of all custom steps. 
 *  
 * The subclasses depend on the "ILSStep" class annotation
 * as the marker to group a particular subclass into the list of 
 * available executable block types.
 */
@ILSStep
public abstract class IlsAbstractChartStep extends AbstractChartElement<StepDefinition> implements StepElement {
	private final LoggerEx logger = new LoggerEx(LogManager.getLogger("sfc.steps.IlsAbstractChartStep"));
	protected ScopeContext scopeContext;
	volatile boolean paused = false;
	volatile boolean cancelled = false;
	volatile boolean deactivated = false;
	volatile boolean done = false;
	private static final String WORK_DONE_FLAG = "workDone";
	
	protected IlsAbstractChartStep(ChartContext context,  ScopeContext scopeContext, StepDefinition definition) {
		super(context, definition);
		this.scopeContext = scopeContext;
	}

	protected boolean isRunning(){
		return !paused && !cancelled;
	}
	
	/** Repeatedly do increments of work. */
	protected void doWork(StepController controller) {
		logger.infof("Entering doWork");
		if(cancelled || deactivated) {
			logger.infof("In the if branch, the step has been cancelled or deactivated");
			// do cleanup
			callPython(true);
		}
		else {
			logger.infof("Entering the else branch");
			while (!cancelled && !paused && !done) {
				logger.infof("...looping...");
				done = callPython(false);
				if(!done) {
					// Some steps are simply waiting for a response...for performance reasons we don't
					// want get into a tight loop for that sort of thing, so we put in a small sleep:
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
				// The yield function ensures that all outstanding messages have been delivered through the chart
				// control queue. We call this so that we know for a fact that the cancelled and paused flags are as accurate as possible.
				controller.yield();
			}
		}
	}

	/** Call the step python and return true if no more calls are required. */
	private boolean callPython(boolean cleanup) {
		boolean workDone = true;
		String methodName = "??";
		try {
			PythonCall pcall = getPythonCall();
			methodName = pcall.getMethodName();
			Object result = pcall.exec(scopeContext, getDefinition().getProperties(), getState());
			if(result != null && result instanceof Boolean) {
				workDone = ((Boolean)result).booleanValue();
			}
			else {
				logger.errorf("ERROR: non-boolean return for step python %s: %s: ", 
					pcall.getMethodName(), (result != null ? result.toString() : "null"));
			}
		} catch (Exception e) {
			logger.errorf("Error running step python %s", methodName, e);
		}
		// note: just using "put" will not trigger change notification--use setVariable()
		scopeContext.getStepScope().setVariable(WORK_DONE_FLAG, workDone ? 1 : 0);
		return workDone || deactivated;
	}

	private String getState(){
		if(paused){
			return Constants.PAUSED;
		}
		else if(cancelled){
			return Constants.CANCELLED;
		}
		else if(deactivated){
			return Constants.DEACTIVATED;
		}
		else{
			return Constants.ACTIVATED;
		}
			
	}
	
	public String getName() {
		return getName(getDefinition());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getName(); 
	}
	
	@Override
	public void activateStep(StepController controller) {
		//logger.info("Step activating");
		// It is important for the correct function of Cancel steps that callPython be called
		// once outside of the controller:execute stuff:
		done = callPython(false);
		//Executing long running tasks through the controller allows the step to block chart flow (the step won't deactivate until the work finishes),
		//but still respond to pause/cancel, as we're not blocking the chart execution queue.
		if(!done) {
			controller.execute(this::doWork);
		}
	}

	@Override
	public void deactivateStep() {
		//logger.info("Step deactivated");
		deactivated = true;
		//In this example, we want to block flow until the work has finished. Therefore, we take no special action during deactivate.
	}

	@Override
	public void cancelStep() {
		//logger.infof("Step cancelled");
		cancelled = true;
	}

	@Override
	public void pauseStep() {
		//logger.infof("Example step paused.");
		paused = true;
	}

	@Override
	public void resumeStep(StepController controller) {
		//logger.infof("Example step resumed.");
		paused = false;
		//On resume, we can just continue the work that we were previously doing.
		controller.execute(this::doWork);
	}

	protected abstract PythonCall getPythonCall();
	
	/** Get the name of a step from its definition */
	private String getName(StepDefinition definition) {
		return (String)definition.getProperties().getOrDefault(IlsProperty.NAME);
	}
	
}
