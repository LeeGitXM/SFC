package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inductiveautomation.sfc.api.AbstractBlockingStep;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.ChartStep;
import com.inductiveautomation.sfc.api.PyChartScope;
import com.inductiveautomation.sfc.definitions.StepDefinition;

/**
 * This abstract class is the basis of all custom steps. 
 *  
 * The subclasses depend on the "ILSStep" class annotation
 * as the marker to group a particular subclass into the list of 
 * available executable block types.
 */
public abstract class IlsAbstractChartStep extends AbstractBlockingStep implements ChartStep {
	private static final Logger logger = LoggerFactory.getLogger(IlsAbstractChartStep.class);
	
	// keys into the scope dictionary:
	public static final String MESSAGE_QUEUE_KEY = "messageQueue";
	public static final String PARENT_SCOPE_KEY = "chart.parent";

	protected IlsAbstractChartStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	/** Insert a message in the appropriate queue. */
	protected void queueMessage(String message) {
		String messageQueueId = getMessageQueueId();
		if(messageQueueId != null) {
			
		}
		else {
			logger.error("Could not find message queue id for step " + toString());
		}
	}
	
	/** Get the parent scope of the given scope, or null if none exists. */
	private PyChartScope getParentScope(PyChartScope scope) {
		return (PyChartScope) scope.get(PARENT_SCOPE_KEY);
	}
	
	/** Get the ID for this step's message queue */
	protected String getMessageQueueId() {
		return (String)getObjectFromScopeTree(MESSAGE_QUEUE_KEY);
	}
	
	/** Get the value for the given key from the chart scope, going up the parent
	 *  scope tree to find it. Returns null if it is not found.
	 */
	protected Object getObjectFromScopeTree(String key) {
		return getObjectFromScopeTree(getContext().getChartScope(), key);
	}
	
	/** The recursive part of {@link #getObjectFromScopeTree(String)} */
	private Object getObjectFromScopeTree(PyChartScope chartScope, String key) {
		Object object = chartScope.get(key);
		if(object != null) {
			return object;
		}
		else if(getParentScope(chartScope) != null) {
			return getObjectFromScopeTree(getParentScope(chartScope), key);
		}
		else {
			return null;
		}
	}

	/** Get the IO object, going to parent context if necessary. */
	public IlsSfcIOIF getIO() {
		return (IlsSfcIOIF) getObjectFromScopeTree(IlsSfcIOIF.SCOPE_KEY);
	}
	
	/** Return a description of this step -- name ,id, etc. */
	public String toString() {
		// TODO: make a better name--maybe chart id, a name hierarchy and a unique step id...
		return getClass().getName();
	}
	
}
