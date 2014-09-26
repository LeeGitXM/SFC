package com.ils.sfc.client;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.ils.sfc.common.TestStepProperties;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public abstract class AbstractIlsStepUI extends AbstractStepUI {
	
    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, 
    	Graphics2D g2d) {
    	int anim = 0;
    	Rectangle rect = (Rectangle)g2d.getClipBounds().clone();
    	rect.grow(-10,-10);
    	this.drawUpLink(g2d);
    	this.drawDownLink(g2d);
    	this.drawShape(g2d, chartStatusContext.getStepStatus(propertyValues), anim, rect);
    	
    	String name = (String)propertyValues.getRawValueMap().get(TestStepProperties.Name);
    	Font oldFont = g2d.getFont();
    	Font nameFont = oldFont.deriveFont(Font.BOLD);
    	g2d.setFont(nameFont);
    	this.drawName(g2d, name);
    	g2d.setFont(oldFont);
    }

}
