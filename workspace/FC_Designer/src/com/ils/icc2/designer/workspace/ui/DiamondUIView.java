/**
 *   (c) 2014  ILS Automation. All rights reserved. 
 */
package com.ils.icc2.designer.workspace.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import com.ils.icc2.designer.workspace.ProcessBlockView;

@SuppressWarnings("serial")
public class DiamondUIView extends AbstractUIView implements BlockViewUI {
	private static final int DEFAULT_HEIGHT = 60;
	private static final int DEFAULT_WIDTH  = 60;
	public DiamondUIView(ProcessBlockView view) {
		super(view,DEFAULT_WIDTH,DEFAULT_HEIGHT);
		setOpaque(false);
		initAnchorPoints();	
	}

	@Override
	protected void paintComponent(Graphics _g) {
		// Calling the super method effects an "erase".
		Graphics2D g = (Graphics2D) _g;

		// Preserve the original transform to roll back to at the end
		AffineTransform originalTx = g.getTransform();
		Color originalBackground = g.getBackground();

		// Turn on anti-aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Prepare for outlining
		float outlineWidth = OUTLINE_WIDTH;
		Stroke stroke = new BasicStroke(outlineWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		g.setStroke(stroke);
		
		// Calculate the inner area
		Rectangle ifb = new Rectangle();   // Interior, frame and border
		ifb = SwingUtilities.calculateInnerArea(this,ifb);
		// Now translate so that 0,0 is is at the inner origin
		g.translate(ifb.x, ifb.y);
		// Now leave space for stubs
		int inset = INSET;
		ifb.x += inset;
		ifb.y += inset;
		ifb.width  -= 2*inset;
		ifb.height -= 2*inset;
		// Draw the borders. Do light to the left and dark to the left.
		// Create a diamond that is within the inset and border boundaries
		int[] xlvertices = new int[] {ifb.x,               ifb.x+(ifb.width/2),ifb.x+(ifb.width/2),ifb.x };
		int[] ylvertices = new int[] {ifb.y+(ifb.height/2),ifb.y,              ifb.y+(ifb.height), ifb.y+(ifb.height/2) };
		Polygon fi = new Polygon(xlvertices,ylvertices,4);
		g.setColor(BORDER_LIGHT_COLOR);
		g.fillPolygon(fi);
		g.draw(fi);
		
		int[] xrvertices = new int[] {ifb.x+(ifb.width/2),ifb.x+ifb.width,ifb.x+(ifb.width/2),ifb.x+(ifb.width/2) };
		int[] yrvertices = new int[] {ifb.y,ifb.y+(ifb.height/2),         ifb.y+(ifb.height),ifb.y};
		fi = new Polygon(xrvertices,yrvertices,4);
		g.setColor(BORDER_DARK_COLOR);
		g.fillPolygon(fi);

		
		// Create a diamond that is within the inset and border boundaries
		ifb.x += BORDER_WIDTH;
		ifb.y += BORDER_WIDTH;
		ifb.width  -= 2*BORDER_WIDTH;
		ifb.height -= 2*BORDER_WIDTH;
		int[] xvertices = new int[] {ifb.x,ifb.x+(ifb.width/2),ifb.x+ifb.width,ifb.x+(ifb.width/2) };
		int[] yvertices = new int[] {ifb.y+(ifb.height/2),ifb.y,ifb.y+(ifb.height/2),ifb.y+(ifb.height)};
		fi = new Polygon(xvertices,yvertices,4);
		g.setColor(new Color(block.getBackground()));
		g.fillPolygon(fi);
		// Outline the inner area
		g.setPaint(INSET_COLOR);
		g.draw(fi);
		
		// Reverse any transforms we made
		g.setTransform(originalTx);
		g.setBackground(originalBackground);
		drawAnchors(g,0,0);
		drawEmbeddedIcon(g);
		drawEmbeddedText(g,0,0);
	}
}
