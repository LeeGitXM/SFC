/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.util;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 *  This is a utility class of, hopefully, generally useful data conversion
 *  functions. This class carries no state.
 */
public class UtilityFunctions  {
	private final static String TAG = "UtilityFunctions: ";
	private final LoggerEx log;
	/**
	 * No-argument constructor. 
	 */
	public UtilityFunctions() {
		log = LogUtil.getLogger(getClass().getPackage().getName());
	}
	
	
	/**
	 * Safely parse a double. Catch and report a format exception.
	 * @return the double equivalent of the input. If the input
	 *         could not be parsed then return Double.NaN. 
	 */
	public double parseDouble(String val) {
		double result = Double.NaN;
		try{
			result = Double.parseDouble(val);
		}
		catch(NumberFormatException nfe) {
			log.error(TAG+"parseDouble: Format exception "+nfe.getLocalizedMessage(),nfe);    // Prints stack trace
		}
		return result;
	}
	
	/**
	 * Safely parse an int. Catch and report a format exception.
	 * @return  the integer equivalent of the input. If the input
	 *         could not be parsed then return zero.
	 */
	public int parseInteger(String val) {
		int result = 0;
		try{
			result = Integer.parseInt(val);
		}
		catch(NumberFormatException nfe) {
			double dbl = parseDouble(val);
			if( !Double.isNaN(dbl)) result = (int)dbl;
		}
		return result;
	}
	/**
	 * Force a Double, Integer or String to a boolean.
	 */
	public boolean coerceToBoolean(Object val) {
		boolean result = false;
		if( val!=null ) {
			if( val instanceof Boolean)      result = ((Boolean)val).booleanValue();
			else if( val instanceof Double)  result = (((Double)val).doubleValue()!=0.0);
			else if( val instanceof Integer) result = (((Integer)val).intValue() != 0);
			else                             result = val.toString().equalsIgnoreCase("true");
		}
		return result;
	}
	
	/**
	 * Force a Double, Integer or String to a double. Throws NumberFormatException
	 * for bad input - and sets result to 0.0.
	 */
	public double coerceToDouble(Object val) {
		double result = 0.0;
		if( val!=null ) {
			if( val instanceof Double)       result = ((Double)val).doubleValue();
			else if( val instanceof Integer) result = ((Integer)val).intValue();
			else                             result  = parseDouble(val.toString());	
		}
		return result;
	}
	
	/**
	 * Force a Double, Integer or String to an int. Throws NumberFormatException
	 * for bad input - and sets result to 0.
	 */
	public int coerceToInteger(Object val) {
		int result = 0;
		if( val !=null ) {
			if( val instanceof Integer)      result = ((Integer)val).intValue();
			else if( val instanceof Double)  result = ((Double)val).intValue();
			else                             result = parseInteger(val.toString());
		}
		return result;
	}
	/**
	 * Force a Double, Integer or String to a String. 
	 * Guarantee the return is not null. 
	 */
	public String coerceToString(Object val) {
		String result = "";
		if( val!=null ) result = val.toString();
		return result;
	}
}
