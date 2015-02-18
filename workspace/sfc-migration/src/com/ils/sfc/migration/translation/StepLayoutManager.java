/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private final List<Element> anchors;                    // Anchors and jumps created by this manager
	private final ArrayList<Integer> rightmostIndex;        // Rightmost index by row number 
	private final Document chart;                           // Ignition chart
	// Record the chart limits so that we can return a canvas size, if asked.
	private int minx = 0;
	private int miny = 0;
	private int maxx = 0;
	private int maxy = 0;
	private int anchorCount = 0;
	private double zoom = 1.0;
	/**
	 * Constructor: Immediately analyze the supplied chart.
	 * @param g2chart
	 */
	public StepLayoutManager(Document ichart, Document g2chart) {
		this.blockMap = new HashMap<>();
		this.connectionMap = new HashMap<>();
		this.gridMap = new HashMap<>();
		this.rightmostIndex = new ArrayList<>();
		this.anchors = new ArrayList<>();
		this.anchorCount = 0;
		this.chart = ichart;
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
		log.infof("%s.analyze: begin block is %s",TAG,beginuuid);
		// Now do the layout. Position the root. Walk the tree.
		int x = 0;   // Center on zero so that we can scale if need be.
		int y = 2;
		positionNode(null,beginuuid,x,y);
	}
	
	/** 
	 * Recursive routine to set the position of the specified node
	 * and continue on with its children.
	 * 
	 * @param uuid the block being placed
	 * @param x the block's new x
	 * @param y the block's new y
	 */
	private void positionNode(String source,String uuid,int x,int y) {

		ConnectionHub hub = connectionMap.get(uuid);
		if( blockMap.get(uuid)!=null ) {
			// NOTE: anchors and jumps are not in the block map.
			// If there are multiple inputs on a block
			// then create an anchor and associated jumps
			for(String input:hub.connectedFrom) {
				if( !input.equals(source) ) {
					x = createAnchors(source,uuid,x,y-1);
					y+=2;
					break;
				}
			}
		}
		
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
		log.infof("%s.positionNode: %s at %d,%d", TAG,uuid,x,y);
		setRightmost(x,y);
		
		List<String> nextBlocks = hub.getConnectionsTo();
		if( nextBlocks.size() < 2 ) y = y+1;
		else                       y = y+2;  // Allow for connections
		int xpos = x - (nextBlocks.size()-1);
		for( String childuuid:nextBlocks) {
			positionNode(uuid,childuuid,xpos,y);
			gp = gridMap.get(childuuid);  
			xpos = gp.x + 2;                // Position for next block
		}
	}
	
	/**
	 * Create an anchor plus a corresponding jump for every block connecting to it.
	 * Exclude the source 
	 * @param uuid target step
	 * @param x
	 * @param y
	 * @return
	 */
	private int createAnchors(String source,String uuid,int x,int y) {
		ConnectionHub hub = connectionMap.get(uuid);
		String anchoruuid = UUID.randomUUID().toString();
		List<String> from = hub.getConnectionsFrom();
		// Search all blocks connect connections to this block
		// replace with a link to the anchor.
		for(String fromid:from) {
			if(fromid.equalsIgnoreCase(source)) continue;
			ConnectionHub tohub = connectionMap.get(fromid);
			List<String> tos = tohub.getConnectionsTo();
			String jumpuuid = UUID.randomUUID().toString();
			tos.remove(uuid);
			tos.add(jumpuuid);
			Element jump = createJump(chart,jumpuuid,anchorCount);
			anchors.add(jump);
			GridPoint gp = new GridPoint(UNSET,UNSET);
			gridMap.put(jumpuuid,gp);
			ConnectionHub jumpHub = new ConnectionHub();
			jumpHub.getConnectionsFrom().add(uuid);
			connectionMap.put(jumpuuid,jumpHub);
		}
		// Now
		from.clear();
		from.add(source);
		from.add(anchoruuid);
		Element anchor = createAnchor(chart,anchoruuid,anchorCount);
		anchors.add(anchor);
		ConnectionHub anchorHub = new ConnectionHub();
		anchorHub.getConnectionsTo().add(uuid);
		connectionMap.put(anchoruuid,anchorHub);
		// We are given the y of the source block - the most compact location
		// is immediately to the right of the source block. This spot should 
		// ALWAYS be available.
		x+=1;
		GridPoint gp = new GridPoint(x,y);
		gridMap.put(anchoruuid,gp);
		anchorCount++;
		return x;
	}
	
	public List<Element> getAnchors() { return this.anchors; }
	
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
		
		// The anchors and jumps also need translating. Set their location directly on the elements
		for(Element e:anchors) {
			String uuid = e.getAttribute("id");
			GridPoint gp = gridMap.get(uuid);    // These have just been translated
			e.setAttribute("location", String.format("%d %d",gp.x,gp.y));
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
	
	private Element createAnchor(Document chart,String uuid,int count) {
		log.infof("%s.createAnchor: %s", TAG,uuid);
		Element e = chart.createElement("anchor");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
	}
	
	private Element createJump(Document chart,String uuid,int count) {
		log.infof("%s.createJump: %s", TAG,uuid);
		Element e = chart.createElement("jump");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
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
