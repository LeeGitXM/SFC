/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.ArrayList;
import java.util.List;
/**
 * Used to keep track of step connectivity.
 */
public class ConnectionHub {
	private final List<String> connectedTo;
	private final List<String> connectedFrom;
	public ConnectionHub() {
		this.connectedTo = new ArrayList<>();
		this.connectedFrom = new ArrayList<>();
	}

	public void addConnectionTo(String uuid) { connectedTo.add(uuid); }
	public void addConnectionFrom(String uuid) { connectedFrom.add(uuid); }
	public List<String> getConnectionsTo() { return connectedTo; }
	public List<String> getConnectionsFrom() { return connectedFrom; }
}
