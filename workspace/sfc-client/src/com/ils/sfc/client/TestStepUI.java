package com.ils.sfc.client;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.ils.sfc.common.AbstractTestStepDelegate;
import com.ils.sfc.common.TestStepProperties;
import com.inductiveautomation.ignition.common.config.Property;
import com.inductiveautomation.sfc.api.XMLParseException;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.rpc.ChartStatus.StepElementStatus;
import com.inductiveautomation.sfc.uimodel.ChartCompilationResults;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class TestStepUI extends AbstractStepUI {

    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, 
    	Graphics2D g2d) {
    	//Rectangle2D clipRect = g2d.getClipBounds();
    	int anim = 0;
    	Rectangle rect = (Rectangle)g2d.getClipBounds().clone();
    	rect.grow(-10,-10);
    	this.drawUpLink(g2d);
    	this.drawDownLink(g2d);
    	this.drawShape(g2d, chartStatusContext.getStepStatus(propertyValues), anim, rect);
    	this.drawName(g2d, "Test");
    }
   
    public static final ClientStepFactory FACTORY = new TestStepFactory();

    public static final class TestStepFactory extends AbstractTestStepDelegate implements ClientStepFactory {

    	TestStepUI UI = new TestStepUI();

        Icon icon = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));
        Icon rollover = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));

        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        public Icon getPaletteIcon() {
            return null; //icon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return null; // rollover;
        }

        public String getPaletteText() {
            return "Test Step";
        }

        public String getPaletteTooltip() {
            return "A test step";
        }

        public void initializeStep(ChartUIElement element) {

        }

		@Override
		public void fromXML(Element arg0, ChartUIElement arg1)
				throws XMLParseException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public List<Property<?>> getCompilationAlteringProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void toXML(XMLStreamWriter arg0, ChartUIElement arg1, String arg2)
				throws XMLStreamException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void validate(ChartUIElement arg0, ChartCompilationResults arg1) {
			// TODO Auto-generated method stub			
		}

		@Override
		public String getCategory() {
			return "EMC";
		}

		@Override
		public String getId() {
			return TestStepProperties.FACTORY_ID;
		}

    }

}

