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
	public final static String PY_DOC_STRING    = "docstring"; // "docstring"
	public final static String PY_G2_CODE  		= "g2code";    // The input procedure code
	public final static String PY_G2_PROC  		= "g2proc";    // The input procedure name
	public final static String PY_IMPORTS  		= "imports";   // List of required imports
	public final static String PY_MODULE_CODE   = "pythonCode";  // Text of the module (preliminary)
	public final static String PY_PACKAGE       = "package";   // Package for Python modules
	public final static String PY_MODULE_NAME   = "pythonModule";   // Module name
	
	// Constants relating to errors
	public final static String ERR_LINE     = "line";      // line on which error occurred
	public final static String ERR_MESSAGE  = "msg";       // text of error message
	public final static String ERR_POSITION = "position";  // position in line where error occurred
	public final static String ERR_TOKEN    = "token";     // token at which error occurred

}
