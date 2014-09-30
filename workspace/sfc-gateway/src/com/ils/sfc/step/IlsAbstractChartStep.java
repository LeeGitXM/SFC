package com.ils.sfc.step;

import org.python.core.Py;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.step.annotation.ILSStep;
import com.ils.sfc.util.IlsSfcIOIF;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.ChartContext;
import com.inductiveautomation.sfc.api.PyChartScope;
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
	
	// keys into the scope dictionary:
	public static final String MESSAGE_QUEUE_KEY = "messageQueue";
	public static final String PARENT_SCOPE_KEY = "chart.parent";

	protected IlsAbstractChartStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}
	
	/** Get the parent scope of the given scope, or null if none exists. */
	private PyChartScope getParentScope(PyChartScope scope) {
		return (PyChartScope) scope.get(PARENT_SCOPE_KEY);
	}
	
	/** Get the ID for this step's message queue */
	protected String getCurrentMessageQueue() {
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

	protected void setObjectInScopeTree(String key, Object value) {
		getContext().getChartScope().__set__(Py.java2py(key), Py.java2py(value));
	}
	
	/** Get the IO object, going to parent context if necessary. */
	public IlsSfcIOIF getIO() {
		return (IlsSfcIOIF) getObjectFromScopeTree(IlsSfcIOIF.SCOPE_KEY);
	}
	
	public String getStringProperty(Property<String> prop) {
		return getDefinition().getProperties().getOrDefault(prop);
	}

	public Double getDoubleProperty(Property<Double> prop) {
		return getDefinition().getProperties().getOrDefault(prop);
	}

	public Integer getIntProperty(Property<Integer> prop) {
		return getDefinition().getProperties().getOrDefault(prop);
	}
	
	protected void exec(PythonCall pcall) {
		try {
			logger.trace(pcall.getMethodName());
			pcall.exec(getContext().getChartScope(), getDefinition().getProperties());
		} catch (Exception e) {
			logger.error("Error calling " + pcall.getMethodName(), e);
		}
	}

	
}
