package com.ils.sfc.designer.browser;

import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;


/**
 * A control that launches a dialog on selection of a chart node. 
 * The action is the same for all nodes.
 */
public class ChartSelector extends ControlAdapter implements Control {
   
    /**
     * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
     */
	public void itemClicked(VisualItem item, MouseEvent e) {
		final JDialog editor = (JDialog)new ChartSelectionDialog(item);
		editor.pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				editor.setLocationByPlatform(true);
				editor.setVisible(true);
			}
		}); 
	}

} 
