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
	protected boolean isParallel = false;
	protected ParallelArea  parallelArea = null;
	protected int visitCount = 0;
	private Element parent;
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
	public ParallelArea getParallelArea() { return this.parallelArea; }
	public void setParallelArea(ParallelArea pa) { this.parallelArea=pa; }
	public boolean isInParallelZone() { return (parallelArea!=null); }
	public boolean isParallelBlock() { return this.isParallel; }
	public int getVisitCount()  { return this.visitCount; }
	public void incrementVisitCount()  { this.visitCount++; }
	public void setParallelBlock(boolean flag) { this.isParallel=flag; }
	public void setForChart(Element e) {
		this.parent = e; 
	}
	// Use setForChart to counter the effect
	public void setForParallel(ParallelArea pa) { 
		this.parallelArea=pa;
		this.parent = pa.getElement();
		this.isParallel = true;
	}
	// Use when inheriting from previous block
	public void setParentage(ConnectionHub hub) { 
		this.parent = hub.getParent();
		this.parallelArea = hub.parallelArea;
	}
}


