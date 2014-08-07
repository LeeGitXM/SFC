/**
 *   (c) 2014 ILS Automation. All rights reserved.
 *  
 */
package com.ils.icc2.common;

import java.util.UUID;


/**
 *  Define an interface for accessing module properties .
 */
public interface ICC2Properties   {   
	public final static String MODULE_ID = "icc2";     // See module-icc2.xml
	public final static String MODULE_NAME = "ICC2";   // See build-icc2.xml
	
	/** This is the name of the jar file containing block class definitions */
	public final static String BLOCK_JAR_NAME = "icc2-blocks";
	
	public final static String DIAGRAM_RESOURCE_TYPE       = "icc2.diagram";
	public final static String FOLDER_RESOURCE_TYPE        = "__folder";
	
	public final static String APPLICATION_SCRIPT_PACKAGE      = "system.ils.icc2.application";
	public final static String BLOCK_SCRIPT_PACKAGE            = "system.ils.icc2.block";
	
	/** This unique ID represents the root node in the project tree */
	public static final UUID ROOT_FOLDER_UUID = UUID
			.fromString("7bbbd6b9-3140-4328-a844-51817eb58574");
	public static final String ROOT_FOLDER_NAME = "ROOT";   
	
	// This is the common prefix under which bundle files are identified/registered
	public static final String BUNDLE_PREFIX = "icc2";
	public static final String BLOCK_PREFIX  = "block";
	public static final String CUSTOM_PREFIX = "custom";
	// This is where we find the string resources for blocks
	public static final String BLOCK_RESOURCE_PATH = "com.ils.icc2.designer";
	
	// These are the property names in the message payload, gateway to client
	public static final String MSG_BLOCK_NAME      = "BlockName";
	public static final String MSG_BLOCK_STATE     = "BlockState";
	public static final String MSG_WORKSPACE_ID    = "WorkspaceID";    // UUID of the component's workspace
	
	// These are names of system properties
	public static final String EXIM_PATH = "icc2.exim.path";            // Default for file choose dialogs
	// These are the key names allowed in the Python dictionary that defines a block attribute.
	public static final String BLOCK_ATTRIBUTE_BINDING    = "binding";
	public static final String BLOCK_ATTRIBUTE_BINDING_TYPE = "bindingType";
	public static final String BLOCK_ATTRIBUTE_EDITABLE   = "editable";
	public static final String BLOCK_ATTRIBUTE_MAX        = "maximum";
	public static final String BLOCK_ATTRIBUTE_MIN        = "minimum";	
	public static final String BLOCK_ATTRIBUTE_NAME       = "name";
	public static final String BLOCK_ATTRIBUTE_QUALITY    = "quality";
	public static final String BLOCK_ATTRIBUTE_DATA_TYPE  = "type";
	public static final String BLOCK_ATTRIBUTE_VALUE      = "value";
	
}
