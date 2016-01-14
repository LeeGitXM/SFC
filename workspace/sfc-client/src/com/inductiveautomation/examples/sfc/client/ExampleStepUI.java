package com.inductiveautomation.examples.sfc.client;

import javax.swing.*;

import java.awt.*;

import com.inductiveautomation.examples.sfc.common.AbstractExampleStepDelegate;
import com.inductiveautomation.examples.sfc.common.ExampleStepProperties;
import com.inductiveautomation.sfc.client.api.ChartStatusContext;
import com.inductiveautomation.sfc.client.api.ClientStepFactory;
import com.inductiveautomation.sfc.client.api.StepUI;
import com.inductiveautomation.sfc.client.ui.AbstractStepUI;
import com.inductiveautomation.sfc.elements.steps.ChartStepProperties;
import com.inductiveautomation.sfc.elements.steps.action.ActionStepProperties;
import com.inductiveautomation.sfc.rpc.ChartStatus;
import com.inductiveautomation.sfc.uimodel.ChartUIElement;

import static com.inductiveautomation.sfc.client.ui.AbstractChartElementComponent.CELL_HEIGHT;
import static com.inductiveautomation.sfc.client.ui.AbstractChartElementComponent.CELL_WIDTH;

public class ExampleStepUI extends AbstractStepUI {
    Rectangle rect = new Rectangle(12, 12, CELL_WIDTH - 23, CELL_HEIGHT - 23);
    public ExampleStepUI() {
        repaintProperties.add(ExampleStepProperties.EXAMPLE_PROPERTY);
    }

    @Override
    public void drawStep(ChartUIElement element, ChartStatusContext context, Graphics2D g) {
        ChartStatus.StepElementStatus state = context.getStepStatus(element);
        drawDownLink(g);
        drawUpLink(g);
        drawShape(g, state, 0, rect);
        drawName(g, String.format("%s (%s)",
                element.getOrDefault(ChartStepProperties.Name),
                element.getOrDefault(ExampleStepProperties.EXAMPLE_PROPERTY)));
    }

    public static final ClientStepFactory FACTORY = new ExampleClientStepFactory();

    public static final class ExampleClientStepFactory extends AbstractExampleStepDelegate implements ClientStepFactory {

        ExampleStepUI UI = new ExampleStepUI();

        Icon icon = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));
        Icon rollover = new ImageIcon(getClass().getResource("/images/auction_hammer.png"));

        public StepUI createStepUI(ChartUIElement element) {
            return UI;
        }

        @Override
        public String getCategory() {
            return "";
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
