/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 *   This interface defines methods available in Client or Designer
 *   scope for communication with the BlockExecutionController.
 */
package com.ils.common.control;

import com.ils.watchdog.Watchdog;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;


/**
 *  This interface describes a controller that accepts change notifications
 *  from the blocks and acts as a delegate for facilities that are not
 *  within the Block Definition project. 
 */
public interface ExecutionController  {

	public void acceptCompletionNotification(OutgoingNotification note);
	public void pet(Watchdog dog);
	public void removeWatchdog(Watchdog dog);
	public void updateTag(String tagPath,QualifiedValue qv);
}
