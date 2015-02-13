/**
 *   (c) 2015  ILS Automation. All rights reserved.
 */
package com.ils.sfc.migration.translation;

import java.util.HashMap;
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
	private  final LoggerEx log = LogUtil.getLogger(StepLayoutManager.class.getPackage().getName());
	private final Map<String,GridPoint> gridMap;   // Grid by step UUID
	
	
	/**
	 * Constructor: Immediately analyze the supplied chart.
	 * @param g2chart
	 */
	public StepLayoutManager(Document g2chart) {
		this.gridMap = new HashMap<>();
		analyze(g2chart.getElementsByTagName("block"));
	}
	
	public Map<String,GridPoint> getGridMap() { return this.gridMap; }
	// The standard grid is 10x10. Factor our layout to the same physical size.
	// Consider both dimensions equally.
	public double getZoom() {
		int max = 10;
		for( GridPoint gp:gridMap.values()) {
			if( gp.x>max ) max = gp.x;
			if( gp.y>max ) max = gp.y;
		}
		return 10./(double)max;
	}
	
	// ========================================= This is where the work gets done ================================
	private void analyze(NodeList blocks) {
		int index = 0;
		// First-time through create default entries in the grid map
		while( index < blocks.getLength()) {
			Element block = (Element)blocks.item(index);
			
			index++;
		}
	}
}
