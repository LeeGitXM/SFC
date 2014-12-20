/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.designer.browser;



/**
 *  Global constants for the chart browser.
 */
public interface BrowserConstants   {
	// Resource types
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	// Table column names
	public static final String CXNS    = "Cxns";          // Incoming connection count
	public static final String ENCLOSURES = "Enclosures"; // Refs to this as an enclosure
	public static final String KEY      = "Key";
	public static final String NAME     = "Name";
	public static final String PARENT   = "Parent";
	public static final String PATH    = "Path";       // Chart identifier
	public static final String RESOURCE = "Resource"; // ResourceId
	
	// Indicates a VisualItem resource that represents an enclosing chart step
	public static final int NO_RESOURCE = -2;  // Enclosing step has no associated resource
	
}
