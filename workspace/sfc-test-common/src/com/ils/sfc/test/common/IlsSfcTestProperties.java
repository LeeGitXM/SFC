/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 */
package com.ils.sfc.test.common;

/**
 *  Define properties that are common to all scopes.
 */
public interface IlsSfcTestProperties   {
	public final static String MODULE_ID = "blocktest";        // See module-blt-test.xml
	public final static String MODULE_NAME = "BLTTest";        // See build-blt-test.xml
	public final static String MOCK_SCRIPT_PACKAGE = "system.ils.test.mock";        // Python package name for block test
	public final static String TAG_SCRIPT_PACKAGE  = "system.ils.test.tag";         // Python package name for tag definition
	public final static String TIMESTAMP_FORMAT = "yyyy.MM.dd HH:mm:ss.SSS";        // Format for writing timestamp
}
