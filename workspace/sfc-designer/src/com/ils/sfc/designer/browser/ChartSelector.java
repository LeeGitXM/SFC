package com.ils.sfc.designer.browser;

import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


/**
 * A control that launches a dialog on single-click on a chart node. 
 * The action is the same for all nodes. 
 * Allow 500ms for a double-click which we do NOT act upon. 
 */
public class ChartSelector extends ControlAdapter implements Control {
	private final int clickCount;
	private int clicks;
	public ChartSelector(int c) {
		clickCount = c;
		clicks = 0;
	}
    /**
     * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
	public void itemClicked(final VisualItem item, MouseEvent e) {
		if( clicks==0 ) {
			// We've got the correct number of clicks, 
			Timer t = new Timer("doubleclickTimer", false);
			t.schedule(new TimerTask() {
				@Override
			     public void run() {
			        	if( clicks==clickCount) {
			        		final JDialog editor = (JDialog)new ChartSelectionDialog(item);
			    			editor.pack();
			    			SwingUtilities.invokeLater(new Runnable() {
			    				public void run() {
			    					editor.setLocationByPlatform(true);
			    					editor.setVisible(true);
			    				}
			    			}); 
			    		}
			        	clicks = 0;
			      	}
			      }, 500);
		}
		clicks++;	
			
	}
} 
