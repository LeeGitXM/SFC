/**
 *   (c) 2013-2014  ILS Automation. All rights reserved. 
 */
package com.ils.common.block;

import java.util.Set;
import java.util.UUID;

import com.ils.common.control.BlockPropertyChangeEvent;
import com.ils.common.control.BlockPropertyChangeListener;
import com.ils.common.control.IncomingNotification;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;


/**
 * This interface defines an executable block in a diagram.
 * Each block carries its unique identity consisting of a projectId,
 * a diagramId and blockId. 
 */
public interface ProcessBlock extends BlockPropertyChangeListener {
	/**
	 * Notify the block that a new value has appeared on one of its
	 * input anchors. The notification contains the upstream source
	 * block, the port and value.
	 * @param vcn 
	 */
	public void acceptValue(IncomingNotification vcn);
	
	/**
	 * @return the Id of the block's diagram (parent).
	 */
	public UUID getParentId();
	/**
	 * @return the universally unique Id of the block.
	 */
	public UUID getBlockId();
	/**
	 * @return the block's label
	 */
	public String getName();
	/**
	 * @return the current state of the block
	 */
	public BlockState getState();
	/**
	 * @return a string describing the status of the block. This 
	 * 		string is used for the dynamic block display.
	 */
	public String getStatusText();
	
	/**
	 * @return information necessary to populate the block 
	 *          palette and subsequently paint a new block
	 *          dropped on the workspace.
	 */
	public PalettePrototype getBlockPrototype();
	
	/**
	 * @return a particular property by name.
	 */
	public BlockProperty getProperty(String name);

	/**
	 * @return a list of names of properties known to this class.
	 */
	public Set<String> getPropertyNames() ;
	
	/**
	 * @return all properties of the block. The array may be used
	 * 			to updated properties directly.
	 */
	public BlockProperty[] getProperties();
	
	/**
	 * Reset the internal state of the block.
	 */
	public void reset();
	
	/**
	 * @param name the name of the block. The name
	 *        is guaranteed to be unique within a 
	 *        diagram.
	 */
	public void setName(String name);
	/**
	 * Accept a new value for a block property. It is up to the
	 * block to determine whether or not this triggers block 
	 * evaluation.
	 * @param name of the property to update
	 * @param value new value of the property
	 */
	public void setProperty(String name,QualifiedValue value);
	/**
	 * Set the current state of the block.
	 * @param state
	 */
	public void setState(BlockState state);
	/**
	 * @param text the current status of the block
	 */
	public void setStatusText(String text);
	
	/**
	 * In the case where the block has specified a coalescing time,
	 * this method will be called by the engine after receipt of input
	 * once the coalescing "quiet" time has passed without further input.
	 */
	public void evaluate();
	//===================== PropertyChangeListener ======================
	/**
	 * This is a stricter implementation that enforces QualifiedValue data.
	 */
	public void propertyChange(BlockPropertyChangeEvent event);
}