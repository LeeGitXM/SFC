package com.ils.sfc.browser;

import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;

import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;


/**
 * A control that launches a dialog on single-click on a chart node. 
 * The action is the same for all nodes. 
 * Allow 500ms for a double-click which we do NOT act upon. 
 */
public class ChartSelector extends ControlAdapter implements Control {
	private final int OFFSET = 25;
	private final DesignerContext context;
	private final int clickCount;
	private int clicks;
	public ChartSelector(DesignerContext ctx,int c) {
		context = ctx;
		clickCount = c;
		clicks = 0;
	}
	/**
	 * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if(!e.isControlDown()) return;
		if( item instanceof TableNodeItem && 
			item.getInt(BrowserConstants.RESOURCE)!=BrowserConstants.NO_RESOURCE ) {
			if( clicks==0 ) {
				// We've got the correct number of clicks, 
				Timer t = new Timer("clickTimer", false);
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						if( clicks==clickCount) {
							 SFCDesignerHook hook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
							 
							 hook.getWorkspace().openChart(item.getInt(BrowserConstants.RESOURCE));
						}
						clicks = 0;
					}
				}, 250);    // Wait 1/4 of a second for click-count
			}
			clicks++;	
		}	
	}
} 
