/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.common.block;

import java.util.UUID;

import com.ils.common.connection.Connection;
import com.ils.common.connection.ConnectionType;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;



/**
 * This is the basic connection class. It handles all methods required by the interface.
 * Make the class comparable.
 */
public class ProcessConnection implements Connection {
	protected final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	protected UUID source = null;
	protected UUID target = null;
	protected final ConnectionType type;
	protected String upstreamPort = null;
	protected String downstreamPort = null;
	protected QualifiedValue value = null;
	protected long timestamp;
	
	/**
	 * Constructor: Specify the upstream and downstream ports later as they are editable.
	 * @param t connection type
	 */
	public ProcessConnection(ConnectionType t) {
		this.type = t;
	}
	
	/**
	 * @return a the type of this connection, the data type.
	 */
	public ConnectionType getType() { return type; }
	
	/**
	 * @return the name of output port on the upstream block to which we are connected.
	 */
	public String getUpstreamPortName() { return upstreamPort; }
	/**
	 * @param the name of output port on the upstream block to which we are connected.
	 */
	public void setUpstreamPortName(String port) { this.upstreamPort = port; }
	/**
	 * @return the name of input port on the downstream block to which we are connected.
	 */
	public String getDownstreamPortName() { return downstreamPort; }
	/**
	 * @param the name of input port on the downstream block to which we are connected
	 */
	public void setDownstreamPortName(String port) { this.downstreamPort = port; }
	/**
	 * @return the latest value placed on this connection
	 */
	public QualifiedValue getValue() { return this.value; }
	/**
	 * @param val the current value on this connection
	 */
	public void setValue(QualifiedValue val) { this.value = val; }
	

	@Override
	public UUID getSource() { return source; }

	@Override
	public void setSource(UUID id) { this.source = id; }

	@Override
	public UUID getTarget() { return this.target; }

	@Override
	public void setTarget(UUID id) { this.target = id; }
	
	// So that class may be used in lists, but prevent duplicates.
	// Same source, target and ports are necessary to prove equality
	@Override
	public boolean equals(Object arg) {
		boolean result = false;
		if( arg instanceof ProcessConnection) {
			ProcessConnection that = (ProcessConnection)arg;
			if( this.getSource().equals(that.getSource() ) &&
				this.getTarget().equals(that.getTarget() ) &&
				this.upstreamPort.equals(that.getUpstreamPortName()) &&
				this.downstreamPort.equals(that.getDownstreamPortName())) {
				result = true;
			}
		}
		return result;
	}
	@Override
	public int hashCode() {
		return this.getSource().hashCode()-this.getTarget().hashCode()+this.downstreamPort.hashCode()-this.upstreamPort.hashCode();
	}
}