package com.ils.sfc.step;

import org.w3c.dom.Element;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.step.annotation.ILSStep;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.AbstractChartElement;
import com.inductiveautomation.sfc.api.elements.StepController;
import com.inductiveautomation.sfc.api.elements.StepElement;
import com.inductiveautomation.sfc.definitions.StepDefinition;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

import system.ils.sfc.common.Constants;

/**
 * This abstract class is the basis of all custom steps. 
 *  
 * The subclasses depend on the "ILSStep" class annotation
 * as the marker to group a particular subclass into the list of 
 * available executable block types.
 */
@ILSStep
public abstract class IlsAbstractChartStep extends AbstractChartElement<StepDefinition> implements StepElement {
	private final LoggerEx logger =LogUtil.getLogger(getClass().getPackage().getName());
	protected ScopeContext scopeContext;
	volatile boolean paused = false;
	volatile boolean cancelled = false;
	volatile boolean deactivated = false;
	volatile boolean resumed = false;
	volatile boolean done = false;
	private static final String WORK_DONE_FLAG = "workDone";
	private static final String WAITING_FOR_REPLY = "waitingForReply";
	
	protected IlsAbstractChartStep(ChartContext context,  ScopeContext scopeContext, StepDefinition definition) {
		super(context, definition);
		this.scopeContext = scopeContext;
		logger.trace("Constructing...");
	}

	protected boolean isRunning(){
		return !paused && !cancelled;
	}
	
	/** Repeatedly do increments of work. */
	protected void doWork(StepController controller) {
		logger.trace("Entering doWork");
		if(cancelled || deactivated) {
			logger.infof("In the if branch, the step has been cancelled or deactivated");
			// do cleanup
			callPython(getState());
		}
		else {
			logger.trace("Starting the work loop...");
			while (!cancelled && !paused && !done) {
				logger.trace("...doing work...");
				done = callPython(getState());
				if(!done) {
					// Some steps are simply waiting for a response...for performance reasons we don't
					// want get into a tight loop for that sort of thing, so we put in a small sleep:
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
				
				// The state can only be resumed for one iteration
				if (getState() == Constants.RESUMED){
					logger.trace("...clearing the resumed flag...");
					resumed = false;
				}
				// The yield function ensures that all outstanding messages have been delivered through the chart
				// control queue. We call this so that we know for a fact that the cancelled and paused flags are as accurate as possible.
				controller.yield();
			}
			if (paused){
				logger.trace("...doing PAUSED work...");
				callPython(Constants.PAUSED);
			}
			if (cancelled){
				logger.trace("...doing CANCELLED work...");
				callPython(Constants.CANCELLED);
			}
			if (deactivated){
				logger.trace("...doing DEACTIVATED work...");
				callPython(Constants.DEACTIVATED);
				
//				logger.trace("Resetting WORK_DONE_FLAG to False (2)");
//				scopeContext.getStepScope().setVariable(WORK_DONE_FLAG, 0);
			}
			logger.trace("Done working!");
		}
	}

	/** Call the step python and return true if no more calls are required. */
	private boolean callPython(String state) {
		boolean workDone = true;
		String methodName = "??";
		try {
			PythonCall pcall = getPythonCall();
			methodName = pcall.getMethodName();
			Object result = pcall.exec(scopeContext, getDefinition().getProperties(), state);
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
//		logger.info("In getState()");
//		logger.infof("  Paused:      %b", paused);
//		logger.infof("  Deactivated: %b", deactivated);
//		logger.infof("  Canelled:    %b", cancelled);
//		logger.infof("  Resumed:     %b", resumed);
		
		if(paused){
			return Constants.PAUSED;
		}
		else if(cancelled){
			return Constants.CANCELLED;
		}
		else if(deactivated){
			return Constants.DEACTIVATED;
		}
		else if(resumed){
			return Constants.RESUMED;
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
		logger.trace("Step activating");

		// Added by Pete to handle a step in a loop to initialize things the second time around
		paused = false;
		cancelled = false;
		deactivated = false;
		resumed = false;
		done = false;
		
		// These twp initialization steps are needed for a Long Running Block the second time thrugh a chart loop.
		scopeContext.getStepScope().setVariable(WORK_DONE_FLAG, 0);
		scopeContext.getStepScope().setVariable(WAITING_FOR_REPLY, 0);
		
		// It is important for the correct function of Cancel steps that callPython be called
		// once outside of the controller:execute stuff:
		done = callPython(getState());
		//Executing long running tasks through the controller allows the step to block chart flow (the step won't deactivate until the work finishes),
		//but still respond to pause/cancel, as we're not blocking the chart execution queue.
		if(!done) {
			controller.execute(this::doWork);
		}
	}

	@Override
	public void deactivateStep() {
		logger.trace("Step deactivated");
		deactivated = true;
		//In this example, we want to block flow until the work has finished. Therefore, we take no special action during deactivate.
		
		// note: just using "put" will not trigger change notification--use setVariable()
		// logger.trace("Resetting WORK_DONE_FLAG to False (1)");
		// scopeContext.getStepScope().setVariable(WORK_DONE_FLAG, 0);
	}

	@Override
	public void cancelStep() {
		logger.trace("Step cancelled");
		cancelled = true;
	}

	@Override
	public void pauseStep() {
		logger.trace("Step paused.");
		paused = true;
	}

	@Override
	public void resumeStep(StepController controller) {
		logger.trace("Step resumed.");
		paused = false;
		resumed = true;
		//On resume, we can just continue the work that we were previously doing.
		controller.execute(this::doWork);
	}
	
	public String fromXML(Element element,ChartUIElement chartUIElement) {
		return "XML";		
	}
	
	protected abstract PythonCall getPythonCall();
	
	/** Get the name of a step from its definition */
	private String getName(StepDefinition definition) {
		logger.info("getName is Getting called.");
		return (String)definition.getProperties().getOrDefault(IlsProperty.NAME);
	}
	
}
