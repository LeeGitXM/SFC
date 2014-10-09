package com.ils.sfc.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepUI extends AbstractStepUI {
	protected static Icon messageIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/message.png"));
	protected static Icon questionIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/question.png"));
	protected static Icon clockIcon = new ImageIcon(AbstractIlsStepUI.class.getResource("/images/clock.png"));
	private JLabel label = new JLabel();
	protected enum PaletteTabs { Messages, Input, Control, Notification };
	
	protected AbstractIlsStepUI() {
    	label.setText(getText());
    	label.setBorder(new LineBorder(Color.gray, 2));
    	label.setIcon(getIcon());	
    	label.setHorizontalTextPosition(SwingConstants.LEFT);  // text is to left of icon
    	label.setHorizontalAlignment(SwingConstants.CENTER);
    	label.setVerticalAlignment(SwingConstants.CENTER);
    	label.setBackground(Color.white);
    	label.setOpaque(true);
	}
	
	/** Subclasses override to get text to display. */
	protected Icon getIcon() { return null; }
	
	/** Subclasses override to get icon to display. */
	protected String getText() { return null; }
	
    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, 
    	Graphics2D g2d) {
    	
    	// get the size of the cell:
    	double cellWidth = g2d.getClipBounds().getWidth() * g2d.getTransform().getScaleX();
    	double cellHeight = g2d.getClipBounds().getHeight() * g2d.getTransform().getScaleY();
    	
    	// Give the label a slight inset
    	int inset = 4;
    	label.setSize((int)cellWidth - 2 * inset, (int)cellHeight - 2 * inset);
    	
    	// draw connections to other steps
    	this.drawUpLink(g2d);
    	this.drawDownLink(g2d);
    	
    	// IA's drawing uses some significant scaling--remove that for our label display,
    	// but retain the translation part:
    	AffineTransform oldTransform = g2d.getTransform();
    	AffineTransform transform = new AffineTransform();
    	double tx = oldTransform.getTranslateX() + inset;
    	double ty = oldTransform.getTranslateY() + inset;
    	transform.translate(tx, ty);
    	
    	g2d.setTransform(transform);
    	label.paint(g2d);
    	g2d.setTransform(oldTransform);
    }

}
