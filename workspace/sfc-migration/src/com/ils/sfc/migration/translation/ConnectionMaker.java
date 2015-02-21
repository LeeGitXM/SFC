/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * The initial layout does not include connections. Follow
 * logical connections for steps, insert "physical" connections
 * where needed. A "connection" occupies one spot on the grid.
 * It has elements <up>,<down>,<left>,<right>
 */
public class ConnectionMaker {
	private final static String TAG = "ConnectionCreator";
	private final Dimension gridSize;
	private final Map<String,Element> blockMap;             // block by UUID
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
	
	public ConnectionMaker(Map<String,Element> blocks,Map<String,ConnectionHub> connections,Map<String,GridPoint> gridPoints) {
		this.blockMap = blocks;
		this.connectionMap = connections;
		this.gridMap = gridPoints;
		this.gridSize = computeGridDimension(gridMap);
		createConnections();
	}
	
	/** 
	 * Traverse the layout grid, adding connections where needed.
	 */
	private void createConnections() {
		
		
	}
	
	/** 
	 * @return a list of connection elements.
	 */
	public List<Element> getConnections(Document chart) {
		List<Element> connections = new ArrayList<>();
		return connections;
		
	}
	// 
	/**
	 * Find the width and height
	 * @return the grid dimension
	 */
	private Dimension computeGridDimension(Map<String,GridPoint> gridPoints) {
		// First iteration gets the bounds
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;

		for( GridPoint gp:gridPoints.values()) {
			if( gp.x>maxx ) maxx = gp.x;
			if( gp.y>maxy ) maxy = gp.y;
		}
		return new Dimension(maxx,maxy);
	}
	
	
	private class Cxn {
		private boolean up   = false;
		private boolean down = false;
		private boolean right= false;
		private boolean left = false;

		public Cxn() {}

		public boolean hasUp() {return up;}
		public void setUp(boolean up) {this.up = up;}
		public boolean hasDown() {return down;}
		public void setDown(boolean down) {this.down = down;}
		public boolean hasRight() {return right;}
		public void setRight(boolean right) {this.right = right;}
		public boolean hasLeft() {return left;}
		public void setLeft(boolean left) {this.left = left;}
	}

}

