/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ils.sfc.migration.Converter;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;

/**
 * Given a G2 transition step, create the Ignition expression
 */
public class ChartStructureTranslator {
	private final static String TAG = "ChartStructureTranslator";
	private  final LoggerEx log = LogUtil.getLogger(StepTranslator.class.getPackage().getName());
	private final Document g2chart;
	private final Converter delegate;

	public ChartStructureTranslator(Document doc,Converter converter) {
		this.g2chart = doc;
		this.delegate  = converter;
	}

	/** 
	 * Test the subject document for several structural patterns 
	 * that require some macro-manipulation before the layout.
	 */
	public void refactor() {
		/** 
		 * CASE I: Chart has multiple transitions without upstream connections. 
		 *         Add a transition downstream of the start block, and connect the
		 *         others to the original as well.
		 */
		NodeList blocks = g2chart.getElementsByTagName("block");
		boolean needsRestructure = false;
		// Search for blocks-to-morph-to-transitions
		int index = 0;
		int startIndex = 0;
		// First-time through create default entries in the grid map
		while( index < blocks.getLength()) {
			Element block = (Element)blocks.item(index);
			String claz = block.getAttribute("class");
			if( claz.equalsIgnoreCase("S88-ABORT-BLOCK")) needsRestructure = true;
			else if( claz.equalsIgnoreCase("S88-BEGIN")) startIndex = index;
			else if( claz.equalsIgnoreCase("S88-STOP-BLOCK")) needsRestructure = true;
			else if( claz.equalsIgnoreCase("S88-TIMEOUT-BLOCK")) needsRestructure = true;
			index++;
		}
		if( needsRestructure ) {
			Element oldStartBlock = (Element)blocks.item(startIndex);
			oldStartBlock.setAttribute("class", "S88-CONDITIONAL-TRANSITION");
			oldStartBlock.setAttribute("label", "defaultTransition");
			oldStartBlock.setAttribute("strategy", "default");
			Element newStartBlock = g2chart.createElement("block");
			newStartBlock.setAttribute("class", "S88-BEGIN");
			newStartBlock.setAttribute("uuid", UUID.randomUUID().toString().replace("-", ""));
			newStartBlock.setAttribute("label", "begin");
			newStartBlock.setAttribute("name", "S88-BEGIN-XXX-0000");
			Element cxn = g2chart.createElement("connectedTo");
			cxn.setAttribute("uuid", oldStartBlock.getAttribute("uuid"));
			cxn.setAttribute("label", oldStartBlock.getAttribute("label"));
			newStartBlock.appendChild(cxn);
			// Now connect to the other transitions
			index = 0;
			while( index < blocks.getLength()) {
				Element block = (Element)blocks.item(index);
				String claz = block.getAttribute("class");
				if( claz.equalsIgnoreCase("S88-ABORT-BLOCK")) {
					block.setAttribute("strategy", "abort");
					cxn = g2chart.createElement("connectedTo");
					cxn.setAttribute("uuid", block.getAttribute("uuid"));
					cxn.setAttribute("label", block.getAttribute("label"));
					newStartBlock.appendChild(cxn);
				}
				else if( claz.equalsIgnoreCase("S88-STOP-BLOCK")) {
					block.setAttribute("strategy", "stop");
					cxn = g2chart.createElement("connectedTo");
					cxn.setAttribute("uuid", block.getAttribute("uuid"));
					cxn.setAttribute("label", block.getAttribute("label"));
					newStartBlock.appendChild(cxn);
				}
				else if( claz.equalsIgnoreCase("S88-TIMEOUT-BLOCK")){
					block.setAttribute("strategy", "timeout");
					cxn = g2chart.createElement("connectedTo");
					cxn.setAttribute("uuid", block.getAttribute("uuid"));
					cxn.setAttribute("label", block.getAttribute("label"));
					newStartBlock.appendChild(cxn);
				}
				index++;
			}
			// The document is just a list of nodes. It does not reflect their relationships.
			oldStartBlock.getParentNode().insertBefore(newStartBlock, oldStartBlock);
		}
		/** 
		 * CASE II: Chart has transition(s) that use the "callback" strategy
		 *          Take the value of the callback and define it as the onstop
		 *          method of the previous block. 
		 */
		index = 0;
		// Search for transitions with Callback strategies
		while( index < blocks.getLength()) {
			Element block = (Element)blocks.item(index);
			String claz = block.getAttribute("class");
			if( claz.equalsIgnoreCase("S88-CONDITIONAL-TRANSITION")) {
				if( "callback".equalsIgnoreCase(block.getAttribute("strategy"))) {
					addCallbackToUpstreamBlock(block,blocks);
				}
			}
			index++;
		}
	}
	
	private void addCallbackToUpstreamBlock(Element block,NodeList blocks) {
		// Search for the block(s) connected to this transition.
		String uuid = block.getAttribute("uuid");
		String procedureName = block.getAttribute("callback");
		if( uuid==null || procedureName==null ) return;
		int index = 0;
		while( index < blocks.getLength())  {
			Element parent = (Element)blocks.item(index);
			NodeList connections = parent.getElementsByTagName("connectedTo");
			int jndex = 0;
			while(jndex<connections.getLength()) {
				Element connection = (Element)connections.item(jndex);
				if( uuid.equalsIgnoreCase(connection.getAttribute("uuid"))) {
					// We make sure this gets copied when converting to ignition
					insertOnStopIntoG2Block(block,procedureName);
				}
				jndex++;
			}
			index++;
		}
		
	}
	/**
	 * If the G2 block contains a "callback", then read its converted value from the 
	 * file system and insert as a step property.
	 * 
	 * @param step
	 * @param g2block
	 */
	public void insertOnStopIntoG2Block(Element block,String script) {
		String path = delegate.pathNameForModule(script);
		if( script!=null  ) {
			Path scriptPath = Paths.get(delegate.getPythonRoot().toString()+"/onstop",script);
			try {
				byte[] bytes = Files.readAllBytes(scriptPath);
				if( bytes!=null && bytes.length>0) {
					Element stopelement = g2chart.createElement("stop-script");
					Node textNode = g2chart.createTextNode(new String(bytes));
					stopelement.appendChild(textNode);
					block.appendChild(stopelement);
				}
				else {
					log.errorf("%s.insertOnStopFromG2Block: Empty file %s",TAG,scriptPath.toString());
				}

			}
			catch(IOException ioe) {
				log.errorf("%s.insertOnStopFromG2Block: Error reading script %s (%s)",TAG,scriptPath.toString(),ioe.getMessage());
			}
		}
	}
}
