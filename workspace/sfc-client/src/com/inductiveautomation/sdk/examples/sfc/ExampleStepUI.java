package com.inductiveautomation.sdk.examples.sfc;

import javax.swing.*;

import java.awt.*;

import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class ExampleStepUI extends AbstractStepUI {

    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, Graphics2D graphics2D) {

    }

    
    public static final ClientStepFactory FACTORY = new ExampleClientStepFactory();

    public static final class ExampleClientStepFactory extends AbstractExampleStepDelegate implements ClientStepFactory {

        ExampleStepUI UI = new ExampleStepUI();

        Icon icon = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));
        Icon rollover = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));

        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        public Icon getPaletteIcon() {
            return icon;
        }

        @Override
        public Icon getRolloverPaletteIcon() {
            return rollover;
        }

        public String getPaletteText() {
            return "Example";
        }

        public String getPaletteTooltip() {
            return "Example tooltip";
        }

        public void initializeStep(ChartUIElement element) {

        }

    }

}
