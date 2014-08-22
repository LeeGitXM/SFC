/**
 *   (c) 2014 ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.common;



/**
 *  Define an interface for accessing module properties .
 */
public interface IlsSfcProperties   {   
	public final static String MODULE_ID = "ilssfc";     // See module-icc2.xml
	public final static String MODULE_NAME = "ILS-SFC";  // See build-icc2.xml
	
	/** This is the name of the jar file containing block class definitions */
	public final static String STEP_JAR_NAME = "ils-sfc-steps";
	
	
	// This is the common prefix under which bundle files are identified/registered
	public static final String BUNDLE_PREFIX = "ilsstep";

}
