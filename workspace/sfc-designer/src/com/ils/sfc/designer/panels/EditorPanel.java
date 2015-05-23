package com.ils.sfc.designer.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.inductiveautomation.ignition.designer.model.DesignerContext;

/** A superclass for sliding panes in editors */
@SuppressWarnings("serial")
public abstract class EditorPanel extends JPanel {
	public static java.awt.Color background = new java.awt.Color(238,238,238);	
	protected PanelController panelController; 
	protected int myIndex;	// index of this panel in the controller
	protected int returnIndex;  // index of panel to return to after accept action
	
	protected EditorPanel(PanelController controller, int index) {
		super(new BorderLayout());
		this.panelController = controller;
		myIndex = index;
	}

	protected DesignerContext getContext() {
		return panelController.getContext();
	}
	
	// become visible 
	// subclasses should call super.activate() after doing any necessary preparation
	public void activate(int returnIndex) {
		this.returnIndex = returnIndex;
		panelController.slideTo(myIndex);
	}
	
	public int getIndex() {
		return myIndex;		
	}
	
	public void accept() {
		panelController.slideTo(returnIndex);
		returnIndex = -1;
	}
}
