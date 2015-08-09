package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.common.IlsProperty;
import com.ils.sfc.common.PythonCall;
import com.ils.sfc.step.annotation.ILSStep;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ScopeContext;
import com.inductiveautomation.sfc.api.elements.AbstractChartElement;
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
	private static final Logger logger = LoggerFactory.getLogger(IlsAbstractChartStep.class);
	protected ScopeContext scopeContext;
	protected enum Status {
		Activate, Pause, Resume, Cancel
	};
	
	protected IlsAbstractChartStep(ChartContext context,  ScopeContext scopeContext, StepDefinition definition) {
		super(context, definition);
		this.scopeContext = scopeContext;
	}
	
	public String getName() {
		return getName(getDefinition());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getName(); 
	}
	
	@Override
	public void activateStep() {
		setStatus(Status.Activate);
	}

	@Override
	public void deactivateStep() {
	}

	@Override
	public void pauseStep() {
		setStatus(Status.Pause);
	}

	@Override
	public void resumeStep() {
		setStatus(Status.Resume);
	}

	@Override
	public void cancelStep() {
		setStatus(Status.Cancel);
	}

	/** Set the status in the step scope so the Python code can see it */
	protected void setStatus(Status status) {
		scopeContext.getStepScope().put("_status", status.toString());
	}

	/** Set the status in the step scope so the Python code can see it */
	protected String getStatus() {
		return (String)scopeContext.getStepScope().get("_status");
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

	/** Get the name of a step from its definition */
	private String getName(StepDefinition definition) {
		return (String)definition.getProperties().getOrDefault(IlsProperty.NAME);
	}
	
}
