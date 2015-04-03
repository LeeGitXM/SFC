/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ils.sfc.common.IlsSfcNames;
import com.ils.sfc.common.recipe.objects.RecipeDataTranslator;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Given a G2 transition step, create the Ignition expression
 */
public class TransitionTranslator {
	private final static String TAG = "TransitionTranslator";
	private  final LoggerEx log = LogUtil.getLogger(StepTranslator.class.getPackage().getName());
	private final Element g2Block;

	public TransitionTranslator(Element block) {
		this.g2Block = block;
	}

	/** 
	 * Update Ignition expression suitable for a transition
	 * @param doc
	 * @param transition
	 */
	public void updateTransition(Document doc, Element transition) {
		String expression = "true";
		// Get the attributes - complain about unhandled combinations
		String strategy    = g2Block.getAttribute("strategy");
		String targetStrategy = g2Block.getAttribute("target-strategy");
		String recipeLocation = g2Block.getAttribute("recipe-location");
		String callback    = g2Block.getAttribute("callback");
		String item        = g2Block.getAttribute("identifier-or-name");
		String constant    = g2Block.getAttribute("target-value");
		String operator    = g2Block.getAttribute("operator");
		if( strategy.equalsIgnoreCase("RECIPE-DATA") && targetStrategy.equalsIgnoreCase("CONSTANT-VALUE")) {
			expression = handleConstantValue(recipeLocation,item,constant,operator);
		}
		else {
			log.warnf("%s.updateTransition: Unrecognized strategy combination (%s/%s)",TAG,strategy,targetStrategy);
		}
		
		Node textNode = doc.createTextNode(expression);
		transition.appendChild(textNode);
		
		// Check for timer
		String name    = g2Block.getAttribute("name");
		String timeval = g2Block.getAttribute("timeout-period");
		String tu      = g2Block.getAttribute("timeout-units");
		if( timeval!=null && tu!=null ) {
			try {
				long timemillisecs = 0;
				double timeout = Double.parseDouble(timeval);
				if( tu.equalsIgnoreCase("HR")) {
					timemillisecs = (long)timeout*3600*1000;
					transition.setAttribute("timeout-enabled","true");
					transition.setAttribute("timeout-flag",name);
					transition.setAttribute("timeout-delay",String.valueOf(timemillisecs));
				}
				else if( tu.equalsIgnoreCase("MIN")) {
					timemillisecs = (long)timeout*60*1000;
					transition.setAttribute("timeout-enabled","true");
					transition.setAttribute("timeout-flag",name);
					transition.setAttribute("timeout-delay",String.valueOf(timemillisecs));
				}
				else if( tu.equalsIgnoreCase("SEC")) {
					timemillisecs = (long)timeout*1000;
					transition.setAttribute("timeout-enabled","true");
					transition.setAttribute("timeout-flag",name);
					transition.setAttribute("timeout-delay",String.valueOf(timemillisecs));
				}
				else {
					log.warnf("%s.updateTransition: Unrecognized time unit (%s)",TAG,tu);
				}
			}
			catch(NumberFormatException nfe ) {
				log.warnf("%s.updateTransition: Imported time value (%s) not numeric (%s)",TAG,timeval);
			}
		}
		
	}
	
	private String handleConstantValue(String recipeLocation,String item,String constant,String operator) {
		String ans = "true";
		if(recipeLocation!=null && item!=null && constant!=null ) {
			int pos = item.lastIndexOf(".");
			String baseLoc = "";
			String element = item;
			if( pos > 0 && pos < item.length()-1 ) {
				baseLoc = item.substring(0, pos+1);
				element = item.substring(pos+1);
				String modified = RecipeDataTranslator.g2ToIgName.get(element.toLowerCase());
				if( modified!=null) element = modified;
				else {
					log.warnf("%s.handleConstantValue: Unrecognized recipe item (%s)",TAG,element);
				}
			}
			ans = String.format("{ %s.%s%s } %s %s", 
					 recipeLocation.toLowerCase(),
					 baseLoc.toLowerCase(),
					 element,
					 convertOperator(operator),
					 constant
					 );
		}
		return ans;
	}
	
	/**
	 * Convert G2 versions of operators to Ignition-recognized operators
	 * @param op
	 * @return
	 */
	private String convertOperator(String op) {
		String operator = "=";
		if( op!=null ) {
			if( op.equalsIgnoreCase("EQUAL")) operator = "=";
			else if( op.equalsIgnoreCase("LESS-THAN")) operator = "<";
			else if( op.equalsIgnoreCase("GREATER-THAN")) operator = ">";
			else if( op.equalsIgnoreCase("LESS-THAN-OR_EQUAL")) operator = "<=";
			else if( op.equalsIgnoreCase("GREATER-THAN-OR-EQUAL")) operator = ">=";
			else {
				log.warnf("%s.convertOperator: Unrecognized numeric operator (%s)",TAG,op);
			}
		}
		return operator;
	}
}
