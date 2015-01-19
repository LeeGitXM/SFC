package com.ils.sfc.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ils.sfc.step.annotation.ILSStep;
import com.ils.sfc.util.IlsSfcNames;
import com.ils.sfc.util.PythonCall;
import com.inductiveautomation.ignition.common.config.BasicProperty;
import com.inductiveautomation.ignition.common.config.PropertyValue;
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
	private static final BasicProperty<String> nameProperty = new BasicProperty<String>(IlsSfcNames.NAME, String.class);
	private String auditLevel = IlsSfcNames.OFF;
	private long startTime;
	
	protected IlsAbstractChartStep(ChartContext context, StepDefinition definition) {
		super(context, definition);
	}

	private void setAuditLevel() {
		for(PropertyValue<?> propertyValue: getDefinition().getProperties()) {
			String propName = propertyValue.getProperty().getName();
			if(IlsSfcNames.AUDIT_LEVEL.equals(propName)) {
				auditLevel = (String) propertyValue.getValue();
			}
		}
	}
	
	public String getName() {
		return getName(getDefinition());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getName(); 
	}
	
	private boolean auditOn() {
		return !auditLevel.equals(IlsSfcNames.OFF);
	}
            
	@Override
	public void activateStep() {
		setAuditLevel();
		startTime = System.currentTimeMillis();
		if(auditOn()) {
			logger.debug(toString() + " activated");
		}
	}

	@Override
	public void deactivateStep() {
		long elapsedMillis = System.currentTimeMillis() - startTime;
		if(auditOn()) {
			logger.debug(toString() + " deactivated; elapsed time " + (elapsedMillis/1000.) + " sec ");
		}
	}

	@Override
	public void pauseStep() {
		if(auditOn()) {
			logger.debug(toString() + " paused");
		}
	}

	@Override
	public void resumeStep() {
		if(auditOn()) {
			logger.debug(toString() + " resumed");
		}
	}

	protected void exec(PythonCall pcall) {
		try {
			logger.trace(pcall.getMethodName());
			//indexElements(getChartContext());
			pcall.exec(getChartContext().getChartScope(), getDefinition().getProperties());
		} catch (Exception e) {
			logger.error("Error calling " + pcall.getMethodName(), e);
		}
	}

	/** Lazily initialize chart properties with things we need, like local scopes
	 *  and predecessors
	private void indexElements(ChartContext context) {
		PyChartScope chartScope = context.getChartScope();
		if(chartScope.get(IlsSfcNames.BY_NAME) != null) return;
		
		PyDictionary byName = new PyDictionary();
		
		List<ChartElement<?>> namedStepElements = new ArrayList<ChartElement<?>>();
		for(ChartElement<?> element: context.getElements()) {
			if(element.getDefinition() instanceof StepDefinition) {
				StepDefinition definition = (StepDefinition) element.getDefinition();
				String name = getName(definition);
				// Find all the named steps in the chart, and index their properties by
				// their name
				if(name != null) {
					namedStepElements.add(element);
					PyDictionary stepProperties = new PyDictionary();
					for(PropertyValue<?> propertyValue: definition.getProperties()) {
						String propName = propertyValue.getProperty().getName();
						if(!IlsProperty.ignoreProperties.contains(propName)) {
							stepProperties.put(propName,propertyValue.getValue());
						}
					}
					//System.out.println("indexing " + name + ": " + stepProperties);
					byName.put(name, stepProperties);
				}
			}
		}
	
		// wire the predecessors
		for(ChartElement<?> element: namedStepElements) {
			StepDefinition definition = (StepDefinition) element.getDefinition();
			String predecessorName = getName(definition);
			setPredecessor(element, byName, predecessorName);
		}
		
		// try to be pseudo-transactional by putting everything in at once:
		chartScope.put(IlsSfcNames.BY_NAME, byName);
		if(context.getChartScope().get(IlsSfcNames.BY_NAME) == null ) {
			logger.error("update of step properties in chart scope failed");
		}
	}
*/
	/** Get the name of a step from its definition */
	private String getName(StepDefinition definition) {
		return (String)definition.getProperties().getOrDefault(nameProperty);
	}
	
	/** Set each step's closest _step_ predecessor (i.e. not a transition etc) 
	 *  in its local scope. If there is more than one such predecessor, one is 
	 *  chosen at random.  
	private void setPredecessor(ChartElement<?> element, PyDictionary byName, String predecessorName) {
		for(Object obj: element.getNextElements()) {
			if(obj instanceof ChartElement) {
				ChartElement<?> successor = (ChartElement<?>)obj;
				if(successor.getDefinition() instanceof StepDefinition) {
					String successorName = getName((StepDefinition)successor.getDefinition());
					PyDictionary successorProperties = (PyDictionary)byName.get(successorName);
					successorProperties.put(IlsSfcNames.PREVIOUS, predecessorName);
					//System.out.println("indexing predecessor " + predecessorProperties + ": successor " + successorName);
				}
				else {  // not a step; recurse
					setPredecessor(successor, byName, predecessorName);
				}
			}
		}		
	}

*/	
}
