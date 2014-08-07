/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ils.block.control.PropertyHolder;
import com.ils.common.block.AnchorPrototype;
import com.ils.common.block.BlockDescriptor;
import com.ils.common.block.BlockProperty;
import com.ils.common.block.BlockState;
import com.ils.common.block.PalettePrototype;
import com.ils.common.block.ProcessBlock;
import com.ils.common.control.BlockPropertyChangeEvent;
import com.ils.common.control.BlockPropertyChangeListener;
import com.ils.common.control.ExecutionController;
import com.ils.common.control.IncomingNotification;
import com.ils.watchdog.WatchdogObserver;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;


/**
 * This abstract class is the basis of all blocks. It cannot in itself
 * be instantiated. 
 *  
 * The subclasses depend on the "ExecutableBlock" class annotation
 * as the marker to group a particular subclass into the list of 
 * available executable block types.
 */
public abstract class AbstractProcessBlock implements ProcessBlock, BlockPropertyChangeListener, WatchdogObserver {
	
	protected ExecutionController controller = null;
	private UUID blockId;
	private UUID parentId;
	private String name = ".";
	protected String statusText;
	protected PalettePrototype prototype = null;
	protected BlockState state;

	protected final LoggerEx log = LogUtil.getLogger(getClass().getPackage().getName());
	/** Properties are a dictionary of attributes keyed by property name */
	protected final PropertyHolder properties;
	/** Describe ports/stubs where connections join the block */
	protected final List<AnchorPrototype> anchors;

	
	/**
	 * Constructor: The no-arg constructor is used when creating a prototype for use in the palette.
	 *              It does not correspond to a functioning block.
	 */
	public AbstractProcessBlock() {
		properties = new PropertyHolder();
		properties.addBlockPropertyChangeListener(this);
		anchors = new ArrayList<AnchorPrototype>();
		initialize();
		initializePrototype();
	}
	
	/**
	 * Constructor: Use this version to create a block that correlates to a block in the diagram.
	 * @param ec execution controller for handling block output
	 * @param parent universally unique Id identifying the parent of this block
	 * @param block universally unique Id for the block
	 */
	public AbstractProcessBlock(ExecutionController ec, UUID parent, UUID block) {
		this();
		this.controller = ec;
		this.blockId = block;
		this.parentId = parent;
	}
	
	/**
	 * Create an initial list of properties. There is none for the base class.
	 */
	private void initialize() {
		this.state = BlockState.INITIALIZED;
	}
	
	/**
	 * Fill a prototype object with defaults - as much as is reasonable.
	 */
	private void initializePrototype() {
		prototype = new PalettePrototype();
		BlockDescriptor blockDescriptor = prototype.getBlockDescriptor();
		blockDescriptor.setAnchors(anchors);

		
		// Currently this refers to a path in /images of the BLT_Designer source area.
		prototype.setPaletteIconPath("unknown.png");
	}
	
	
	@Override
	public PalettePrototype getBlockPrototype() {return prototype; }
	@Override
	public String getName() {return name;}
	@Override
	public BlockState getState() {return state;}
	@Override
	public void setState(BlockState state) {this.state = state;}
	@Override
	public void setName(String lbl) {this.name = lbl;}
	@Override
	public String getStatusText() {return statusText;}
	@Override
	public void setStatusText(String statusText) {this.statusText = statusText;}

	/**
	 * @param name the property (attribute) name.
	 * @return a particular property given its name.
	 */
	@Override
	public BlockProperty getProperty(String name) {
		return properties.get(name);
	}
	
	@Override
	public UUID getParentId() { return parentId; }
	@Override
	public UUID getBlockId() { return blockId; }

	/**
	 * @return all properties. The returned array is a copy of the internal.
	 * Thus although the attributes of an individual property can be modified,
	 * the makeup of the set cannot.
	 * @return properties an array of the properties of the block.
	 */
	public BlockProperty[] getProperties() {
		Collection<BlockProperty> propertyList = properties.values();
		BlockProperty[] results = new BlockProperty[propertyList.size()];
		int index=0;
		for(BlockProperty bp:propertyList ) {
			results[index]=bp;
			index++;
		}
		return results;
	}
	
	/**
	 * @return a list of the attribute names required by this class.
	 */
	@Override
	public Set<String> getPropertyNames() {
		return properties.keySet();
	}
	
	
	/**
	 * The default method sets the state to INITIALIZED.
	 */
	@Override
	public void reset() {this.state = BlockState.INITIALIZED;}
	/**
	 * Accept a new value for a block property. In general this does not trigger
	 * block evaluation. Use the property change listener interface to do so.
	 * 
	 * @param name of the property to update
	 * @param value new value of the property
	 */
	@Override
	public void setProperty(String name,QualifiedValue value) {
		BlockProperty prop = getProperty(name);
		if( prop!=null && value!=null ) {
			prop.setValue(value.getValue());
			prop.setQuality(value.getQuality().getName());
		}
	}
	/**
	 * The block is notified that a new value has appeared on one of its input anchors.
	 * The base implementation simply logs the value.
	 * 
	 * Note: there can be several connections attached to a given port.
	 * @param vcn notification of the new value.
	 */
	@Override
	public void acceptValue(IncomingNotification vcn) {
		validate(vcn );
		log.tracef("%s.setValue: incoming value: %s from %s:%s",getName(),
				valueToString(vcn.getValue()),
				vcn.getConnection().getSource().toString(),
				vcn.getConnection().getUpstreamPortName());

	}
	
	/**
	 * In the case where the block has specified a coalescing time, this method
	 * will be called by the engine after receipt of input once the coalescing 
	 * "quiet" time has passed without further input.
	 * 
	 * The default implementation is appropriate for blocks that trigger calculation
	 * on every update of the inputs. It does nothing.
	 */
	public void evaluate() {}
	
	// ================================= PropertyChangeListener ================================
	/**
	 * One of the block properties has changed. This default implementation simply updates
	 * the block property with the new value and logs the result. The data type is guaranteed
	 * to be QualifiedValue.
	 */
	@Override
	public void propertyChange(BlockPropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		QualifiedValue newValue = event.getNewValue();
		setProperty(propertyName,newValue);

		if( log.isTraceEnabled() ) {
			QualifiedValue oldValue = event.getOldValue();
			log.tracef("%s.propertyChange: %s from %s to %s",this.getName(),propertyName,
					(oldValue==null?"null":oldValue.toString()),(newValue==null?"null":newValue.toString()));
		}
	}
	
	/**
	 * Validate the incoming value. This does not execute unless the logger
	 * is enabled for debugging.
	 */
	private void validate(IncomingNotification vcn ) {
		if( log.isDebugEnabled() ) {
			if(getName()==null)          log.warnf("AbstractProcessBlock.validate: getLabel is null");
			if(vcn.getConnection()==null) log.warnf("AbstractProcessBlock.validate: getConnection is null");
			if(vcn.getValue()==null)      log.warnf("AbstractProcessBlock.validate: getValue is null");
			if(vcn.getConnection().getSource()==null) log.warnf("AbstractProcessBlock.validate: getSource is null");
			if(vcn.getConnection().getUpstreamPortName()==null) log.warnf("AbstractProcessBlock.validate: getUpPort is null");
		}
	}
	
	/**
	 * Convert a value received on an input connection 
	 * into a string. Used for debugging purposes.
	 * @param val
	 * @return the specified value (or value of value) as a string.
	 */
	private String valueToString(Object val) {
		String result = "";
		if( val==null ) {
			result = "NULL";
		}
		else if( val instanceof QualifiedValue ) {
			Object value = ((QualifiedValue)val).getValue();
			if( value==null ) result = "NULL";
			else              result = value.toString();
		}
		else {
			result = val.toString();
		}
		return result;
	}
	
	// So that class is comparable
	// Same blockId is sufficient to prove equality
	@Override
	public boolean equals(Object arg) {
		boolean result = false;
		if( arg instanceof AbstractProcessBlock) {
			AbstractProcessBlock that = (AbstractProcessBlock)arg;
			if( this.getBlockId().equals(that.getBlockId()) ) {
				result = true;
			}
		}
		return result;
	}
	@Override
	public int hashCode() {
		return this.getBlockId().hashCode();
	}
	
	/**
	 * Identify the block as a string. Make this as user-friendly as possible.
	 */
	@Override
	public String toString() { return getName(); }

}