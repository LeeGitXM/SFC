/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.UUID;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import system.ils.sfc.common.Constants;

import com.ils.sfc.migration.Converter;
import com.ils.sfc.migration.DOMUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Given a G2 step document, create the corresponding Ignition version.
 */
public class StepTranslator {
	private final static String CLSS = "StepTranslator";
	private  final LoggerEx log = LogUtil.getLogger(StepTranslator.class.getPackage().getName());
	private final static boolean DEBUG = false;
	private final Converter delegate;

	public StepTranslator(Converter converter) {
		this.delegate = converter;
	}
	
	/**
	 * Create an ignition element corresponding to the specified
	 * G2 block. The g2block element has a single "block" child and,
	 * potentially, multiple "recipe" elements.
	 * @param chart
	 * @param block g2block corresponding to a step within the chart
	 * @return
	 */
	public Element translate(Document chart,Element block,int x,int y,String xml) {
		
		Element step = null;
		// Get attributes from the block element
		String name = makeName(block.getAttribute("name"));
		String uuid = canonicalForm(block.getAttribute("uuid"));
		String claz = block.getAttribute("class");
		String factoryId = "action-step";     // Generic action step as default
		boolean isEncapsulation = false;
		boolean isTransition = false;
		boolean isParallel = false;
		if( claz!=null && !claz.isEmpty() ) {
			String fid = delegate.getClassMapper().factoryIdForClass(claz);
			isEncapsulation= delegate.getClassMapper().isEncapsulation(claz);
			isTransition= delegate.getClassMapper().isTransition(claz);
			isParallel= delegate.getClassMapper().isParallel(claz);
			if( fid!=null) {
				factoryId = fid;
			}
			else {
				log.errorf("%s.translate: Error no SFC factoryID found for G2 class (%s)",CLSS,claz);
			}
		}
		if( isTransition ) {
			step = chart.createElement("transition");
			TransitionTranslator transTrans = new TransitionTranslator(block,delegate);
			transTrans.updateTransition(chart,step);
		}
		// Note: In the G2 export we find parallel blocks for both the start and end.
		//       In Ignition, the parallel element encapsulates its children
		//       During migration, the parallel element is created by the layout manager
		else if( isParallel ) {
			return null;
		}
		else {
			step = chart.createElement("step");
			step.setAttribute("name", name);
			step.setAttribute(Constants.FACTORY_ID, factoryId);
			// Encapsulation have several additional properties. The encapsulation reference
			// is to a chart name only. Ignition requires a path.
			if( isEncapsulation ) {
				String reference = block.getAttribute("full-path");
				if( reference.length()==0) reference = block.getAttribute("label");
				String chartPath = delegate.partialPathFromInfile(reference);
				
				step.setAttribute(Constants.CHART_PATH, chartPath);
				log.tracef("%s.translate: Encapsulation: %s translates to %s",CLSS,reference,chartPath);
				step.setAttribute("execution-mode", "RunUntilCompletion");   // versus RunUntilStopped
			}
			if( factoryId.equalsIgnoreCase("action-step") ) {
				delegate.insertOnStartFromG2Block(chart,step,block);
			}
			else {
				delegate.updateStepFromG2Block(chart,step,block);
			}
			// We may have created stop methods based on a downstream transition
			if( block.hasAttribute("stop-script")) {
				step.appendChild(DOMUtil.createChildElement(chart,step,"stop-script", block.getAttribute("stop-script")));
			}
			// Now add recipe data - feed the translator the entire "data" element
			Element recipe = makeRecipeDataElement(chart,step,block);
			if( recipe!=null) step.appendChild(recipe);
		}

		// Common to both steps and transitions
		step.setAttribute("id", uuid);
		step.setAttribute("location", String.format("%d %d", x,y));
		if( DEBUG || log.isTraceEnabled()) log.infof("%s.translate %s to %d,%d", CLSS,step.getAttribute("name"),x,y);
		return step;
	}
	
	// Escape XML so it can be embedded in xml as text
	private String escapeXml(String xml) {
		xml = xml.replace("&", "&amp;");
		xml = xml.replace("&amp;amp;", "&amp;");
		xml = xml.replace("<", "&lt;");
		xml = xml.replace(">", "&gt;");
		xml = xml.replace("\"", "&quot;");
		xml = xml.replace("'", "&apos;");
		return xml;
	}

	/**
	 * Remove dashes and spaces. Convert to camel-case.
	 * Attempt to shorten.
	 * 
	 * @param filename dash-delimited name from G2
	 * @return munged name
	 */
	public String makeName(String filename) {
		String name = filename;
		if( name==null || name.length()==0 ) name = "?";

		// Replace XXX with an underscore
		name = name.replace("-XXX-", "_");
		name = name.replace("PROCEDURE", "");

	    StringBuilder camelCase = new StringBuilder();
	    boolean nextTitleCase = true;
	    //log.tracef("toCamelCase: %s",input);
	    for (char c : name.toCharArray()) {
	    	if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	            continue;
	        } 
	        // remove illegal characters
	        else if (c=='-' ||
	        		 c=='#' ||
	        		 c=='/' ||
	        		 c==':' ||
	        		 c=='.'    ) {
	            nextTitleCase = true;
	            continue;
	        } 
	        else if (nextTitleCase) {
	            c = Character.toUpperCase(c);
	            nextTitleCase = false;
	        }
	        else {
	        	c = Character.toLowerCase(c);
	        }
	        camelCase.append(c);
	    }
	    //log.tracef("StepTranslator.toCamelCase: %s -> %s",input,camelCase.toString());
	    return camelCase.toString();
	}
	/** 
	 * Seems like this should be easier...
	 * @param g2Block a G2 export "block" element
	 * @return an xml element for associated data, containing the recipe data.
	 */
	private Element makeRecipeDataElement(Document chart,Element step,Element g2Block) {
		// The recipe data translator uses  a SAX parser, so stream the input
		Element recipe = null;
		try {
			RecipeDataTranslator rdTranslator = new RecipeDataTranslator(g2Block);
			String stepFactoryId = step.getAttribute("factory-id");
			Element associatedData = rdTranslator.createAssociatedDataElement(chart, stepFactoryId);
			if( associatedData!=null) step.appendChild(associatedData);

			// Some debug stuff for errors--might want to log it...
			for(String errMsg: rdTranslator.getErrors()) {
				log.errorf("%s.makeRecipeDataElement: Parse error in (%s)",CLSS,errMsg);
			}
		} 
		catch (JSONException je) {
			log.errorf("%s.makeRecipeDataElement: Exception creating JSON data (%s)",CLSS,je.getMessage());
		} 
		return recipe;
	}
	
	/**
	 * Format a G2 UUID string into the UUID canonical form. Basically
	 * this entails inserting dashes.
	 * 
	 * @param uuidin the incoming UUID proposal.
	 * @return
	 */
	public static String canonicalForm(String uuidin) {
		String uuid = uuidin;
		if( uuid.isEmpty() ) return UUID.randomUUID().toString();
		int len = uuid.length();
		if( len==32 ) {
			uuid = String.format("%s-%s-%s-%s-%s",uuid.substring(0,8),
										uuid.substring(8,12),uuid.substring(12,16),uuid.substring(16,20),
										uuid.substring(20,32));
		}
		else if( len==36 ) {
			// Format is good already
			;
		}
		else {
			throw new IllegalArgumentException(String.format("%s is not a legal or almost loegal UUID", uuidin));
		}
		return uuid;
	}
}
