/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.step;

import java.util.UUID;

import com.ils.sfc.common.CustomStep;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 * This abstract class is the basis of all custom steps. 
 *  
 * The subclasses depend on the "ILSStep" class annotation
 * as the marker to group a particular subclass into the list of 
 * available executable block types.
 */
public abstract class AbstractStep implements CustomStep {
	
	protected final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	

	
	/**
	 * Constructor: The no-arg constructor is used when creating a prototype for use in the palette.
	 *              It does not correspond to a functioning block.
	 */
	public AbstractStep() {
		initialize();
		initializePrototype();
	}
	
	/**
	 * Constructor:
	 * @param stepId universally unique Id for the step
	 */
	public AbstractStep(UUID stepId) {
		this();
	}
	
	/**
	 * Create an initial list of properties. There is none for the base class.
	 */
	private void initialize() {
	}
	
	/**
	 * Fill a prototype object with defaults - as much as is reasonable.
	 */
	private void initializePrototype() {

	}

}