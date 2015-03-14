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
	private final static String TAG = "ConnectionCreator";
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
		this.gridSize = computeGridDimension(gridMap);
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
			ConnectionHub hub = connectionMap.get(blockuuid);
			for(String destuuid:hub.getConnectionsTo()) {
				GridPoint destination = gridMap.get(destuuid);
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
	private GridPoint computeGridDimension(Map<String,GridPoint> gridPoints) {
		// First iteration gets the bounds
		int maxx = Integer.MIN_VALUE;
		int maxy = Integer.MIN_VALUE;

		for( GridPoint gp:gridPoints.values()) {
			if( gp.x>maxx ) maxx = gp.x;
			if( gp.y>maxy ) maxy = gp.y;
		}
		return new GridPoint(maxx,maxy);
	}
	
	/**
	 * Create links as needed between the source and destination. For 
	 * horizontal routing create links one step below the source.
	 */
	private void route(GridPoint source,GridPoint destination,ConnectionHub sourceHub,ConnectionHub destinationHub) {
		if( !isAdjacent(source,destination,sourceHub.isParallelBlock()||destinationHub.isParallelBlock())) {
			Link link = null;
			if( sourceHub!=null && sourceHub.isParallelBlock() && destinationHub.isParallelBlock()) {
				// For a straight shot between two parallel bars, take the far-right lane
				ParallelArea pa = sourceHub.getParallelArea();
				int x = source.x + pa.x2 - pa.x1 -1 ;
				int y = source.y;
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getParent(),x, y);
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
					link = linkArray.get(sourceHub.getParent(),x, y);
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
					link = linkArray.get(sourceHub.getParent(),x, y);
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
					link = linkArray.get(sourceHub.getParent(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( source.x > destination.x ) {
				// Route is left and down
				int x = source.x;
				int y = source.y;
				y++;
				// Create link directly below.
				link = linkArray.get(sourceHub.getParent(),x, y);
				link.setUp(true);
				link.setLeft(true);
				x--;
				// Draw horizontally left
				while(x > destination.x) {
					link = linkArray.get(sourceHub.getParent(),x,y);
					link.setLeft(true);
					link.setRight(true);
					x--;
				}
				// Draw corner down
				link = linkArray.get(sourceHub.getParent(),x,y);
				link.setRight(true);
				link.setDown(true);
				// Continue down to destination
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getParent(),x,y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
			}
			else if( source.x < destination.x ) {
				// Route is right and down
				int x = source.x;
				int y = source.y;
				y++;
				// Create link directly below.
				link = linkArray.get(sourceHub.getParent(),x,y);
				link.setUp(true);
				link.setRight(true);
				x++;
				// Draw horizontally right
				while(x < destination.x) {
					link = linkArray.get(sourceHub.getParent(),x,y);
					link.setLeft(true);
					link.setRight(true);
					x++;
				}
				// Draw corner down
				link = linkArray.get(sourceHub.getParent(),x,y);
				link.setLeft(true);
				link.setDown(true);
				// Continue down to destination
				y++;
				while(y<destination.y) {
					link = linkArray.get(sourceHub.getParent(),x, y);
					link.setUp(true);
					link.setDown(true);
					y++;
				}
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
			log.warnf("%s.isAdjacent: attempt to link destination above source (%d,%d),(%d,%d)",TAG,
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
			Link link = array[x][y];
			if( link==null ) {
				link = new Link(parent,x,y);
				array[x][y] = link;
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

