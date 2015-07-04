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

import com.ils.sfc.migration.Converter;
import com.inductiveautomation.ignition.common.util.LogUtil;
import com.inductiveautomation.ignition.common.util.LoggerEx;
/**
 * Given a list of steps in a chart and their connections, determine
 * x-y placements.
 */
public class StepLayoutManager {
	private final static String TAG = "StepLayoutManager";
	private final LoggerEx log = LogUtil.getLogger(StepLayoutManager.class.getPackage().getName());
	private final Map<String,Element> blockMap;             // block by UUID
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
	private final Map<String,ParallelArea> parallelMap;     // Parallel areas by UUID
	private final List<Element> anchors;                    // Anchors and jumps created by this manager
	private final ArrayList<Integer> rightmostIndex;        // Rightmost index by row number 
	private final Document chart;                           // Ignition chart
	private final Converter delegate;
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
	public StepLayoutManager(Converter converter,Document g2chart,Document ichart ) {
		this.delegate = converter;
		this.blockMap = new HashMap<>();
		this.connectionMap = new HashMap<>();
		this.gridMap = new HashMap<>();
		this.parallelMap = new HashMap<>();
		this.rightmostIndex = new ArrayList<>();
		this.anchors = new ArrayList<>();
		this.anchorCount = 0;
		this.chart = ichart;
		analyze(g2chart.getElementsByTagName("block"));
		center();
	}
	public Map<String,Element>       getBlockMap() { return this.blockMap; }
	public Map<String,ConnectionHub> getConnectionMap() { return this.connectionMap; }
	public Map<String,GridPoint>     getGridMap() { return this.gridMap; }
	public double getZoom() { return this.zoom; }
	
	/**
	 * Iterate through the parallel zones and make sure that the areas cover their children.
	 * Set element properties, add element to the chart.
	 * Repeat the iteratation over children, relativize their location to the parallel block.
	 * 
	 * Ignore the terminating block.
	 */
	public void sizeParallelAreas() {
		for( String key:parallelMap.keySet() ) {
			ParallelArea pa  = parallelMap.get(key);
			Element parallel = pa.getElement();
			NodeList stepList = parallel.getChildNodes();
			int index = 0;
			int minx = Integer.MAX_VALUE;
			int miny = Integer.MAX_VALUE;
			int maxx = Integer.MIN_VALUE;
			int maxy = Integer.MIN_VALUE;
			int childCount = stepList.getLength();
			while( index<childCount ) {
				Element step = (Element)stepList.item(index);
				String loc = step.getAttribute("location");   // "x y"
				if( loc !=null ) {
					int pos = loc.indexOf(" ");
					try {
						int x = Integer.parseInt(loc.substring(0,pos));
						int y = Integer.parseInt(loc.substring(pos+1));
						if( x<minx ) minx = x;
						if( x>maxx ) maxx = x;
						if( y<miny ) miny = y;
						if( y>maxy ) maxy = y;
					}
					catch(NumberFormatException nfe) {
						log.errorf("%s.sizeParallelAreas: Error extracting locations from %s (%s)",TAG,loc,nfe.getMessage());
					}
				}
				index++;
			}
			
			// The ending parallel block will have no children, we ignore.
			if( childCount>0) {
				pa.x1 = minx;
				pa.x2 = maxx;
				pa.y1 = miny - 1;  // Make room for top bar
				pa.y2 = maxy + 1;  // Make room for bottom bar
				pa.getElement().setAttribute("location", String.format("%d %d", pa.x1,pa.y1));
				pa.getElement().setAttribute("size", String.format("%d %d", pa.x2- pa.x1+1,pa.y2-pa.y1+1));
				chart.getDocumentElement().appendChild(pa.getElement());   // Add it directly to the chart.
			}
			// Now make child locations relative to the parallel zone
			index = 0;
			while( index<childCount ) {
				Element step = (Element)stepList.item(index);
				String loc = step.getAttribute("location");   // "x y"
				if( loc !=null ) {
					int pos = loc.indexOf(" ");
					try {
						int x = Integer.parseInt(loc.substring(0,pos));
						int y = Integer.parseInt(loc.substring(pos+1));
						step.setAttribute("location", String.format("%d %d",x-pa.x1,y-pa.y1-1));
					}
					catch(NumberFormatException nfe) {
						log.errorf("%s.sizeParallelAreas: Error extracting locations from %s (%s)",TAG,loc,nfe.getMessage());
					}
				}
				index++;
			}
		}
	}
	// ========================================= This is where the work gets done ================================
	// Note: the blocklist contains transitions
	private void analyze(NodeList blocklist) {
		int index = 0;
		// First-time through create default entries in the grid map
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
			blockMap.put(uuid, block);
			gridMap.put(uuid,new GridPoint());   // Not connected yet
			log.infof("%s.analyze: %s(%s)",TAG,block.getAttribute("name"),uuid);
			ConnectionHub hub = connectionMap.get(uuid);
			if( hub==null) {
				hub = new ConnectionHub(chart.getDocumentElement());
				connectionMap.put(uuid,hub);
			}
			if(isParallel(block)) {
				Element e = createParallel(chart,uuid);
				parallelMap.put(uuid, new ParallelArea(e));
				hub.setParallelBlock(true);
			}
			NodeList connections = block.getElementsByTagName("connectedTo");
			int jndex = 0;
			while( jndex < connections.getLength() ) {
				Element connection = (Element)connections.item(jndex);
				String cxn = StepTranslator.canonicalForm(connection.getAttribute("uuid"));
				log.infof("%s.analyze: %s connected to %s",TAG,uuid,cxn);
				ConnectionHub destinationHub = connectionMap.get(cxn);
				if( destinationHub==null) {
					destinationHub = new ConnectionHub(chart.getDocumentElement());
					connectionMap.put(cxn,destinationHub);
				}
				hub.addConnectionTo(cxn);
				destinationHub.addConnectionFrom(uuid);
				jndex++;
			}
			index++;
		}
		// Find the begin block. There can be only one. Instead of relying on
		// nothing connected to it, check the class. There may be disconnected
		// block just sitting there. We ignore these.
		String beginuuid = null;
		index = 0;
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String className = block.getAttribute("class");
			if( "S88-BEGIN".equalsIgnoreCase(className)) {
				String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
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
		GridPoint root = new GridPoint(x,y);
		gridMap.put(beginuuid,root);
		positionNode(null,beginuuid,x,y);
	}
	
	/** 
	 * Recursive routine to set the position of the specified node
	 * and continue on with its children.
	 * 
	 * Note: We will meet a terminating parallel each time it is referenced 
	 *       by a block in the parallel zone. Make sure we only track its exit
	 *       once.
	 * 
	 * @param source the name of the block from which we have a connection
	 * @param uuid the block being placed
	 * @param x the block's new x
	 * @param y the block's new y
	 */
	private void positionNode(String source,String uuid,int x,int y) {
		ConnectionHub sourceHub = null;
		if( source!=null ) sourceHub = connectionMap.get(source);
		ConnectionHub hub = connectionMap.get(uuid);
		ParallelArea pa = parallelMap.get(uuid);
		
		// Multiple inputs are expected for an ending parallel block
		if( blockMap.get(uuid)!=null && sourceHub!=null &&
			(pa==null || !sourceHub.isInParallelZone()) ) {
			// NOTE: anchors and jumps are not in the block map.
			// If there are multiple inputs on a block
			// then create an anchor and associated jumps. 
			for(String input:hub.getConnectionsFrom()) {
				if( !input.equals(source) ) {
					x = createAnchors(hub,source,uuid,x,y-1);
					y+=1;  
					break;
				}
			}
		}
		
		// The mere fact that we're at this point means that we're connected
		GridPoint gp = gridMap.get(uuid);
		gp.setConnected(true);

		if( pa!=null ) {
			// We're either moving into or out of a parallel zone
			if( sourceHub.isInParallelZone() ) {
				// Now the parallel areas are the same for both  begin and end.
				// For the bottom bar, set the position to the lower right.
				pa = sourceHub.getParallelArea();
				hub.setForChart(chart.getDocumentElement());
				// This is a terminating parallel block. We will
				// see this once for every incoming connection.
				// Only process its outputs the last time.
				hub.incrementVisitCount();
				if( sourceHub.isParallelBlock() ) {
					// Straight shot through parallel zone
					pa.x2+=2;
				}
				x = (pa.x1+pa.x2)/2;   // Position in middle
				y = pa.y2;
				gp.x = pa.x2;
				gp.y = pa.y2;
				if( hub.getVisitCount()<hub.getConnectionsFrom().size() ) return;
			}
			else {
				hub.setForParallel(pa);
				// First approximation of the size. For the top bar, set the
				// grid points to the upper left.
				pa.x1 = x;
				pa.y1 = y;
				pa.x2 = x + 1;
				pa.y2 = y+2;
				gp.x = pa.x1;
				gp.y = pa.y1;
			}
		}
		// Otherwise inherit the hub from the previous
		else if(sourceHub!=null) {
			hub.setParentage(sourceHub);
			gp.x = x;
			gp.y = y;
			if( sourceHub.isInParallelZone() ) {
				pa = sourceHub.getParallelArea();
				if( pa.x2 < x ) pa.x2 = x;
				if( pa.y2 < y+1 ) pa.y2 = y+1;
			}
		}
		else {
			gp.x = x;
			gp.y = y;
		}
		
		List<String> nextBlocks = new ArrayList<String>(hub.getConnectionsTo());
		y = y+1;
		if( nextBlocks.size() >= 2 && !hub.isParallelBlock() ) y = y+1; // Allow for connections
		
		// In positioning the blocks, disregard the parallel zone.
		// Later on we will size it to cover all its children.
		// If we conflict on the left, move everything right
		int rightmost = getRightmost(y);
		if( rightmost > x-2 ) {
			int dx = rightmost - x + 2;
			x = x + dx;
			moveAncestryRight(uuid,dx);
		}

		log.infof("%s.positionNode: at %d,%d %s ", TAG,x,y,uuid);
		setRightmost(x,y);
		  
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
	private int createAnchors(ConnectionHub hub,String source,String uuid,int x,int y) {
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
			hub.getParent().appendChild(jump);
			GridPoint gp = new GridPoint();    // Not connected yet
			gridMap.put(jumpuuid,gp);
			ConnectionHub jumpHub = new ConnectionHub(hub.getParent());
			jumpHub.getConnectionsFrom().add(uuid);
			connectionMap.put(jumpuuid,jumpHub);
		}
		// Now
		from.clear();
		from.add(source);
		from.add(anchoruuid);
		Element anchor = createAnchor(chart,anchoruuid,anchorCount);
		anchors.add(anchor);
		hub.getParent().appendChild(anchor);
		ConnectionHub anchorHub = new ConnectionHub(hub.getParent());
		anchorHub.getConnectionsTo().add(uuid);
		connectionMap.put(anchoruuid,anchorHub);
		// We are given the y of the source block - the most compact location
		// is immediately to the right of the source block. This spot should 
		// ALWAYS be available.
		x+=1;
		GridPoint gp = new GridPoint(x,y);
		gridMap.put(anchoruuid,gp);
		log.infof("%s.createAnchor: %s at %d,%d",TAG,anchoruuid,x,y);
		anchorCount++;
		return x;
	}
	
	// The original layout may create indices that are out-of-range.
	// Place the diagram in the upper left corner of the space.
	// NOTE: We make no attempt to modify parallelArea objects.
	//       These are set in a sizeParallelAreas()
	private void center() {
		// First iteration gets the bounds
		minx = Integer.MAX_VALUE;
		miny = Integer.MAX_VALUE;
		maxx = Integer.MIN_VALUE;
		maxy = Integer.MIN_VALUE;
		// Ignore points that are unset
		for( GridPoint gp:gridMap.values()) {
			if( !gp.isConnected()) continue;
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
		//Ignore points that are UNSET
		for( GridPoint gp:gridMap.values()) {
			if( !gp.isConnected() ) continue;
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
					ParallelArea pa = parallelMap.get(parent);
					if( pa!=null ) {
						pa.x1 = pa.x1+dx;
						pa.x2 = pa.x2+dx;
					}
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
		log.debugf("%s.createAnchor: %c %s", TAG,'A'+count,uuid);
		Element e = chart.createElement("anchor");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
	}
	
	private Element createJump(Document chart,String uuid,int count) {
		log.debugf("%s.createJump: %c %s", TAG, 'A'+count,uuid);
		Element e = chart.createElement("jump");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
	}
	private Element createParallel(Document chart,String uuid) {
		log.debugf("%s.createParallel: %s", TAG,uuid);
		Element e = chart.createElement("parallel");
		e.setAttribute("id", uuid);
		return e;
	}
	private boolean isParallel(Element block) {
		String claz = block.getAttribute("class");
		return(delegate.getClassMapper().isParallel(claz));
	}
}
