/**
 *   (c) 2014 ILS Automation. All rights reserved.
 *  
 */
package com.ils.python.translation;



/**
 *  Record the allowable keys for G2 Procedure translation results.
 */
public interface TranslationConstants   {
	
	// Constants for the results dictionary
	public final static String PY_G2_PROC  		= "g2proc";    // The input procedure
	public final static String PY_IMPORTS  		= "imports";   // List of required imports
	public final static String PY_MODULE_CODE   = "pythonCode";    // Text of the module
	public final static String PY_PACKAGE       = "package";   // Package for Python modules
	public final static String PY_PRELIM        = "rawProc";   // Preliminary result
	public final static String PY_MODULE_NAME   = "pythonModule";   // Module name
	
	// Constants relating to errors
	public final static String ERR_LINE     = "line";      // line on which error occurred
	public final static String ERR_MESSAGE  = "line";      // text of error message
	public final static String ERR_POSITION = "position";  // position in line where error occurred
	public final static String ERR_TOKEN    = "token";     // token at which error occurred

}
