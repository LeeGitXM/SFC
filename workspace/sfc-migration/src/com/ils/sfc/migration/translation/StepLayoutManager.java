/**
 *   (c) 2015-2016  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	private final static boolean DEBUG = false;
	private final Map<String,Element> blockMap;             // block by UUID
	private final Map<String,ConnectionHub> connectionMap;  // Incoming/outgoing connections by UUID
	private final Map<String,GridPoint> gridMap;            // Grid by step UUID
	private final Map<String,ParallelArea> parallelMap;     // Parallel areas by UUID
	private final List<Element> anchors;                    // Anchors and jumps created by this manager
	private final ArrayList<Integer> rightmostIndex;        // Rightmost index by row number 
	private String beginuuid = null;
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
		this.connectionMap = new ConcurrentHashMap<>();
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
	public String getCanvasSize() {
		int width = maxx-minx+4;     // Allot a border
		int height = maxy - miny+4;
		if(width<10) width = 10;
		if(height<10) height = 10;
		return String.format("%d %d",width,height);
	}
	public Element getJumpElement(String id) {
		for(Element element:anchors) {
			String uuid = element.getAttribute("id");
			if( uuid.equalsIgnoreCase(id)) return element;
		}
		return null;
	}
	
	/**
	 * Iterate through the parallel zones and make sure that the areas cover their children.
	 * Set element properties, add element to the chart.
	 * Repeat the iteration over children, relativize their location to the parallel block.
	 * 
	 * Ignore the terminating block.
	 */
	public void sizeParallelAreas() {
		for( String key:parallelMap.keySet() ) {
			ParallelArea pa  = parallelMap.get(key);
			Element parallel = pa.getElement();
			NodeList stepList = parallel.getChildNodes();
			int childCount = stepList.getLength();
			// The ending parallel block will have no children, we ignore.
			if( childCount==0 ) continue;
			
			int minx = Integer.MAX_VALUE;
			int miny = Integer.MAX_VALUE;
			int maxx = Integer.MIN_VALUE;
			int maxy = Integer.MIN_VALUE;
			
			int index = 0;
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
			
			pa.x1 = minx;
			pa.x2 = maxx;
			pa.y1 = miny - 1;  // Make room for top bar
			pa.y2 = maxy + 1;  // Make room for bottom bar
			pa.getElement().setAttribute("location", String.format("%d %d", pa.x1,pa.y1));
			pa.getElement().setAttribute("size", String.format("%d %d", pa.x2- pa.x1+1,pa.y2-pa.y1+1));
			chart.getDocumentElement().appendChild(pa.getElement());   // Add it directly to the chart.
			if(DEBUG || log.isDebugEnabled()) log.infof("%s.sizeParallelAreas: pa %d,%d x %d,%d",TAG,pa.x1,pa.y1,pa.x2,pa.y2);
			
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
	// Analyze a list of G2 blocks
	// Note: the blocklist contains transitions
	private void analyze(NodeList blocklist) {
		int index = 0;
		// First-time through create default entries in the grid map
		while( index < blocklist.getLength()) {
			Element block = (Element)blocklist.item(index);
			String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
			blockMap.put(uuid, block);
			gridMap.put(uuid,new GridPoint());   // Not connected yet
			if(DEBUG || log.isDebugEnabled()) log.infof("%s.analyze: %s(%s)",TAG,block.getAttribute("name"),uuid);
			ConnectionHub hub = connectionMap.get(uuid);
			if( hub==null) {
				hub = new ConnectionHub(chart.getDocumentElement());
				connectionMap.put(uuid,hub);
			}
			if(isParallel(block)) {
				Element e = createParallelElement(chart,uuid);
				parallelMap.put(uuid, new ParallelArea(e));
				hub.setParallelBlock(true);
			}
			index++;
		}
		// Loop one more time, creating the connections
		index = 0;
		while( index<blocklist.getLength() ) {
			Element block = (Element)blocklist.item(index);
			String uuid = StepTranslator.canonicalForm(block.getAttribute("uuid"));
			ConnectionHub hub = connectionMap.get(uuid);
			NodeList connections = block.getElementsByTagName("connectedTo");
			int jndex = 0;
			while( jndex < connections.getLength() ) {
				Element connection = (Element)connections.item(jndex);
				String cxn = StepTranslator.canonicalForm(connection.getAttribute("uuid"));
				if(DEBUG || log.isDebugEnabled()) {
					Element to = blockMap.get(cxn);
					log.infof("%s.analyze: %s(%s) connected to %s(%s)",TAG,block.getAttribute("name"),uuid,to.getAttribute("name"),cxn);
				}
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
		index = 0;
		Element block = null;
		while( index < blocklist.getLength()) {
			block = (Element)blocklist.item(index);
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
		if(DEBUG || log.isDebugEnabled()) log.infof("%s.analyze: begin block is %s(%s)",TAG,block.getAttribute("name"),beginuuid);
		// Now do the layout. Position the root. Walk the tree.
		int x = 0;   // Center on zero, we will scale later.
		int y = 2;
		setRightmost(0,y);

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
	 * @param upstreamStepId the name of the block from which we have a connection
	 * @param stepId the block being placed
	 * @param x the block's new x
	 * @param y the block's new y
	 */
	private void positionNode(String parentId,String stepId,int x,int y) {
		ConnectionHub parentHub = null;
		if( parentId!=null ) parentHub = connectionMap.get(parentId);
		ConnectionHub stepHub = connectionMap.get(stepId);
		ParallelArea pa = parallelMap.get(stepId);
		
		// The mere fact that we're at this point means that we're connected
		GridPoint gp = gridMap.get(stepId);
		gp.setConnected(true);

		if( pa!=null ) {
			// We're either moving into or out of a parallel zone
			if( parentHub.isInParallelZone() ) {
				// Now the parallel areas are the same for both  begin and end.
				// For the bottom bar, set the position to the lower right.
				pa = parentHub.getParallelArea();
				stepHub.setChartElement(chart.getDocumentElement());
				// This is a terminating parallel block. We will
				// see this once for every incoming connection.
				// Only process its outputs the last time.
				stepHub.incrementVisitCount();
				if( parentHub.isParallelBlock() ) {
					// Straight shot through parallel zone
					pa.x2+=2;
				}
				x = (pa.x1+pa.x2)/2;   // Position in middle
				y = pa.y2;
				gp.x = pa.x2;
				gp.y = pa.y2;
				if( stepHub.getVisitCount()<stepHub.getConnectionsFrom().size() ) return;
			}
			else {
				stepHub.setForParallel(pa);
				// First approximation of the size. For the top bar, set the
				// grid points to the upper left.
				pa.x1 = x;
				pa.y1 = y;
				pa.x2 = x + 1;
				pa.y2 = y+2;
				pa.rightmost = pa.x1;
				gp.x = pa.x1;
				gp.y = pa.y1;
			}
		}
		// Otherwise inherit the hub from the previous
		else if(parentHub!=null) {
			stepHub.setParentage(parentHub); // Pass on the G2 block element
			gp.x = x;
			gp.y = y;
			if( parentHub.isInParallelZone() ) {
				pa = parentHub.getParallelArea();
				if( pa.x2 < x ) pa.x2 = x;
				if( pa.y2 < y+1 ) pa.y2 = y+1;
			}
		}
		else {
			gp.x = x;
			gp.y = y;
		}
		
		
		// In positioning the blocks, disregard the parallel boundaries.
		// Later on we will size the parallel zone to cover all its children.
		// If we conflict on the left, move everything right
		int rightmost = getRightmost(y);
		if( rightmost > x-2 ) {
			int dx = rightmost - x + 2;
			x = x + dx;
			gp.x = x;    // Move self first, then ancestors
			moveAncestryRight(stepId,dx,new ArrayList<String>());
		}
		
		// By default position one step down. From here on x,y are for next blocks ...
		y = y+1;
		if(DEBUG || log.isDebugEnabled()) {
			Element blk = blockMap.get(stepId);
			if( blk!=null )
				log.infof("%s.positionNode: at %d,%d %s(%s) ", TAG,gp.x,gp.y,blk.getAttribute("name"),stepId);
			else
				log.infof("%s.positionNode: at %d,%d NULL (%s) ", TAG,gp.x,gp.y,stepId);
		}
		setRightmost(gp.x,gp.y);
		if(pa!=null) pa.rightmost = gp.x;

		List<String> nextBlocks = new ArrayList<String>(stepHub.getConnectionsTo());
		// Attempt to center the incoming connection
		int dx = nextBlocks.size()-1;
		if( dx>0 ) {
			gp.x = gp.x+dx;    // Move self first, then ancestors
			moveAncestryRight(stepId,dx,new ArrayList<String>());
		}
		 	
		int index = 0;
		for( String childuuid:nextBlocks) {
			GridPoint childlocation = gridMap.get(childuuid);
			// If we reference a block that is already connected, and not in a parallel zone
			// then create an anchor. Multiple inputs are expected for an ending parallel block.
			if( childlocation.isConnected() && pa==null ) {
				createAnchor(stepId,childuuid);
			}
			else {
				GridPoint parentlocation = gridMap.get(stepId);  // Can change between siblings
				int dy = 1;
				if( nextBlocks.size() >= 2 && !stepHub.isParallelBlock() ) dy++; // Allow for horizontal connections
				positionNode(stepId,childuuid,parentlocation.x-dx+index*2,parentlocation.y+dy);
			} 
			index++;
		}
	}
	
	/**
	 * Create an anchor plus a corresponding jumps for every block connecting to it.  
	 * @param upstreamStepId the uuid of the upstream block
	 * @param stepId target step
	 * @param stepHub the connection hub of the block
	 * @return
	 */
	private void createAnchor(String source,String target) {
		String parent = null;  // The parent of the target block
		ConnectionHub targetHub = connectionMap.get(target);
		for(String from:targetHub.getConnectionsFrom() ) {
			// We want an existing connection that is not our target
			if( from.equals(source)) continue;
			if( gridMap.get(from).isConnected() ) {
				parent = from;
				break;
			}
		}
		
		if( parent==null ) {
			log.errorf("%s.createAnchor: Unable to find parent of target %s",TAG,target);
			return;
		}
		
		// Search all steps with connections to the target
		// replace with a jump to the new anchor.
		for(String key:connectionMap.keySet() ) {
			if( key.equals(parent)) continue;  // Leave the existing link intact
			ConnectionHub hub = connectionMap.get(key);
			List<String> targets = hub.getConnectionsTo();
			if( targets.contains(target) ) {
				// Replace with link with jump.
				String jumpuuid = UUID.randomUUID().toString();
				targets.remove(target);
				targets.add(jumpuuid);
				if(DEBUG || log.isDebugEnabled()) log.infof("%s.createAnchor: From %s, replacing link to %s with jump %s",TAG,key,target,jumpuuid);
				Element jump = createJumpElement(chart,jumpuuid,anchorCount);
				anchors.add(jump);
				hub.getChartElement().appendChild(jump);
				// Cannot position jump as parent is probably not yet positioned.
				GridPoint jumplocation = new GridPoint();
				gridMap.put(jumpuuid,jumplocation);
				ConnectionHub jumpHub = new ConnectionHub(hub.getChartElement());
				jumpHub.getConnectionsFrom().add(key);
				connectionMap.put(jumpuuid,jumpHub);
				// For our original source, we do know the location
				if( key.equals(source)) {
					GridPoint sourcelocation = gridMap.get(source);
					jumplocation.x = sourcelocation.x;
					jumplocation.y = sourcelocation.y+1;
					jumplocation.setConnected(true);
				}

			}

		}
		// Now create the anchor
		// Move all descendants of parent down one to make room for anchor connection
		moveDescendantsDown(parent,new ArrayList<String>());
		List<String> targetFrom = targetHub.getConnectionsFrom();
		targetFrom.clear();

		// Now create and position the anchor
		String anchoruuid = UUID.randomUUID().toString();
		targetFrom.add(parent);
		targetFrom.add(anchoruuid);
		Element anchor = createAnchorElement(chart,anchoruuid,anchorCount);
		anchors.add(anchor);
		targetHub.getChartElement().appendChild(anchor);
		
		ConnectionHub anchorHub = new ConnectionHub(targetHub.getChartElement());
		anchorHub.getConnectionsTo().add(target);
		connectionMap.put(anchoruuid,anchorHub);
		
		// Place the anchor immediately to the right of the parent.
		// We rely on this being a ""safe" location
		GridPoint ploc = gridMap.get(parent);
		if(ploc.isConnected()) {
			gridMap.put(anchoruuid,new GridPoint(ploc.x+1,ploc.y));
			if(DEBUG || log.isDebugEnabled()) log.infof("%s.createAnchor: %s at %d,%d",TAG,anchoruuid,ploc.x+1,ploc.y);
		}
			
		else {
			gridMap.put(anchoruuid,new GridPoint());
			log.infof("%s.createAnchor: %s UNCONNECTED",TAG,anchoruuid);
		}

		anchorCount++;
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
	/**
	 * Traverse the parentage of the specified block and 
	 * move them to the right, recursively. If the parent
	 * has multiple children, adjust the move to try and
	 * keep it centered.
	 *
	 * This depends on the fact that we have not yet placed
	 * the blocks to our right.
	 * 
	 * When entering a parallel section, simply move all the internal
	 * steps. When exiting recursively move the connecting blocks.
	 * 
	 * @param uuid the id of the source block. It has already been moved by dx.
	 * @param dx number of position to move right 
	 * @param a list of ids of elements that have already been moved.
	 *        This prevents multiple moves when exiting a parallel section.
	 */
	private void moveAncestryRight(String uuid,int dx,List<String>moved) {
		if( dx==0 ) return;                   // Nothing to do
		if( uuid.equals(beginuuid)) return;   // Hit the top
		if(DEBUG || log.isDebugEnabled()) {
			Element blk = blockMap.get(uuid);
			if( blk!=null) {
				log.infof("%s.moveAncestryRight: at %s by %d",TAG,blk.getAttribute("name"),dx);
			}
			else if(getJumpElement(uuid)!=null ) {
				// An anchor will not be in the block list
				GridPoint anchorPoint = gridMap.get(uuid);
				if( anchorPoint!=null ) {
					anchorPoint.x = anchorPoint.x + dx;
					return;
				}
				else {
					log.errorf("%s.moveAncestryRight: No grid point defined for UUID %s",TAG,uuid);
				}
			}
			else {
				log.errorf("%s.moveAncestryRight: No block defined for UUID %s",TAG,uuid);
				return;
			}
		}
		ConnectionHub hub = connectionMap.get(uuid);
		if( hub!=null && !hub.getConnectionsFrom().isEmpty()) {
			int maxchildren = 1;
			for(String parent:hub.getConnectionsFrom() ) {
				ConnectionHub parentHub = connectionMap.get(parent);
				int childcount = parentHub.getConnectionsTo().size();
				if( childcount>maxchildren ) {
					// For other than the first, move the parent only a fraction to keep centered
					maxchildren = childcount;
					int position   = parentHub.getConnectionsTo().indexOf(uuid);
					if( position>0 ) {
						dx = dx*(childcount-position)/childcount;
					}
				}
			}
			
			// Now move all ancestors the same amount
			for(String parent:hub.getConnectionsFrom() ) {
				ConnectionHub parentHub = connectionMap.get(parent);
				
				GridPoint gp = gridMap.get(parent);
				if( !gp.isConnected()) continue;
				
				ParallelArea pa = parallelMap.get(parent);
				if( pa!=null ) {
					pa.x1 = pa.x1+dx;
					pa.x2 = pa.x2+dx;
				}

				if( gp!=null && gp.isConnected() ) {
					gp.x = gp.x + dx;
					if( gp.x> getRightmost(gp.y) ) setRightmost(gp.x,gp.y);
					// Terminate if we're not going up any more, ignore steps we've seen.
					GridPoint currentPoint = gridMap.get(uuid);
					if( gp.y<currentPoint.y) moveAncestryRight(parent,dx,moved);
				}
				else {
					log.warnf("%s.moveAncestryRight: Parent %s of %s has no location.", TAG,parent,uuid);
				}
			}
		}
	}
	/**
	 * Traverse the children of the specified block and 
	 * move them down one block, recursively. This is used to make
	 * room for a connection above the parent block. Since loops are
	 * legal, we just do this until either our blocks have not been
	 * positioned, or we 
	 * 
	 * @param uuid the id of the source block. 
	 * @param a list of ids of elements that have already been moved.
	 *        This prevents multiple moves when exiting a parallel section. 
	 */
	private void moveDescendantsDown(String uuid,List<String>moved) {
		ConnectionHub hub = connectionMap.get(uuid);
		if( hub!=null && !hub.getConnectionsTo().isEmpty()) {
			GridPoint ploc = gridMap.get(uuid);
			// Now move all children down. Ignore any repeats.
			for(String child:hub.getConnectionsTo() ) {
				if( moved.contains(child)) continue;
				GridPoint gp = gridMap.get(child);
				if( !gp.isConnected() ) continue;
				if( gp.y<ploc.y-2 ) continue;
				ParallelArea pa = parallelMap.get(child);
				if( pa!=null ) {
					pa.y1 = pa.y1+1;
					pa.y2 = pa.y2+1;
				}
				if( gp!=null ) {
					gp.y = gp.y + 1;
					if(DEBUG || log.isDebugEnabled()) log.infof("%s.moveDescendantsDown: %s to %d,%d.", TAG,child,gp.x,gp.y);
					if( gp.x> getRightmost(gp.y) ) setRightmost(gp.x,gp.y);
					// If there's an attached anchor, move it also
					ConnectionHub childHub = connectionMap.get(child);
					for(String step:childHub.getConnectionsFrom()) {
						if(!step.equals(uuid) ) {
							if( connectionMap.get(step).getConnectionsFrom().isEmpty() ) {
								GridPoint ap = gridMap.get(step);
								ap.y = ap.y+1;
							}
						}
					}
					moved.add(child);
					moveDescendantsDown(child,moved);
				}
				else {
					log.warnf("%s.moveDescendantsDown: Child %s of %s has no location.", TAG,child,uuid);
				}
			}
		}
	}
	// Ignore parallel areas.
	private void setRightmost(int x,int y) {
		while( y>=rightmostIndex.size() ) {
			rightmostIndex.add(null);
		}
		if( y>=0 ) rightmostIndex.set(y,new Integer(x));
	}
	
	/**
	 * Determine the x-position of the right-most step
	 * at a y position.
	 * @param y
	 * @return
	 */
	private int getRightmost(int y) {
		int xpos = Integer.MIN_VALUE;
		if( y>=0 && y < rightmostIndex.size()) {
			Integer val = rightmostIndex.get(y);
			if( val!=null ) xpos = val.intValue();
		}
		return xpos;
	}
	
	private Element createAnchorElement(Document chart,String uuid,int count) {
		if(DEBUG || log.isDebugEnabled()) log.infof("%s.createAnchor: %c %s", TAG,'A'+count,uuid);
		Element e = chart.createElement("anchor");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
	}
	
	private Element createJumpElement(Document chart,String uuid,int count) {
		if(DEBUG || log.isDebugEnabled()) log.infof("%s.createJump: %c %s", TAG, 'A'+count,uuid);
		Element e = chart.createElement("jump");
		e.setAttribute("id", uuid);
		Node node = chart.createTextNode(String.format("%c",'A'+count));
		e.appendChild(node);
		return e;
	}
	private Element createParallelElement(Document chart,String uuid) {
		if(DEBUG || log.isDebugEnabled()) log.infof("%s.createParallel: %s", TAG,uuid);
		Element e = chart.createElement("parallel");
		e.setAttribute("id", uuid);
		return e;
	}
	private boolean isParallel(Element block) {
		String claz = block.getAttribute("class");
		return(delegate.getClassMapper().isParallel(claz));
	}
}
