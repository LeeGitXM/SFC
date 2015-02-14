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
		analyze(g2chart.getElementsByTagName("block"));
		center();
	}
	
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
			String connectionString = block.getAttribute("connectedTo");
			if( connectionString!=null ) {
				ConnectionHub hub = connectionMap.get(uuid);
				if( hub==null) {
					hub = new ConnectionHub();
					connectionMap.put(uuid,hub);
				}
				String[] connections = connectionString.split(",");
				int jndex = 0;
				while( jndex < connections.length ) {
					String cxn = StepTranslator.canonicalForm(connections[jndex]);
					hub.addConnectionTo(cxn);
					ConnectionHub destinationHub = connectionMap.get(cxn);
					if( destinationHub==null) {
						destinationHub = new ConnectionHub();
						connectionMap.put(cxn,destinationHub);
					}
					destinationHub.addConnectionFrom(uuid);
					jndex++;
				}
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
			if( hub.getConnectionsFrom().isEmpty()) {
				beginuuid = uuid;
				break;
			}
			index++;
		}
		if( beginuuid==null ) {
			log.errorf("%s.analyze: Chart has no begin block", TAG);
			return;
		}
		
		// Now do the layout. Position the root. Walk the tree.
		int x = 0;   // Center on zero so that we can scale if need be.
		int y = 2;
		GridPoint gp = gridMap.get(beginuuid);
		gp.x = x;
		gp.y = y;
		ConnectionHub hub = connectionMap.get(beginuuid);
		List<String> nextBlocks = hub.getConnectionsTo();
		if( nextBlocks.size() < 2) y = y+1;
		else                       y = y+2;  // Allow for connections
		int xpos = x - (nextBlocks.size()-1);
		for( String uuid:nextBlocks) {
			positionNextNode(uuid,xpos,y);
			xpos += 2;
		}
	}
	
	/** 
	 * @param uuid the block being placed
	 * @param x the block's new x
	 * @param y the block's new y
	 */
	private void positionNextNode(String uuid,int x,int y) {
		GridPoint gp = gridMap.get(uuid);
		gp.x = x;
		gp.y = y;
		ConnectionHub hub = connectionMap.get(uuid);
		List<String> nextBlocks = hub.getConnectionsTo();
		if( nextBlocks.size() < 2) y = y+1;
		else                       y = y+2;  // Allow for connections
		int xpos = x - (nextBlocks.size()-1);
		for( String blockuuid:nextBlocks) {
			positionNextNode(blockuuid,xpos,y);
			xpos += 2;
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
