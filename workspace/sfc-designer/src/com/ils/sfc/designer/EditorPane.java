package com.ils.sfc.designer;

/** A trivial interface for sliding panes in editors */
public interface EditorPane {
	public static java.awt.Color background = new java.awt.Color(238,238,238);	
	// become visible and do any necessary preparation
	public void activate();
}
