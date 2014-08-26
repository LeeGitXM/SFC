package com.ils.sfc.client;

import javax.swing.*;

import java.awt.*;

import com.ils.sfc.common.AbstractMessageQueueStepDelegate;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

public class QueueMessageStepUI extends AbstractStepUI {

    @Override
    public void drawStep(ChartUIElement propertyValues, ChartStatusContext chartStatusContext, Graphics2D graphics2D) {

    }

    
    public static final ClientStepFactory FACTORY = new QueueMessageStepFactory();

    public static final class QueueMessageStepFactory extends AbstractMessageQueueStepDelegate implements ClientStepFactory {

    	QueueMessageStepUI UI = new QueueMessageStepUI();

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
            return "Queue Message";
        }

        public String getPaletteTooltip() {
            return "Put the message in the current queue";
        }

        public void initializeStep(ChartUIElement element) {

        }

    }

}

