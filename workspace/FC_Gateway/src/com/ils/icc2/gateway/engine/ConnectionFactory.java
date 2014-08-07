/**
 *   (c) 2012  ILS Automation. All rights reserved.
 *  
 *   The block controller is designed to be called from the client
 *   via RPC. All methods must be thread safe,
 */
package com.ils.icc2.gateway.engine;

import com.ils.common.block.ProcessConnection;
import com.ils.icc2.common.serializable.SerializableConnection;



/**
 *  The connection factory creates a concrete process connection from a serializable version.
 *  For the moment there is very little difference between the classes. Nevertheless
 *  the separation keeps the serializable version behavior-free.
 */
public class ConnectionFactory  {
	private static ConnectionFactory instance = null;
	/**
	 * Private per the Singleton pattern.
	 */
	private ConnectionFactory() {
		
	}

	/**
	 * Static method to create and/or fetch the single instance.
	 */
	public static ConnectionFactory getInstance() {
		if( instance==null) {
			synchronized(ConnectionFactory.class) {
				instance = new ConnectionFactory();
			}
		}
		return instance;
	}
	
	/**
	 * Create a concrete instance of a Process block represented by the serializable block.
	 * @param sc
	 * @return the ProcessConnection equivalent of the specified serializable connection
	 */
	public ProcessConnection connectionFromSerializable(SerializableConnection sc) {
		ProcessConnection pc = null;
		pc = new ProcessConnection(sc.getType());
		updateConnectionFromSerializable(pc,sc);
		return pc;
	}
	
	/**
	 * Update the concrete instance of a Process block from a serializable block.
	 *
	 * @param pc
	 * @param sc
	 */
	public void updateConnectionFromSerializable(ProcessConnection pc,SerializableConnection sc) {
		pc.setSource(sc.getBeginBlock());
		pc.setUpstreamPortName(sc.getBeginAnchor().getId().toString());
		pc.setTarget(sc.getEndBlock());
		pc.setDownstreamPortName(sc.getEndAnchor().getId().toString());
	}
	
}
