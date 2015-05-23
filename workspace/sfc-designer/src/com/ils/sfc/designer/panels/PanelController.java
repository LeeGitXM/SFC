package com.ils.sfc.designer.panels;

import com.inductiveautomation.ignition.client.util.gui.SlidingPane;
import com.inductiveautomation.ignition.designer.model.DesignerContext;

/** An superclass for controllers that contain and switch between sliding panes */
public abstract class PanelController {
	protected SlidingPane slidingPane = new SlidingPane();
	protected DesignerContext context;
	
	public PanelController(DesignerContext context) {
		this.context = context;
	}
	
	public DesignerContext getContext() {
		return context;
	}
	
	public void slideTo(int index) {
		slidingPane.setSelectedPane(index);	
	}	
	
	public SlidingPane getSlidingPane() {
		return slidingPane;
	}
}
