/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

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
	 * @param g2block
	 * @return
	 */
	public Element translate(Document chart,Element g2block,int x,int y) {
		Element step = chart.createElement("step");

		step.setAttribute("location", String.format("%d %d", x,y));
		
		if( g2block.getElementsByTagName("block").getLength()==0) {
			log.errorf("%s.translate: g2block has no \"block\" element",TAG);
			return step;
		}
		Element block = (Element)(g2block.getElementsByTagName("block").item(0));
		// Get attributes from the block element
		String name = makeName(block.getAttribute("name"));
		String uuid = block.getAttribute("uuid");
		if( uuid==null ) uuid = UUID.randomUUID().toString();
		String claz = block.getAttribute("class");
		String factoryId = "action-step";     // Generic action step as default
		boolean isEnclosure = false;
		if( claz!=null ) {
			String fid = delegate.getClassMapper().factoryIdForClass(claz);
			isEnclosure= delegate.getClassMapper().isClassAnEnclosure(claz);
			if( fid!=null) {
				factoryId = fid;
			}
			else {
				log.errorf("%s.translate: Error no SFC factoryID found for G2 class (%s)",TAG,claz);
			}
		}
		// Enclosures have several additional properties
		if( isEnclosure ) {
			String reference = block.getAttribute("label");
			String filename = delegate.toCamelCase(reference);
			step.setAttribute("chart-path", delegate.getPathForFile(filename));
			step.setAttribute("execution-mode", "RunUntilStopped");
		}
	
		step.setAttribute("name", name);
		step.setAttribute("id", uuid);
		step.setAttribute("factory-id", factoryId);
		
		// Now add recipe data - feed the translator the entire "data" element
		Element recipe = makeRecipeDataElement(g2block);
		if( recipe!=null) step.appendChild(recipe);
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
	 * @return an xml element for associated data, containing the recipe data.
	 */
	private Element makeRecipeDataElement(Element g2Block) {
		// The recipe data translator uses  a SAX parser, so stream the input
		Element recipe = null;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(g2Block);
		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			InputStream xmlIn = new ByteArrayInputStream(outputStream.toByteArray());
			RecipeDataTranslator rdTranslator = new RecipeDataTranslator(xmlIn);
			String rdElement = rdTranslator.translate();
			if(rdElement != null) {
				InputStream sbis = new ByteArrayInputStream(rdElement.getBytes());
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    Document doc = builder.parse(sbis);
			    recipe = doc.getDocumentElement();
			}
			else {
				// Some debug stuff for errors--might want to log it...
				for(String errMsg: rdTranslator.getErrors()) {
					log.errorf("%s.makeRecipeDataElement: Parse error (%s)",TAG,errMsg);
				}
			}
		} 
		catch (TransformerException | TransformerFactoryConfigurationError tf) {
			log.errorf("%s.makeRecipeDataElement: Exception transforming G2 block element (%s)",TAG,tf.getMessage());
		} 
		catch (JSONException je) {
			log.errorf("%s.makeRecipeDataElement: Exception creating JSON data (%s)",TAG,je.getMessage());
		} 
		catch (SAXException saxe) {
			log.errorf("%s.makeRecipeDataElement: Exception parsing JSON data (%s)",TAG,saxe.getMessage());
		} 
		catch (IOException ioe) {
			log.errorf("%s.makeRecipeDataElement: Exception writing JSON element (%s)",TAG,ioe.getMessage());
		} 
		catch (ParserConfigurationException pce) {
			log.errorf("%s.makeRecipeDataElement: Exception creating recipe data parser (%s)",TAG,pce.getMessage());
		}
		return recipe;
	}
}
