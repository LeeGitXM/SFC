package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.step.annotation.ILSStep;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.sfc.api.ChartContext;
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
	
	protected IlsAbstractChartStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
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
