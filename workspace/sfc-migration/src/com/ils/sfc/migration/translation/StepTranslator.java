/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ils.sfc.common.recipe.objects.RecipeDataTranslator;
import com.ils.sfc.migration.Converter;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Given a G2 step document, create the corresponding Ignition version.
 */
public class StepTranslator {
	private final static String TAG = "StepTranslator";
	private  final LoggerEx log = LogUtil.getLogger(StepTranslator.class.getPackage().getName());
	
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
	public Element translate(Document chart,Element block,int x,int y) {
		
		Element step = null;
		// Get attributes from the block element
		String name = makeName(block.getAttribute("name"));
		String uuid = canonicalForm(block.getAttribute("uuid"));
		String claz = block.getAttribute("class");
		String factoryId = "action-step";     // Generic action step as default
		boolean isEncapsulation = false;
		boolean isTransition = false;
		if( claz!=null ) {
			String fid = delegate.getClassMapper().factoryIdForClass(claz);
			isEncapsulation= delegate.getClassMapper().isEncapsulation(claz);
			isTransition= delegate.getClassMapper().isTransition(claz);
			if( fid!=null) {
				factoryId = fid;
			}
			else {
				log.errorf("%s.translate: Error no SFC factoryID found for G2 class (%s)",TAG,claz);
			}
		}
		if( isTransition ) {
			step = chart.createElement("transition");
			String expression = TransitionTranslator.createTransitionExpression(block);
			Node textNode = chart.createTextNode(expression);
			step.appendChild(textNode);
		}
		else {
			step = chart.createElement("step");
			step.setAttribute("name", name);
			// Encapsulation have several additional properties. The encapsulation reference
			// is to a chart name only. Ignition requires a path.
			if( isEncapsulation ) {
				String reference = block.getAttribute("block-full-path-label");
				if( reference.length()==0) reference = block.getAttribute("label");
				String convertedReference = delegate.toCamelCase(reference);
				String chartPath = delegate.getPathForChart(convertedReference);
				step.setAttribute("chart-path", chartPath);
				log.infof("%s.translate: Encapsulation: %s translates to %s",TAG,convertedReference,chartPath);
				step.setAttribute("execution-mode", "RunUntilStopped");
			}
			if( factoryId.equalsIgnoreCase("action-step") ) {
				delegate.insertOnStartFromG2Block(chart,step,block);
			}
			else {
				delegate.updateStepFromG2Block(chart,step,block);
			}
			// Now add recipe data - feed the translator the entire "data" element
			Element recipe = makeRecipeDataElement(chart,step,block);
			if( recipe!=null) step.appendChild(recipe);
		}

		// Common to both steps and transitions
		step.setAttribute("id", uuid);
		step.setAttribute("factory-id", factoryId);
		step.setAttribute("location", String.format("%d %d", x,y));
		return step;
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
	    log.tracef("toCamelCase: result %s",camelCase.toString());
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

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(g2Block);
		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			InputStream xmlIn = new ByteArrayInputStream(outputStream.toByteArray());
			RecipeDataTranslator rdTranslator = new RecipeDataTranslator(xmlIn);
			Element associatedData = rdTranslator.createAssociatedDataElement(chart);
			if( associatedData!=null) step.appendChild(associatedData);

			// Some debug stuff for errors--might want to log it...
			for(String errMsg: rdTranslator.getErrors()) {
				log.errorf("%s.makeRecipeDataElement: Parse error (%s)",TAG,errMsg);
			}

		} 
		catch (TransformerException | TransformerFactoryConfigurationError tf) {
			log.errorf("%s.makeRecipeDataElement: Exception transforming G2 block element (%s)",TAG,tf.getMessage());
		} 
		catch (JSONException je) {
			log.errorf("%s.makeRecipeDataElement: Exception creating JSON data (%s)",TAG,je.getMessage());
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
