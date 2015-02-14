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
	private final static String TAG = "StepTranslator";
	private static int UNSET = -8888;
	private  final LoggerEx log = LogUtil.getLogger(StepLayoutManager.class.getPackage().getName());
	private final Map<String,Element> blockMap;             // block by UUID
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
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
			String uuid = block.getAttribute("uuid");
			if( uuid!=null ) {
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
						String cxn = connections[jndex];
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
			}
			index++;
		}
		// Find the begin block. There can be only one.
		String beginuuid = null;
		index = 0;
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String uuid = block.getAttribute("uuid");
			if( uuid!=null ) {
				ConnectionHub hub = connectionMap.get(uuid);
				if( hub.getConnectionsFrom().isEmpty()) {
					beginuuid = uuid;
					break;
				}
			}
			index++;
		}
		if( beginuuid==null ) {
			log.errorf("%s.analyze: Chart has no begin block", TAG);
			return;
		}
		
		// Now do the layout. Position the root. Walk the tree.
		int x = 5;
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
		
	}
	
	// The original layput may create indices that are out-of-range.
	// Adjust
	private void center() {
		// First iteration gets the bounds
		
		// Make adjustments the second time through
		int max = 10;
		for( GridPoint gp:gridMap.values()) {
			if( gp.x>max ) max = gp.x;
			if( gp.y>max ) max = gp.y;
		}
		
		
		// As a bonus, we create the zoom factor.
		// The standard grid is 10x10. Factor our layout to the same physical size.
		// Consider both dimensions equally.
		this.zoom =  10./(double)max;
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
