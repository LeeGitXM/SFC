/**
 *   (c) 2013  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 *  This class contains a collection of convenience methods for extracting
 *  information from XML documents and elements.
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
}
