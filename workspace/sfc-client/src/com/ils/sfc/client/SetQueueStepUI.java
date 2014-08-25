package com.ils.sfc.client;

import javax.swing.*;

import java.awt.*;

import com.ils.sfc.common.AbstractMessageQueueStepDelegate;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class SetQueueStepUI extends AbstractStepUI {

    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, Graphics2D graphics2D) {

    }

    
    public static final ClientStepFactory FACTORY = new SetQueueStepFactory();

    public static final class SetQueueStepFactory extends AbstractMessageQueueStepDelegate implements ClientStepFactory {

    	SetQueueStepUI UI = new SetQueueStepUI();

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
            return "Set Queue";
        }

        public String getPaletteTooltip() {
            return "Set the current queue for this chart";
        }

        public void initializeStep(ChartUIElement element) {

        }

    }

}

