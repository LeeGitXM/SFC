/**
 *   (c) 2013  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;



/**
 *  A grid point represents a spot on the layout grid
 *  of an SFC chart. This object is a simple bean with no behavior.
 *  For simple access, the members are public.
 *  
 */
public class GridPoint  {
	public int x = 0;      // column number
	public int y = 0;      // row number
	
	public GridPoint() {
		x = 0;
		y = 0;
	}
	
	/**
	 * Create a new grid point.
	 */
	public GridPoint(int col,int row) {
		this.x = col;
		this.y = row;
	}
	
	/**
	 * This is a debugging aid.
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append(x);
		str.append(":");
		str.append(y);
		return str.toString();
	}
}
