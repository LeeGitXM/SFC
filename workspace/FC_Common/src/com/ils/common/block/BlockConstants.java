/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 *   Class contains static constants that have meaning to the ICC2 module.
 */
package com.ils.common.block;

import java.awt.Color;


/**
 *  Global constants for blocks and connection properties. These are
 *  collected in the common jar to prevent mutual dependencies 
 *  in otherwise unrelated projects.
 */
public interface BlockConstants   {
	public static final String TIMESTAMP_FORMAT = "yyyy.MM.dd HH:mm:ss.SSS";
	public static final long UNKNOWN = -1;
	
	// Use these when there is only a single input and/or output
	public static final String IN_PORT_NAME    = "in";
	public static final String OUT_PORT_NAME = "out";
		
	// These are block property names that used in multiple block definitions
	public static final String BLOCK_PROPERTY_CLEAR_ON_RESET = "ClearOnReset?";
	public static final String BLOCK_PROPERTY_DEADBAND      = "Deadband";
	public static final String BLOCK_PROPERTY_DISTRIBUTION  = "Distribution";
	public static final String BLOCK_PROPERTY_INSTANCE      = "Instance"; 
	public static final String BLOCK_PROPERTY_LIMIT         = "Limit";
	public static final String BLOCK_PROPERTY_LIMIT_TYPE    = "LimitType";
	public static final String BLOCK_PROPERTY_OFFSET        = "Offset";
	public static final String BLOCK_PROPERTY_SCOPE         = "Scope";
	public static final String BLOCK_PROPERTY_INHIBIT_INTERVAL = "InhibitInterval";  // Inhibit period ~ msec
	public static final String BLOCK_PROPERTY_SCAN_INTERVAL    = "ScanInterval";     // Compute interval ~ msec
	public static final String BLOCK_PROPERTY_SYNC_INTERVAL    = "SyncInterval";     // Time to coalesce inputs ~ msec
	
	// These are valid block data types
	public static final String BLOCK_TYPE_SCRIPT        = "script";  // Python module path
	public static final String BLOCK_TYPE_STRING        = "string";
	public static final String BLOCK_TYPE_TAG           = "tag";     // FUlly qualified tag path
	
	// These are valid/required properties for ports
	public static final String PORT_NAME                = "name";
	public static final String PORT_TYPE                = "type";   // datatype for a port
	
	// These are standard connection property names
	public static final String CONNECTION_PROPERTY_DOWNSTREAM_PORT     = "downstream"; 
	public static final String CONNECTION_PROPERTY_QUALITY             = "quality";
	public static final String CONNECTION_PROPERTY_UPSTREAM_PORT       = "upstream"; 
	public static final String CONNECTION_PROPERTY_VALUE               = "value"; 
	
	// These are standard palette tab names
	public static final String PALETTE_TAB_CONNECTIVITY       = "Connectivity";
	public static final String PALETTE_TAB_CONTROL            = "Control";

	// Block filler colors
	public static final int BLOCK_BACKGROUND_LIGHT_BLUE     = (new Color(195,225,240)).getRGB();
	public static final int BLOCK_BACKGROUND_LIGHT_GRAY     = (new Color(240,240,240)).getRGB();
	
}
