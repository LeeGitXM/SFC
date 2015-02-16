/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ils.sfc.migration.DOMUtil;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Given a list of steps in a chart and their connections, determine
 * x-y placements.
 */
public class StepLayoutManager {
	private final static String TAG = "StepLayoutManager";
	private static int UNSET = -8888;
	private  final LoggerEx log = LogUtil.getLogger(StepLayoutManager.class.getPackage().getName());
	private final Map<String,Element> blockMap;             // block by UUID
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
	private final ArrayList<Integer> rightmostIndex;        // Rightmost index by row number 
	// Record the chart limits so that we can return a canvas size, if asked.
	private int minx = 0;
	private int miny = 0;
	private int maxx = 0;
	private int maxy = 0;
	private double zoom = 1.0;
	/**
	 * Constructor: Immediately analyze the supplied chart.
	 * @param g2chart
	 */
	public StepLayoutManager(Document g2chart) {
		this.blockMap = new HashMap<>();
		this.connectionMap = new HashMap<>();
		this.gridMap = new HashMap<>();
		this.rightmostIndex = new ArrayList<>();
		analyze(g2chart.getElementsByTagName("block"));
		center();
	}
	public Map<String,Element>   getBlockMap() { return this.blockMap; }
	public Map<String,GridPoint> getGridMap() { return this.gridMap; }
	public double getZoom() { return this.zoom; }
	
	// ========================================= This is where the work gets done ================================
	private void analyze(NodeList blocklist) {
		int index = 0;
		// First-time through create default entries in the grid map
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
			blockMap.put(uuid, block);
			gridMap.put(uuid,new GridPoint(UNSET,UNSET));
			log.infof("%s.analyze: block and gridpoints for %s(%s)",TAG,block.getAttribute("name"),uuid);
			ConnectionHub hub = connectionMap.get(uuid);
			if( hub==null) {
				hub = new ConnectionHub();
				connectionMap.put(uuid,hub);
			}
			NodeList connections = block.getElementsByTagName("connectedTo");
			int jndex = 0;
			while( jndex < connections.getLength() ) {
				Element connection = (Element)connections.item(jndex);
				String cxn = StepTranslator.canonicalForm(connection.getAttribute("uuid"));
				log.infof("%s.analyze: connected to %s",TAG,cxn);
				hub.addConnectionTo(cxn);
				ConnectionHub destinationHub = connectionMap.get(cxn);
				if( destinationHub==null) {
					destinationHub = new ConnectionHub();
					connectionMap.put(cxn,destinationHub);
				}
				destinationHub.addConnectionFrom(uuid);
				jndex++;
			}
			index++;
		}
		// Find the begin block. There can be only one.
		String beginuuid = null;
		index = 0;
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
			ConnectionHub hub = connectionMap.get(uuid);
			if( hub.getConnectionsTo().isEmpty()) {
				beginuuid = uuid;
				break;
			}
			index++;
		}
		if( beginuuid==null ) {
			log.errorf("%s.analyze: Chart has no begin block", TAG);
			return;
		}
		log.infof("%s.analyze: begin block is %s",TAG,beginuuid);
		// Now do the layout. Position the root. Walk the tree.
		int x = 0;   // Center on zero so that we can scale if need be.
		int y = 2;
		positionNode(beginuuid,x,y);
	}
	
	/** 
	 * Recursive routine to set the position of the specified node
	 * and continue on with its children.
	 * 
	 * @param uuid the block being placed
	 * @param x the block's new x
	 * @param y the block's new y
	 */
	private void positionNode(String uuid,int x,int y) {
		GridPoint gp = gridMap.get(uuid);
		
		// If we conflict on the left, move everything right
		int rightmost = getRightmost(y);
		if( rightmost > x-2 ) {
			int dx = rightmost - x + 2;
			x = x + dx;
			moveAncestryRight(uuid,dx);
		}
		gp.x = x;
		gp.y = y;
		setRightmost(x,y);
		
		
		ConnectionHub hub = connectionMap.get(uuid);
		List<String> nextBlocks = hub.getConnectionsTo();
		if( nextBlocks.size() < 2) y = y+1;
		else                       y = y+2;  // Allow for connections
		int xpos = x - (nextBlocks.size()-1);
		for( String childuuid:nextBlocks) {
			positionNode(childuuid,xpos,y);
			gp = gridMap.get(childuuid);  
			xpos = gp.x + 2;                // Position for next block
		}
	}
	
	// The original layout may create indices that are out-of-range.
	// Place the diagram in the upper left corner of the space. 
	private void center() {
		// First iteration gets the bounds
		minx = 10000;
		miny = 10000;
		maxx = -10000;
		maxy = -10000;
		for( GridPoint gp:gridMap.values()) {
			if( gp.x>maxx ) maxx = gp.x;
			if( gp.y>maxy ) maxy = gp.y;
			if( gp.x<minx ) minx = gp.x;
			if( gp.y<miny ) miny = gp.y;
		}
		
		// Make adjustments the second time through
		int deltax = minx - 1;      // Normalize to left edge at 1
		int deltay = miny - 1;      // Normalize to top edge  at 1
		minx = minx - deltax;
		maxx = maxx - deltax;
		miny = miny - deltay;
		maxy = maxy - deltay;
		for( GridPoint gp:gridMap.values()) {
			gp.x = gp.x - deltax;
			gp.y = gp.y - deltay;
		}
		
		// As a bonus, we create the zoom factor.
		// The standard grid is 10x10. Factor our layout to the same physical size.
		// Consider both dimensions equally.
		int max = maxx;
		if( maxy>max) max = maxy;
		if( max>10) this.zoom =  10./(double)max;
	}
	
	// Traverse the parentage of the specified block and 
	// move them to the right, recursively. If the parent
	// has multiple children, adjust the move to try and
	// keep it centered.
	//
	// This depends on the fact that we have not yet placed
	// the blocks to our right.
	private void moveAncestryRight(String uuid,int dx) {
		ConnectionHub hub = connectionMap.get(uuid);
		if( hub!=null && !hub.getConnectionsFrom().isEmpty()) {
			for(String parent:hub.getConnectionsFrom() ) {
				int childcount = hub.getConnectionsTo().size();
				int position   = hub.getConnectionsTo().indexOf(parent);
				if( position>=0 && childcount>0) {
					dx = dx*(childcount-position)/childcount;
					GridPoint gp = gridMap.get(parent);
					gp.x = gp.x + dx;
					moveAncestryRight(parent,dx);
				}
				else {
					log.warnf("%s.moveAncestryRight: Parent (%s) of %s has no children.", TAG,parent,uuid);
				}
			}
		}
	}
	private void setRightmost(int x,int y) {
		while( y>=rightmostIndex.size() ) {
			rightmostIndex.add(null);
		}
		rightmostIndex.set(y,new Integer(x));
	}
	
	private int getRightmost(int y) {
		int index = Integer.MIN_VALUE;
		if( y< rightmostIndex.size()) {
			Integer val = rightmostIndex.get(y);
			if( val!=null ) index = val.intValue();
		}
		return index;
	}
	
	private class ConnectionHub {
		private final List<String> connectedTo;
		private final List<String> connectedFrom;
		public ConnectionHub() {
			this.connectedTo = new ArrayList<>();
			this.connectedFrom = new ArrayList<>();
		}
		
		public void addConnectionTo(String uuid) { connectedTo.add(uuid); }
		public void addConnectionFrom(String uuid) { connectedFrom.add(uuid); }
		public List<String> getConnectionsTo() { return connectedTo; }
		public List<String> getConnectionsFrom() { return connectedFrom; }
	}
}
