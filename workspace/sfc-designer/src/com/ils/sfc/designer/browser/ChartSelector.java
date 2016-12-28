package com.ils.sfc.designer.browser;

import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import com.ils.common.designer.WorkspaceHandler;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.sfc.SFCModule;
import com.inductiveautomation.sfc.designer.SFCDesignerHook;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;


/**
 * A control that launches a dialog on a double-click on a chart node. 
 * The action is the same for all nodes. 
 * Allow 500ms for a double-click which we do act upon. 
 */
public class ChartSelector extends ControlAdapter implements Control {
	private final DesignerContext context;
	private final WorkspaceHandler workspaceHandler;
	private final int clickCount;
	private int clicks;
	
	public ChartSelector(DesignerContext ctx,int c) {
		context = ctx;
		this.workspaceHandler = new WorkspaceHandler(context);
		clickCount = c;
		clicks = 0;
	}
	/**
	 * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if(e.isControlDown()) return;
		if( item instanceof TableNodeItem && 
			item.getInt(BrowserConstants.RESOURCE)!=BrowserConstants.NO_RESOURCE ) {
			if( clicks==0 ) {
				// We've got the correct number of clicks, 
				Timer t = new Timer("clickTimer", false);
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						if( clicks==clickCount) {
							e.consume();
							workspaceHandler.showWorkspace(WorkspaceHandler.SFC_WORKSPACE_NAME);
							SFCDesignerHook hook = (SFCDesignerHook)context.getModule(SFCModule.MODULE_ID);
							hook.getWorkspace().openChart(item.getInt(BrowserConstants.RESOURCE));
						}
						clicks = 0;
					}
				}, 300);    // Wait 1/2 of a second for click-count
			}
			clicks++;	
		}	
	}
} 
