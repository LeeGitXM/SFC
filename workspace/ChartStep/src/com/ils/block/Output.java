/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.block;

import java.awt.Color;
import java.util.UUID;

import com.ils.block.annotation.ExecutableBlock;
import com.ils.common.block.AnchorDirection;
import com.ils.common.block.AnchorPrototype;
import com.ils.common.block.BlockConstants;
import com.ils.common.block.BlockDescriptor;
import com.ils.common.block.BlockProperty;
import com.ils.common.block.BlockState;
import com.ils.common.block.BlockStyle;
import com.ils.common.block.ProcessBlock;
import com.ils.common.block.PropertyType;
import com.ils.common.connection.ConnectionType;
import com.ils.common.control.BlockPropertyChangeEvent;
import com.ils.common.control.ExecutionController;
import com.ils.common.control.IncomingNotification;
import com.inductiveautomation.ignition.common.model.values.BasicQualifiedValue;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;

/**
 * A tagwriter propagates values on its input to a configured tag path.
 */
@ExecutableBlock
public class Output extends AbstractProcessBlock implements ProcessBlock {
	protected static String BLOCK_PROPERTY_TAG_PATH = "TagPath";

	protected String path = "";
	
	/**
	 * Constructor: The no-arg constructor is used when creating a prototype for use in the palette.
	 */
	public Output() {
		initialize();
		initializePrototype();
	}
	
	/**
	 * Constructor. Custom property is "diagnosis".
	 * 
	 * @param ec execution controller for handling block output
	 * @param parent universally unique Id identifying the parent of this block
	 * @param block universally unique Id for the block
	 */
	public Output(ExecutionController ec,UUID parent,UUID block) {
		super(ec,parent,block);
		initialize();
	}
	
	/**
	 * One of the block properties has changed. This default implementation does nothing.
	 */
	@Override
	public void propertyChange(BlockPropertyChangeEvent event) {
		super.propertyChange(event);
		if(event.getPropertyName().equals(BLOCK_PROPERTY_TAG_PATH)) {
			path = event.getNewValue().getValue().toString();
		}
	}
	
	/**
	 * The block is notified that a new value has appeared on one of its input anchors.
	 * Write the value to the configured tag. Handle any of the possible input types.
	 * @param vcn notification of the new value.
	 */
	@Override
	public void acceptValue(IncomingNotification vcn) {
		super.acceptValue(vcn);
		this.state = BlockState.ACTIVE;
		QualifiedValue qv = null;
		Object val = vcn.getValue();
		if( val instanceof QualifiedValue ) {
			qv = vcn.getValueAsQualifiedValue();
		}
		else {
			qv = new BasicQualifiedValue(val.toString());
		}
		controller.updateTag(path, qv);
	}
	
	/**
	 * Add properties that are new for this class.
	 * Populate them with default values.
	 */
	private void initialize() {
		setName("Output");
		BlockProperty tag = new BlockProperty(BLOCK_PROPERTY_TAG_PATH,null,PropertyType.STRING,true);
		properties.put(BLOCK_PROPERTY_TAG_PATH, tag);
		
		// Define a single input
		AnchorPrototype input = new AnchorPrototype(BlockConstants.IN_PORT_NAME,AnchorDirection.INCOMING,ConnectionType.DATA);
		anchors.add(input);
	}
	
	/**
	 * Augment the palette prototype for this block class.
	 */
	private void initializePrototype() {
		prototype.setPaletteIconPath("BNC/icons/palette/output.png");
		prototype.setPaletteLabel("Output");
		prototype.setTooltipText("Write the incoming value to a pre-configured tag");
		prototype.setTabName(BlockConstants.PALETTE_TAB_CONNECTIVITY);
		
		BlockDescriptor desc = prototype.getBlockDescriptor();
		desc.setBlockClass(getClass().getCanonicalName());
		desc.setStyle(BlockStyle.ARROW);
		desc.setPreferredHeight(45);
		desc.setPreferredWidth(60);
		desc.setBackground(new Color(125,110,230).getRGB());   // Purple
	}
}