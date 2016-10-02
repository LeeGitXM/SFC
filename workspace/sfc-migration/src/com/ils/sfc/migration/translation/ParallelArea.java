/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import org.w3c.dom.Element;

/**
 *  This class is a representation of a parallel area
 *  in an SFC chart. The element holds, as children,
 *  all steps and connections to be executed in parallel.
 *  
 *  The element location is the upper left corner.
 */
public class ParallelArea  {
	public int x1 = 0;      // min x
	public int x2 = 0;      // min x
	public int y1 = 0;      // min x
	public int y2 = 0;      // max y
	// Keep track of the rightmost position of the current lane
	public int rightmost = Integer.MIN_VALUE;
	private final Element element;
	
	/**
	 * Create a new parallel area.
	 */
	public ParallelArea(Element e) {
		this.element = e;
	}
	public Element getElement() { return this.element; }
	
	/**
	 * This is a debugging aid.
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append(x1);
		str.append(":");
		str.append(y1);
		str.append(",");
		str.append(x2);
		str.append(":");
		str.append(y2);
		return str.toString();
	}
}
