/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.block;

import java.awt.Color;
import java.util.UUID;

import com.ils.block.annotation.ExecutableBlock;
import com.ils.common.block.AnchorDirection;
import com.ils.common.block.AnchorPrototype;
import com.ils.common.block.BindingType;
import com.ils.common.block.BlockConstants;
import com.ils.common.block.BlockDescriptor;
import com.ils.common.block.BlockProperty;
import com.ils.common.block.BlockStyle;
import com.ils.common.block.ProcessBlock;
import com.ils.common.block.PropertyType;
import com.ils.common.connection.ConnectionType;
import com.ils.common.control.BlockPropertyChangeEvent;
import com.ils.common.control.ExecutionController;
import com.ils.common.control.OutgoingNotification;

/**
 * This class subscribes to value changes for a specified tag.
 * Value changes are propagated to the output connection.
 * 
 * Annotate the constructor.
 */
@ExecutableBlock
public class Input extends AbstractProcessBlock implements ProcessBlock {
	protected static String BLOCK_PROPERTY_INPUT = "Input";
	
	/**
	 * Constructor: The no-arg constructor is used when creating a prototype for use in the palette.
	 */
	public Input() {
		initialize();
		initializePrototype();
	}
	
	/**
	 * Constructor: Custom property is "entry"
	 * 
	 * @param ec execution controller for handling block output
	 * @param parent universally unique Id identifying the parent of this block
	 * @param block universally unique Id for the block
	 */
	public Input(ExecutionController ec,UUID parent,UUID block) {
		super(ec,parent,block);
		initialize();
	}
	
	/**
	 * Add the tag property and link it to the value property.
	 */
	private void initialize() {
		setName("Input");
		BlockProperty value = new BlockProperty(BLOCK_PROPERTY_INPUT,null,PropertyType.OBJECT,true);
		value.setBinding("");
		value.setBindingType(BindingType.TAG);
		properties.put(BLOCK_PROPERTY_INPUT, value);
		
		// Define a single output
		AnchorPrototype output = new AnchorPrototype(BlockConstants.OUT_PORT_NAME,AnchorDirection.OUTGOING,ConnectionType.DATA);
		anchors.add(output);
	}
	
	/**
	 * If the input property changes, then place the new value on the output.
	 */
	@Override
	public void propertyChange(BlockPropertyChangeEvent event) {
		super.propertyChange(event);
		if( event.getPropertyName().equals(BLOCK_PROPERTY_INPUT)) {
			OutgoingNotification nvn = new OutgoingNotification(this,BlockConstants.OUT_PORT_NAME,event.getNewValue());
			controller.acceptCompletionNotification(nvn);
		}
	}

	/**
	 * Augment the palette prototype for this block class.
	 */
	private void initializePrototype() {
		prototype.setPaletteIconPath("BNC/icons/palette/input.png");
		prototype.setPaletteLabel("Input");
		prototype.setTooltipText("Place values on the output when a configured tag is updated");
		prototype.setTabName(BlockConstants.PALETTE_TAB_CONNECTIVITY);
		
		BlockDescriptor desc = prototype.getBlockDescriptor();
		desc.setBlockClass(getClass().getCanonicalName());
		desc.setStyle(BlockStyle.ARROW);
		desc.setPreferredHeight(45);
		desc.setPreferredWidth(60);
		desc.setBackground(Color.cyan.getRGB());
	}
}