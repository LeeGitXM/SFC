/**
 *   (c) 2013  ILS Automation. All rights reserved.
 *  
 */
package com.ils.icc2.common;

import java.util.List;

import com.ils.common.block.PalettePrototype;
import com.ils.icc2.common.serializable.SerializableResourceDescriptor;


/**
 * This class exposes python-callable functions that deal with properties
 * of diagrams, blocks and connections. It also handles
 * functions of the engine itself. 
 * 
 * Where applicable, we make use of the ApplicationRequestHandler to perform the requests.
 */
public class ApplicationScriptFunctions   {
	private static ApplicationRequestManager manager = new ApplicationRequestManager();

	/**
	 * Query the gateway for a list of prototypes for the defined blocks. 
	 */
	@SuppressWarnings("rawtypes")
	public static List getBlockPrototypes() {
		List<PalettePrototype> result = manager.getBlockPrototypes();
		return result;
	}
	
	
	/**
	 * Query the gateway for list of diagrams 
	 * 
	 * @param projectName
	 * @return a list of tree-paths to the diagrams saved (ie. known to the Gateway).
	 */
	@SuppressWarnings("rawtypes")
	public static List getDiagramTreePaths(String projectName) {
		return manager.getDiagramTreePaths(projectName);
	}
	
	/**
	 * @return the current state of the controller.
	 */
	public static String getControllerState() {
		return manager.getControllerState();
	}
	
	/**
	 * Query the gateway for list of resources that it knows about. This is
	 * a debugging aid. 
	 * 
	 * @return a list of resources known to the BlockController.
	 */
	@SuppressWarnings("rawtypes")
	public static List queryControllerResources() {
		List<SerializableResourceDescriptor> result = manager.queryControllerResources();
		return result;
	}
	
	
	/**
	 * Start the block execution engine in the gateway.
	 */
	public static void startController() {
		manager.startController();
	}

	/**
	 * Shutdown the block execution engine in the gateway.
	 */
	public static void stopController() {
		manager.stopController();
	}

	
}