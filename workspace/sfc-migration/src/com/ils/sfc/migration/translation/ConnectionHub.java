/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
/**
 * Used to keep track of step connectivity. 
 * Note: any given step can be enclosed by at most one parallel structure.
 */
public class ConnectionHub {
	private final List<String> connectedTo;
	private final List<String> connectedFrom;
	private final Element parent;
	/**
	 * The root is either a document (chart) root or a  parallel element
	 * @param root
	 */
	public ConnectionHub(Element root) {
		this.parent = root;
		this.connectedTo = new ArrayList<>();
		this.connectedFrom = new ArrayList<>();
	}
	public Element getParent() { return this.parent; }
	public void addConnectionTo(String uuid) { connectedTo.add(uuid); }
	public void addConnectionFrom(String uuid) { connectedFrom.add(uuid); }
	public List<String> getConnectionsTo() { return connectedTo; }
	public List<String> getConnectionsFrom() { return connectedFrom; }
}


