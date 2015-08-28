/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
		boolean needsRestructure = false;
		// Search for blocks-to-morph-to-transitions
		int index = 0;
		int startIndex = 0;
		// First-time through create default entries in the grid map
		NodeList blocks = g2chart.getElementsByTagName("block");
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
		// Search for transitions with Callback strategies
		List<Element> transitions = new ArrayList<>();
		index = 0;
		while( index < blocks.getLength()) {
			Element block = (Element)blocks.item(index);
			String claz = block.getAttribute("class");
			if( claz.equalsIgnoreCase("S88-CONDITIONAL-TRANSITION")) {
				if( "callback".equalsIgnoreCase(block.getAttribute("strategy"))) {
					transitions.add(block);
				}
			}
			index++;
		}
		// Re-query with each loop as the nodes my be added
		for(Element transition:transitions) {
			blocks = g2chart.getElementsByTagName("block");
			addCallbackToUpstreamStep(transition,blocks);
		}
	}
	
	/** 
	 * The element will be a transition
	 * @param transition
	 * @param blocks current list of blocks
	 */
	private void addCallbackToUpstreamStep(Element transition,NodeList blocks) {
		
		// Search for the block(s) connected upstream of this transition. 
		// If this is a custom ILS block, then insert a generic action step 
		String uuid = transition.getAttribute("uuid");
		String procedureName = transition.getAttribute("callback");
		if( uuid==null || procedureName==null ) return;

		Element parent = findParent(transition,blocks);
		if( parent!=null) {
			// We can only add stop methods to an action-step
			// If the parent is one, then we're in good shape. 
			// --- else we need to create one and insert.
			Element actionBlock = null;
			if( parent.hasAttribute("class") && !parent.getAttribute("class").equalsIgnoreCase("action-step") ) {
				// Create an action step between the parent and the subject block, fix the connections.
				actionBlock = g2chart.createElement("block");
				//actionBlock.setAttribute("class", "action-step");  // Generic block does not have this attribute
				actionBlock.setAttribute("uuid", UUID.randomUUID().toString().replace("-", ""));
				actionBlock.setAttribute("label", "Transition Callback");
				actionBlock.setAttribute("name", "TRANSITION-CALLBACK-ACTION");
				log.infof("%s.addCallbackToUpstreamStep: Added action-block %s (%s)",TAG,actionBlock.getAttribute("name"),actionBlock.getAttribute("uuid"));
				g2chart.getDocumentElement().insertBefore(actionBlock, transition);
				// Move parent links to the new block
				// The new block takes on all the connections of the parent
				NodeList downstreamBlocks = parent.getElementsByTagName("connectedTo");
				Element downstreamNode = null;
				int count = downstreamBlocks.getLength();
				int k = 0;
				List<Element> elementsToMove = new ArrayList<Element>();
				while(k<count) {
					downstreamNode = (Element)downstreamBlocks.item(k);
					elementsToMove.add(downstreamNode);
					k++;
				}
				for(Element e:elementsToMove) {
					parent.removeChild(e);
					actionBlock.appendChild(e);
				}

				// Add a connection between the parent and the new action-step
				Element cxn = g2chart.createElement("connectedTo");
				cxn.setAttribute("uuid",  actionBlock.getAttribute("uuid"));
				cxn.setAttribute("label", actionBlock.getAttribute("label"));
				parent.appendChild(cxn);
			}
			else {
				actionBlock = parent;
			}
			// We make sure this procedure gets copied when converting to ignition
			insertOnStopIntoActionBlock(actionBlock,procedureName);
		}
		else {
			log.warnf("%s.addCallbackToUpdstream:could not find parent of transition %s. Transition expression will be invalid.", TAG,transition.getAttribute("name"));	
		}
	}
	
	/**
	 * Search for a node that has "connectedTo" links to the subject node.
	 * @param block
	 * @param blocks
	 * @return
	 */
	private Element findParent(Element block,NodeList blocks) {
		Element parent = null;
		int index = 0;
		int nodeCount = blocks.getLength();
		String targetUuid = block.getAttribute("uuid");
		//log.infof("%s.findParent: Looking for node of %d with connection to %s",TAG,nodeCount,targetUuid);
		while( index<nodeCount) {
			Element e = (Element)blocks.item(index);
			NodeList connections = e.getElementsByTagName("connectedTo");
			int connectionIndex = 0;
			int connectionCount = connections.getLength();
			while( connectionIndex<connectionCount) {
				Element connection = (Element)connections.item(connectionIndex);
				String uuid = connection.getAttribute("uuid");
				if( targetUuid.equals(uuid)) {
					log.infof("%s.findParent: Found %s as parent of %s out of %d blocks",TAG,e.getAttribute("uuid"),uuid,nodeCount);
					return e;
				}
				connectionIndex++;
			}
			index++;
		}
		return parent;
	}
	
	/**
	 * If the G2 block contains a "callback", then read its converted value from the 
	 * file system and insert as a step property.
	 * 
	 * @param step
	 * @param g2block
	 */
	public void insertOnStopIntoActionBlock(Element block,String script) {
		String path = delegate.pathNameForModule(delegate.toCamelCase(script));
		if( path!=null  ) {
			Path scriptPath = Paths.get(delegate.getPythonRoot().toString()+"/onstop",path);
			try {
				byte[] bytes = Files.readAllBytes(scriptPath);
				if( bytes!=null && bytes.length>0) {
					Element stopelement = g2chart.createElement("stop-script");
					Node textNode = g2chart.createTextNode(new String(bytes));
					stopelement.appendChild(textNode);
					block.appendChild(stopelement);
				}
				else {
					log.errorf("%s.insertOnStopIntoActionBlock: Empty file %s",TAG,scriptPath.toString());
				}

			}
			catch(IOException ioe) {
				log.errorf("%s.insertOnStopIntoActionBlock: Error reading script %s (%s)",TAG,scriptPath.toString(),ioe.getMessage());
			}
		}
	}
}
