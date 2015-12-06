/**
 *   (c) 2014  ILS Automation. All rights reserved.
 */
package com.ils.sfc.browser;



/**
 *  Global constants for the chart browser.
 */
public interface BrowserConstants   {
	// Resource types
	public static final String CHART_RESOURCE_TYPE="sfc-chart-ui-model";
	public static final String FOLDER_RESOURCE_TYPE="__folder";
	public static final String MODULE_ID = "sfc-browser";
	// Table column names
	public static final String CXNS    = "Cxns";          // Incoming connection count
	public static final String KEY      = "Key";
	public static final String NAME     = "Name";
	public static final String PATH    = "Path";       // Chart identifier
	public static final String RESOURCE = "Resource"; // ResourceId
	public static final String STATUS   = "Status";   // Health of the node
	// Indicates a VisualItem resource that represents an enclosing chart step
	public static final int NO_RESOURCE = -2;  // Enclosing step has no associated resource
	// Status values
	public static final int STATUS_OK   = 0;
	public static final int STATUS_PATH = 1;    // Bad path references
	public static final int STATUS_LOOP = 2;    // Infinite loop
	
	// Validation table column names
	public static final String CALLED_FROM_COL    = "Called From (Enclosure)"; 
	public static final String CHART_PATH_COL     = "Chart Path";     
	public static final String ERROR_COL          = "Error Description";     
	public static final String RESID_COL          = "ResID";  
	public static final String STEP_PATH_COL      = "Chart Step Path";
	public static final int   CHART_PATH_WIDTH = 250;
	public static final int   REMAINDER_WIDTH = 2000;
	public static final int   RESID_WIDTH = 20;
	public static final int   STEP_PATH_WIDTH = 300;
}
