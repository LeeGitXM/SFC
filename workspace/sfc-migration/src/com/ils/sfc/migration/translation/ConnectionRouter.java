/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.Map;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * The initial layout does not include connections. Follow
 * logical connections for steps, insert "physical" connections
 * where needed. A "connection" occupies one spot on the grid.
 * It has elements <up>,<down>,<left>,<right>
 * 
 * The version of the grid map that we have here is absolute. For links
 * that are inside parallel zones, we need to change the locations to
 * be relative to the zone start point.
 */
public class ConnectionRouter {
	private final static String TAG = "ConnectionRouter";
	private final LoggerEx log = LogUtil.getLogger(StepLayoutManager.class.getPackage().getName());
	private final GridPoint gridSize;
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
	private final LinkArray linkArray;
	
	/**
	 * Constructor: NOTE: The gridMap and connectionMap must have the same keylists.
	 *              These are the G2 block UUID strings
	 * @param connections
	 * @param gridPoints
	 */
	public ConnectionRouter(StepLayoutManager layout) {
		this.connectionMap = layout.getConnectionMap();
		this.gridMap = layout.getGridMap();
		this.gridSize = computeGridDimension(layout);
		this.linkArray = new LinkArray(gridSize);
		createConnections();
	}
	
	/** 
	 * Traverse the layout grid, adding connections where needed.
	 * The hub contains information about parallel blocks. These
	 * require special handling.
	 */
	private void createConnections() {
		for(String blockuuid:connectionMap.keySet()) {
			GridPoint source = gridMap.get(blockuuid);
			if( !source.isConnected())  continue;
			ConnectionHub hub = connectionMap.get(blockuuid);
			for(String destuuid:hub.getConnectionsTo()) {
				GridPoint destination = gridMap.get(destuuid);
				// If the destination has never been positioned place below the source
				if( destination.x==GridPoint.UNSET ) {
					log.infof("%s.createConnections: Step %s has never been positioned.",TAG,destuuid);
					destination.x = source.x;
					destination.y = source.y+1;
				}
				route(source,destination,hub,connectionMap.get(destuuid));
			}
		}
	}
	
	/** 
	 * Convert the connections to elements of the chart.
	 * Append to the proper parent element.
	 */
	public void createLinks(Document chart) {
		linkArray.createLinkElementsInDocument(chart);
	}
	 
	// 
	/**
	 * Find the width and height
	 * @return the grid dimension
	 */
	private GridPoint computeGridDimension(StepLayoutManager layout) {
		// First iteration gets the bounds
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;

		Map<String,ConnectionHub> connectionHubMap = layout.getConnectionMap();  // Incoming/outgoing connections by UUID
		Map<String,GridPoint> gridPointMap = layout.getGridMap();             // Grid by step UUID
		for( String key: connectionMap.keySet()) {
			GridPoint gp = gridPointMap.get(key);
			ConnectionHub hub = connectionHubMap.get(key);
			if( !gp.isConnected()) continue;
			if( gp.x>maxx ) maxx = gp.x;
			if( gp.y>maxy ) maxy = gp.y;
			ParallelArea pa = hub.getParallelArea();
			if( pa!=null ) {
				int x = gp.x + pa.x2 - pa.x1 -1;
				if( x>maxx ) maxx = x;
				int y = pa.y2;
				if( y>maxy ) maxy = y;
			}
		}
		return new GridPoint(maxx,maxy);
	}
	
	/**
	 * Create links as needed between the source and destination. For 
	 * horizontal routing create links one step below the source.
	 */
	private void route(GridPoint source,GridPoint destination,ConnectionHub sourceHub,ConnectionHub destinationHub) {
		log.debugf("%s.route: %d,%d to %d,%d",TAG,source.x,source.y,destination.x,destination.y);
		if( !source.isConnected() || !destination.isConnected() ) {
			log.warnf("%s.route: Disconnected node at %d,%d or %d,%d IGNORED",TAG,source.x,source.y,destination.x,destination.y);
			return;
		}
		if( !isAdjacent(source,destination,sourceHub.isParallelBlock()||destinationHub.isParallelBlock())) {
			Link link = null;
			if( sourceHub!=null && sourceHub.isParallelBlock() && destinationHub.isParallelBlock()) {
				// For a straight shot between two parallel bars, take the far-right lane
				ParallelArea pa = sourceHub.getParallelArea();
				if( pa==null ) {
					log.errorf("%s.route: source block is parallel, but has no parallel area",TAG);
					return;
				}
				int x = source.x + pa.x2 - pa.x1 -1 ;
				int y = source.y;
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( source.x==destination.x) {
				// Route is directly below
				int x = source.x;
				int y = source.y;
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( sourceHub!=null && sourceHub.isParallelBlock() ) {
				// Route directly down from source
				int x = destination.x;
				int y = source.y;
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( destinationHub.isParallelBlock() ) {
				// Route directly down to destination
				int x = source.x;
				int y = source.y;
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( source.x > destination.x ) {
				int x = source.x;
				int y = source.y;
				y++;
				
				// Create links directly below - until two above.
				while( y<destination.y -1) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
				// Turn corner
				link = linkArray.get(sourceHub.getChartElement(),x, y);
				link.setUp(true);
				link.setLeft(true);
				x--;
				// Draw horizontally left
				while(x > destination.x) {
					link = linkArray.get(sourceHub.getChartElement(),x,y);
					link.setLeft(true);
					link.setRight(true);
					x--;
				}
				// Draw corner down
				link = linkArray.get(sourceHub.getChartElement(),x,y);
				link.setRight(true);
				link.setDown(true);
			}
			else if( source.x < destination.x ) {
				// Route is right and down
				int x = source.x;
				int y = source.y;
				y++;
				// Create links directly below - until two above.
				while(y<destination.y-1) {
					link = linkArray.get(sourceHub.getChartElement(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
				// Turn corner
				link = linkArray.get(sourceHub.getChartElement(),x,y);
				link.setUp(true);
				link.setRight(true);
				x++;
				// Draw horizontally right
				while(x < destination.x) {
					link = linkArray.get(sourceHub.getChartElement(),x,y);
					link.setLeft(true);
					link.setRight(true);
					x++;
				}
				// Draw corner down
				link = linkArray.get(sourceHub.getChartElement(),x,y);
				link.setLeft(true);
				link.setDown(true);
			}
		}
	}
	/**
	 * Determine if two grid points are adjacent. Also check for legal spacing.
	 * @param a the source
	 * @param b the destination
	 */
	private boolean isAdjacent(GridPoint a,GridPoint b, boolean isParallel) {
		boolean adjacent = false;
		if( a.x==b.x && a.y==b.y ) {
			log.warnf("%s.isAdjacent: attempt to link two steps at same address (%d,%d)",TAG,a.x,a.y);
		}
		else if( a.y > b.y ) {
			log.warnf("%s.isAdjacent: attempt to connect destination above source (%d,%d)->(%d,%d) -- link ignored",TAG,
					a.x,a.y,b.x,b.y);
			adjacent = true;  // To ignore
		}
		else if( a.y==b.y && (b.x==a.x+1 || b.x==a.x-1) ) {
			adjacent = true;
		}
		// For parallel transitions, ignore x (they will stretch to fit)
		else if( b.y==a.y+1 && isParallel ) {
			adjacent = true;
		}
		else if( a.x==b.x && b.y==a.y+1 ) {
			adjacent = true;
		}
		return adjacent;
	}
	/**
	 * A two-dimensional array of link objects. Only occupied slots
	 * are non-null. Indexing is one-based.
	 */
	private class LinkArray {
		private final int maxx;
		private final int maxy;
		private final Link[][] array;

		public LinkArray(GridPoint extreme) {
			array = new Link[extreme.x+1][extreme.y+1];
			maxx = extreme.x;
			maxy = extreme.y;
		}
		
		/*
		 * The getter guarantees that the link exists.
		 * The reason for the parent is that the link definition
		 * needs it. Different links get added to different parent elements. 
		 */
		public Link get(Element parent,int x,int y) {
			Link link = null;
			if (x>=0 && x<=maxx && y>=0 && y<=maxy ) {
				link = array[x][y];
				if( link==null ) {
					link = new Link(parent,x,y);
					array[x][y] = link;
				}
			}
			else {
				// This will eventually result in a null pointer exception
				log.errorf("%s.get: %d,%d bounds are: %d,%d",TAG,x,y,maxx,maxy);
			}
			return link;
		}
		/*
		 * Convert all non-null links to XML elements
		 * and add to the parent element
		 */
		public void createLinkElementsInDocument(Document chart) {
			for(int y=0;y<=maxy;y++) {
				for( int x=0; x<=maxx; x++) {
					Link link = array[x][y];
					if( link==null ) continue;
					Element e = link.toElement(chart);
					link.getParent().appendChild(e);
				}
			}
		}
	}
	
	private class Link {
		private boolean up   = false;
		private boolean down = false;
		private boolean right= false;
		private boolean left = false;
		private final int x;
		private final int y;
		private final Element parent;

		public Link(Element p,int xpos,int ypos) {
			this.parent = p;
			this.x = xpos;
			this.y = ypos;
		}

		public void setUp(boolean up) {this.up = up;}
		public void setDown(boolean down) {this.down = down;}
		public void setRight(boolean right) {this.right = right;}
		public void setLeft(boolean left) {this.left = left;}
		public Element getParent() { return this.parent; }
		
		public Element toElement(Document doc) {
			Element link = doc.createElement("link");
			link.setAttribute("id",UUID.randomUUID().toString());
			link.setAttribute("location",String.format("%d %d",x,y));
			if( up ) {
				Element e = doc.createElement("up");
				link.appendChild(e);
			}
			if( left ) {
				Element e = doc.createElement("left");
				link.appendChild(e);
			}
			if( right ) {
				Element e = doc.createElement("right");
				link.appendChild(e);
			}
			if( down ) {
				Element e = doc.createElement("down");
				link.appendChild(e);
			}
			return link;
		}
	}

}

