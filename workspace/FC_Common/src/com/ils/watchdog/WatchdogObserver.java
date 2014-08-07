/**
 *   (c) 2014  ILS Automation. All rights reserved.
 *  
 */
package com.ils.watchdog;


/**
 *  This is the interface by which the watchdog timer 
 *  reports a watchdog timeout.
 */
public interface WatchdogObserver   {
	public void evaluate();
}
