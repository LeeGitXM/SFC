/**
 *   (c) 2012-2013  ILS Automation. All rights reserved.
 *  
 *   Class contains static constants that have meaning to the ILS-Core module.
 */
package com.ils.icc2.designer.workspace;

import java.awt.Color;


/**
 *  Constants used in drawing components of the workspace.
 */
public interface WorkspaceConstants   {
	
	// Connection widths. In each case the border is a single pixel.
	// The "informational" path has a black line down its center.
	// These seem to look better with even pixel counts for the width
	public static final int CONNECTION_WIDTH_CENTERLINE     = 1;
	public static final int CONNECTION_WIDTH_TRUTHVALUE     = 4;
	public static final int CONNECTION_WIDTH_DATA           = 6;
	public static final int CONNECTION_WIDTH_INFORMATION    = 8;
	
	public static final Color CONNECTION_BACKGROUND           = Color.BLACK;
	public static final Color CONNECTION_HOVER                = new Color(255,255,40);   // Yellow
	public static final Color CONNECTION_SELECTED             = Color.MAGENTA;
	public static final Color CONNECTION_FILL_TRUTHVALUE      = Color.GREEN;
	public static final Color CONNECTION_FILL_DATA            = Color.LIGHT_GRAY;
	public static final Color CONNECTION_FILL_INFORMATION     = new Color(230,200,55);   // Mustard
}