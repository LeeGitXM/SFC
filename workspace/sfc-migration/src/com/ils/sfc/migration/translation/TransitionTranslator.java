/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import org.w3c.dom.Element;
/**
 * Given a G2 transition step, create the Ignition expression
 */
public class TransitionTranslator {
	private final static String TAG = "TransitionTranslator";


	
	/** 
	 * @param g2Block a G2 export "block" (transition) element
	 * @return an Ignition expression suitable for a transition
	 */
	public static String createTransitionExpression(Element g2Block) {
		String expression = "true";
		return expression;
	}
}
