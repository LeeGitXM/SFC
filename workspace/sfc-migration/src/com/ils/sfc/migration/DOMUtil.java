/**
 *   (c) 2016  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *  This class contains a collection of convenience methods for working
 *  with XML documents and elements.
 *  
 */
public class DOMUtil  {

	/**
	 * Convenience method to return the string value of a named attribute within a
	 * child of the supplied method. There can be only one child with the supplied tag
	 * @param parent
	 * @param tagName
	 * @param attName
	 * @return
	 */
	public static String elementAttributeValue(Element parent,String tagName,String attName) {
		NodeList nodes = parent.getElementsByTagName(tagName);
		int count = nodes.getLength();
		if( count==0 ) return null;
		if( count>1) throw new IllegalArgumentException(
				String.format("DOMUtil.elementAttributeValue: there must be exactly one %s, %d found",tagName,count));
		
		// Exactly one
		Element e = (Element)nodes.item(0);
		return e.getAttribute(attName);
	}
	
	public static Element createChildElement(Document doc,Element parent,String tagName,String value) {
		NodeList nodes = parent.getElementsByTagName(tagName);
		int count = nodes.getLength();
		if( count>0) throw new IllegalArgumentException(
				String.format("DOMUtil.createChildElement: an element %s already exists",tagName));
		
		Element element = doc.createElement(tagName);
		Node textNode = doc.createTextNode(value);
		element.appendChild(textNode);
		parent.appendChild(element);
		return element;
	}
}
